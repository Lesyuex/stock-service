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

    public static void main(String[] args) {
        Double d = new Double(1);
        System.out.println(d.getClass().getSuperclass());
    }

    public static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Object bean = SpringContextUtil.getBean(clazz);
        return bean.getClass().getDeclaredField(fieldName);
    }

    public static <T> T createDataByStrArr(String[] arr, Class<T> clazz) throws Exception {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        T instance = constructor.newInstance();
        Field[] filedArr = clazz.getDeclaredFields();
        String annValue = null;
        for (Field filed : filedArr) {
            QtIndex annotation = filed.getAnnotation(QtIndex.class);
            if (annotation != null) {
                annValue = annotation.value();
                int i = Integer.parseInt(annValue);
                filed.setAccessible(true);
                Class<?> datatType = filed.getType();
                Constructor<?> dtConstru = datatType.getConstructor(String.class);
                String str = arr[i];
                if (datatType.getSuperclass().equals("java.lang.Number")) {
                    boolean b = StringUtil.checkStrIsNumber(str);
                    if (b)filed.set(instance, dtConstru.newInstance(str));
                    else filed.set(instance, null);
                } else {
                    filed.set(instance, str);
                }
            }
        }
        return instance;
    }
}
