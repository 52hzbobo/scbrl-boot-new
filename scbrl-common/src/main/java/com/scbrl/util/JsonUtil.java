package com.scbrl.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bruce.Liu
 * 2017-07-04 01:30
 */
public class JsonUtil {

    /**
     * 对象转Json字符串
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        return gson.toJson(object);
    }

    /**
     * 字符串转Map
     * @param json
     * @return
     */
    public static Map<String,Object> toMap(String json){
        try{
            Map<String,Object> map = new Gson().fromJson(json, new TypeToken<HashMap<String,Object>>(){}.getType());
            return map;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
