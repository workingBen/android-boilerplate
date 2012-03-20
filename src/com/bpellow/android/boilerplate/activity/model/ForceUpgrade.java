package com.bpellow.android.boilerplate.activity.model;

import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ForceUpgrade {
    Boolean force_upgrade;
    String url;

    public ForceUpgrade() {}

    public static ForceUpgrade fromJSON(Reader stream) {
        try {
            Gson gson = new GsonBuilder()
                .create();
            return gson.fromJson(stream, ForceUpgrade.class);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static ForceUpgrade fromJSON(JsonObject json) {
        try {
            if (json == null) throw new JsonParseException("invalid json");            
            Gson gson = new GsonBuilder()
                .create();
            return gson.fromJson(json, ForceUpgrade.class);
        } catch (JsonParseException e) {
        	throw new RuntimeException(e);
        }
    }
    
    public Boolean requireForceUpgrade() {
        return force_upgrade;
    }
    public String url() {
    	return url;
    }

}
