package org.usth.ict.ulake.common.misc;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonElement;

public class Utils {
//    public static Gson gsonNoExpose() {
//        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//    }
//
//    public static JsonElement addGsonString(Object o, String key, String value) {
//        JsonElement jsonElement = new Gson().toJsonTree(o);
//        jsonElement.getAsJsonObject().addProperty(key, value);
//        return jsonElement;
//    }
//
//    public static JsonElement addGsonInt(Object o, String key, Integer value) {
//        JsonElement jsonElement = new Gson().toJsonTree(o);
//        jsonElement.getAsJsonObject().addProperty(key, value);
//        return jsonElement;
//    }
//
//    public static JsonElement addGsonBoolean(Object o, String key, Boolean value) {
//        JsonElement jsonElement = new Gson().toJsonTree(o);
//        jsonElement.getAsJsonObject().addProperty(key, value);
//        return jsonElement;
//    }
    
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
