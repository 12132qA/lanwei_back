package org.example.Utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.Oneway;
import java.util.List;


//import java.util.logging.Logger;

public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);


    /**
     * 对象转 JSON
     *
     * */

    public static String convertObj2Json(Object object){
        return JSON.toJSONString(object);
    }

    /**
     * JSON转字符串
     *
     * */
    public static <T> T convertJson2Obj(String json,Class<T> classz){
         return JSONObject.parseObject(json,classz);
    }



    /**
     * JSON转集合对象
     *
     * */
    public static <T> List<T>  convertJsonArray2List (String json, Class<T> classz){
        return JSONObject.parseArray(json,classz);
    }



}
