package com.bcloud.fire.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class CommonUtils {

    public static Map<String, Object> populateMap(Object object) {
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                if (!Modifier.isFinal(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
                    String name = field.getName();
                    Method method = object.getClass().getDeclaredMethod("get" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
                    if (method != null) {
                        Object result = method.invoke(object);
                        if (result != null) {
                            map.put(name, result);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
