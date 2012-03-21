package com.bpellow.android.boilerplate.net;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.bpellow.android.boilerplate.activity.BaseActivity;
import com.bpellow.android.boilerplate.activity.model.ForceUpgrade;
import com.bpellow.android.boilerplate.activity.model.Item;
import com.bpellow.android.boilerplate.activity.model.Token;
import com.bpellow.android.boilerplate.database.DBAdapter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ApiProxyStub {
	public static final String API_VERSION = "1.0";
	public static boolean DEBUG = true;
	
	public static final int CONNECTION_TIMEOUT = 1000*15; // 15s
	public static final int SOCKET_TIMEOUT = 1000*30; // 30s
	
	//protected static final String _apiHost = "web1.tunnlr.com:11929";
	//protected static final String _apiHost = "staging.android-boilerplate.com";
	protected static final String _apiHost = "www.android-boilerplate.com";
	
	ApiProxyStub() {}
	
	public static String getApiVersion() {
		return API_VERSION;
	}
	
	public static void setDBAdapter(DBAdapter dbAdapter) {
		StubConfig.dbAdapter = dbAdapter;
	}
	static class StubConfig {
		static boolean force_upgrade = false;
		static DBAdapter dbAdapter;
	}
	
    /* ================================================================ */
    /* ======================= API METHODS ============================ */
    /* ================================================================ */
	
	/*
	 * 
	 * USER
	 *  
	 */
    public static Token getAuthToken(String username, String password, HashMap<String,Object> options) {
        return tokenStub();
    }
    
    /* FORCE UPGRADE */
    public static ForceUpgrade isForceUpgradeRequired(String version_code, HashMap<String,Object> options) {
    	stubNetworkDelay();
    	return forceUpgradeStub();
    }    
	
	
	/* ==== ITEMS ==== */
    public static Integer getItemCount(String auth_token, Date last_sync, HashMap<String,Object> options) {
    	return StubConfig.dbAdapter.localItemCount();
    }
    
    public static Integer getUsedItemCount(String auth_token, HashMap<String,Object> options) {
    	return StubConfig.dbAdapter.usedItemCount();
    }
    
    public static ArrayList<Item> getItems(String auth_token, Integer offset, Integer batch_size, Date last_sync, HashMap<String,Object> options) {
    	return itemsStub();
    }
    
    public static boolean batchUpdateItems(String auth_token, ArrayList<Item> items_to_update, HashMap<String,Object> options) {
    	return true;
    }
    
    /* =============================== API EXCEPTIONS =================================*/
    private static void handleApiException(ApiException ae) {
    	if (ae.code == ApiException.API_LOGIN_FAILED) {
    		BaseActivity.getHandler().sendEmptyMessage(BaseActivity.DIALOG_ERROR_INVALID_SIGN_IN);
    	} else if (ae.code == ApiException.API_TOKEN_INVALID) {
    		BaseActivity.getHandler().sendEmptyMessage(BaseActivity.DIALOG_TOKEN_INVALID);
    	}
    }
    
    /* =============================== NETWORK STUBS =================================*/
    public static void stubNetworkDelay() {
    	try {
    		Thread.sleep(2000);
    	} catch (Exception e) {}
    }
    
    /* =============================== JSON STUBS =================================*/
    public static Token tokenStub() {
        JsonObject testJson = (JsonObject)new JsonParser().parse("{\"authentication_token\":\"as23ase2asd63546\"}");
        return Token.fromJSON(testJson);	
    }	
    public static ForceUpgrade forceUpgradeStub() {
    	String json = StubConfig.force_upgrade ? 
    			"{\"force_upgrade\":\"true\", \"url\":\"upgradeurl.com\"}" : 
    			"{\"force_upgrade\":\"false\"}";
        JsonObject testJson = (JsonObject)new JsonParser().parse(json);
        return ForceUpgrade.fromJSON(testJson);	
    }	
    public static ArrayList<Item> itemsStub() {
    	ArrayList<Item> items = new ArrayList<Item>();
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"id\":5, \"content\":\"Item 5\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"id\":6, \"content\":\"Item 6\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"id\":7, \"content\":\"Item 7\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"id\":8, \"content\":\"Item 8\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"id\":9, \"content\":\"Item 9\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.addAll(usedItemsStub());
        return items;
    }
    public static ArrayList<Item> usedItemsStub() {
    	ArrayList<Item> items = new ArrayList<Item>();
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"id\":0, \"content\":\"Item 0\", \"used\":\"true\", \"used_at\":\"2011-11-01T11:51:36-07:00\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"id\":1, \"content\":\"Item 1\", \"used\":\"true\", \"used_at\":\"2011-11-01T11:51:36-07:00\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"id\":2, \"content\":\"Item 2\", \"used\":\"true\", \"used_at\":\"2011-11-01T11:51:36-07:00\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"id\":3, \"content\":\"Item 3\", \"used\":\"true\", \"used_at\":\"2011-11-01T11:51:36-07:00\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"id\":4, \"content\":\"Item 4\", \"used\":\"true\", \"used_at\":\"2011-11-01T11:51:36-07:00\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        return items;
    }
}
