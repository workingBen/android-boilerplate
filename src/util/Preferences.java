package util;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;

import com.bpellow.android.boilerplate.activity.BaseActivity;
import com.bpellow.android.boilerplate.activity.SignInActivity;

public class Preferences {
	// this shouldn't be easily guessable, although security is not a big concern here because we're not storing sensitive data
	public static final String PREF_NAME = "AndroidBoilerplatePreferences0987";
	
	public static final String AUTH_TOKEN = "apiToken";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	
	/**
	 * put a key value pair into persistent storage
	 * detects the class type of the value and uses the corresponding put method
	 * @param ctx, @param key, @param value
	 */
	public static void put(Context ctx, String key, Object value) {
		if(ctx == null) return;
		
		SharedPreferences settings = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		if (value instanceof String) {
			editor.putString(key, (String)value);
		} else if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean)value);
		} else if (value instanceof Integer) {
			editor.putInt(key, (Integer)value);
		} else if (value instanceof Long) {
			editor.putLong(key, (Long)value);
		} else if (value instanceof Float) {
			editor.putFloat(key, (Float)value);
		}
		editor.commit();
	}
	
	/**
	 * remove a key value pair from persistent storage
	 * @param ctx, @param key
	 */
	public static void remove(Context ctx, String key) {
		if(ctx == null) return;
		
		SharedPreferences settings = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(key);
		editor.commit();
	}
	
	/**
	 * GETTERS: String, Boolean, Integer, Long, Float
	 * 
	 * @param ctx
	 * @param key
	 * @return
	 */
	public static String getString(Context ctx, String key) {
		if(ctx == null) return null;
		
		return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(key, null);
	}	
	
	/*
	 * UNSET BOOLEAN PREFERENCES ARE FALSE. Be careful of using negative keys
	 */
	public static Boolean getBoolean(Context ctx, String key) {
		if(ctx == null) return null;
		
		return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean(key, false);
	}
	
	public static Integer getInt(Context ctx, String key) {
		if(ctx == null) return null;
		
		return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(key, -1);
	}
	
	public static Long getLong(Context ctx, String key) {
		if(ctx == null) return null;
		
		return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getLong(key, -1L);
	}
	
	public static Float getFloat(Context ctx, String key) {
		if(ctx == null) return null;
		
		return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getFloat(key, -1f);
	}
	
	/**
	 * INFORMATION
	 */
	public static boolean loggedInUser(Context ctx) {
		String username = Preferences.getString(ctx, Preferences.USERNAME);
		String auth_token = Preferences.getString(ctx, Preferences.AUTH_TOKEN);
		return (Preferences.getString(ctx, Preferences.USERNAME) != null && Preferences.getString(ctx, Preferences.AUTH_TOKEN) != null);
	}
	
	public static String getToken(Context ctx) {
		return Preferences.getString(ctx, Preferences.AUTH_TOKEN);
	}
	
	public static Date epochToDate(Long epoch) {
		if (epoch == -1L) {
			epoch = 0L;
		}
		return new Date(epoch);
	}
	
	/**
	 * ACTIONS
	 */
	public static void logout(BaseActivity ctx) {
		clearUser(ctx);
		ctx.goToActivity(SignInActivity.class, true);
	}
	
	public static void clearUser(BaseActivity ctx) {
		Preferences.remove(ctx, Preferences.AUTH_TOKEN);
		Preferences.remove(ctx, Preferences.USERNAME);
		Preferences.remove(ctx, Preferences.PASSWORD);
	}
}