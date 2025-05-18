package utils;

import com.google.gson.Gson;

public class Json{

    private static Gson gson = new Gson();

    
    /**
     * Converte um objeto Java para String JSON
     * 
     * @param obj
     * @return String
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * Converte uma String JSON para um objeto da classe especificada
     * 
     * @param <T>
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

}