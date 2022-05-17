package com.jobeth.common.util;

import com.jobeth.annotion.QtIndex;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/19 11:55:55
 * Description: -
 */
public class ReflectionUtils {

    public static void main(String[] args) {
        Double d = new Double("3043258.3584");
        System.out.println(d);
    }

    public static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Object bean = SpringContextUtils.getBean(clazz);
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
                if ("java.lang.Number".equals(datatType.getSuperclass().getName())) {
                    boolean b = StringUtils.checkStrIsNumber(str);
                    if (b) filed.set(instance, dtConstru.newInstance(str));
                    else filed.set(instance, null);
                } else {
                    filed.set(instance, str);
                }
            }
        }
        return instance;
    }


    public static Map<String, Object> objToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String key = field.getName();
            Object value = field.get(obj);
            map.put(key, value);
        }
        return map;
    }
}
