package com.jobeth.util;

import com.jobeth.annotion.QtIndex;
import com.jobeth.vo.StockDetailVo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/19 11:55:55
 * Description: -
 */
public class ReflectionUtils {

    public static Field findField(Class<?> clazz,String fieldName) throws NoSuchFieldException {
        Object bean = SpringContextUtil.getBean(clazz);
        return bean.getClass().getDeclaredField(fieldName);
    }

    public static <T> T createDataByStrArr(String[] arr,Class<T> clazz) throws Exception {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        T t = constructor.newInstance();
        Field[] filedArr = clazz.getDeclaredFields();
        for (Field filed : filedArr) {
            QtIndex annotation = filed.getAnnotation(QtIndex.class);
            String value = null;
            if (annotation != null) {
                value = annotation.value();
                int i = Integer.parseInt(value);
                filed.setAccessible(true);
                filed.set(t,arr[i]);
            }
        }
        return t;
    }
}