package com.bpellow.android.boilerplate.activity.model;

import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Token {
    String authentication_token;

    public Token() {}

    public static Token fromJSON(Reader stream) {
        try {
            Gson gson = new GsonBuilder()
                .create();
            return gson.fromJson(stream, Token.class);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Token fromJSON(JsonObject json) {
        try {
            if (json == null) throw new JsonParseException("invalid json");            
            Gson gson = new GsonBuilder()
                .create();
            return gson.fromJson(json, Token.class);
        } catch (JsonParseException e) {
        	throw new RuntimeException(e);
        }
    }
    
    public String getAuthToken() {
        return authentication_token;
    }

}
