package com.bpellow.android.boilerplate.activity.model;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DateFormat {

    public static final String API_DATE_FORMAT = "yyyy-MM-dd";
    public static final String API_DATE_FORMAT_ALT = "yyyy/MM/dd";
    public static final String API_DATE_FORMAT_ALT2 = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String API_DATE_FORMAT_ALT3 = "yyyy-MM-dd HH:mm:ss";
    
    class DateDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {
            // assume it might be an integer # of seconds since epoch
            try {
                return new Date((long)(new Long(json.getAsJsonPrimitive().getAsString()))*1000);
            } catch (NumberFormatException e) {
                // don't throw and try a different format
            }
            SimpleDateFormat formatter = new SimpleDateFormat(API_DATE_FORMAT);
            try {
                return formatter.parse(json.getAsJsonPrimitive().getAsString());
            } catch (ParseException e) {
                // don't throw and try a different format
            }
            formatter = new SimpleDateFormat(API_DATE_FORMAT_ALT);
            try {
                return formatter.parse(json.getAsJsonPrimitive().getAsString());
            } catch (ParseException e) {
                // don't throw and try a different format
            }
            formatter = new SimpleDateFormat(API_DATE_FORMAT_ALT2);
            try {
                return formatter.parse(json.getAsJsonPrimitive().getAsString());
            } catch (ParseException e) {
                // re throw as Json exception?
                throw new JsonParseException(e);
            }
        }
    }

    class DateSerializer implements JsonSerializer<Date> {
        public JsonElement serialize(Date src, Type typeOfSrc,
                JsonSerializationContext context) {
            SimpleDateFormat formatter = new SimpleDateFormat(API_DATE_FORMAT);
            return new JsonPrimitive(formatter.format(src));
        }
    }
    
    
    public static Date deserializeString(String datestr) {
        SimpleDateFormat formatter = new SimpleDateFormat(API_DATE_FORMAT);
//        try {
//            return formatter.parse(datestr);
//        } catch (ParseException e) {
//            // don't throw and try a different format
//        }
//        formatter = new SimpleDateFormat(API_DATE_FORMAT_ALT);
//        try {
//            return formatter.parse(datestr);
//        } catch (ParseException e) {
//            // don't throw and try a different format
//        }
        formatter = new SimpleDateFormat(API_DATE_FORMAT_ALT2);
        try {
            return formatter.parse(datestr);
        } catch (ParseException e) {
            // don't throw and try a different format
        }
        formatter = new SimpleDateFormat(API_DATE_FORMAT_ALT3);
        try {
            return formatter.parse(datestr);
        } catch (ParseException e) {
            // re throw as Json exception?
            throw new RuntimeException(e);
        }
    }
}
