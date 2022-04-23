package com.jobeth.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 12:45:45
 * Description: -
 */
public class JsonUtil {
    /**
     * 格式化输出JSON字符串
     * @return 格式化后的JSON字符串
     */
    public static void toPrettyFormat(Object obj) {
        String pretty = JSON.toJSONString(obj, SerializerFeature.PrettyFormat,
                SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullListAsEmpty);
        System.out.println(pretty );
    }
}
