package com.skype.jenkins;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JsonUtil {

    private static final String EQUALS = "\\u003d";

    private static Gson gsonWithNulls = new GsonBuilder().serializeNulls().create();
    private static Gson gson = new GsonBuilder().create();

    private JsonUtil() {
    }

    public static String toJson(final Object obj) {
        return toJson(obj, true);
    }

    public static String toJson(final Object obj, final boolean serializeNulls) {
        final String json;
        if (serializeNulls) {
            json = gsonWithNulls.toJson(obj);
        } else {
            json = gson.toJson(obj);
        }
        /*
         * Escaping equals sign is not necessary
         */
        return json.replace(EQUALS, "=");
    }


    public static <T> T fromJson(final String json, final Class<T> type) {
        return gson.fromJson(json, type);
    }

    public static <T> T fromJson(final String json, final Type type) {
        return gson.fromJson(json, type);
    }

    public static String prettyPrintJson(final Object obj) {
        return prettyPrintJson(toJson(obj));
    }

}