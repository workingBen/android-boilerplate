package com.bpellow.android.boilerplate.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.bpellow.android.boilerplate.activity.BaseActivity;
import com.bpellow.android.boilerplate.activity.model.ForceUpgrade;
import com.bpellow.android.boilerplate.activity.model.Item;
import com.bpellow.android.boilerplate.activity.model.Token;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public class ApiProxy {
	public static final String API_VERSION = "1.0";
	public static boolean DEBUG = true;
	
	public static final int CONNECTION_TIMEOUT = 1000*15; // 15s
	public static final int SOCKET_TIMEOUT = 1000*30; // 30s
	
	//protected static final String _apiHost = "web1.tunnlr.com:11929";
	//protected static final String _apiHost = "staging.android-boilerplate.com";
	protected static final String _apiHost = "www.android-boilerplate.com";
	
	ApiProxy() {}
	
    private static String safelog(URI uri) {
        String input = uri.toString();
        if (DEBUG) return input;
        
        int index = input.indexOf('?');
        return (index>0)?input.substring(0, index):input;
    }
    
    private static String safelog(UrlEncodedFormEntity body) {
        if (DEBUG) return "";
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            body.writeTo(stream);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return stream.toString();
    }
    
    private static HashMap<String,Object> queryParams(String authToken, HashMap<String,Object> options) {
        HashMap<String,Object> params = new HashMap<String, Object>();
        if (authToken != null) params.put("auth_token", authToken);
        
        if (options != null) {
            Iterator<String> it = options.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                Object val = null;
                if (key != null && (val = options.get(key)) != null) {
                    params.put(key, val.toString());
                } else {
                    System.out.println(String.format("Invalid option key=%s val=%s", (key!=null)?key:"null", (val!=null)?val:"null"));
                }
            }
        }
        return params;
    }
    private static HashMap<String,Object> queryParams(String authToken) {
    	return queryParams(authToken, null);
    }
    private static HashMap<String,Object> queryParams(HashMap<String,Object> options) {
    	return queryParams(null, options);
    }
    
	public static String getApiVersion() {
		return API_VERSION;
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
    	if (options == null) {
    		options = new HashMap<String,Object>();
    	}
    	if (username != null) options.put("username", username);
        if (password != null) options.put("password", password);
        
        try {
        	JsonObject response = (JsonObject)apiPost("/v1/login", queryParams(options));
        	return Token.fromJSON(response.getAsJsonObject());
        } catch (ApiException ae) {
        	handleApiException(ae);
        	return null;
        }
    }
    public static Token tokenStub() {
        JsonObject testJson = (JsonObject)new JsonParser().parse("{\"authentication_token\":\"as23ase2asd63546\"}");
        return Token.fromJSON(testJson);	
    }	
    
    /* FORCE UPGRADE */
    public static ForceUpgrade isForceUpgradeRequired(String version_code, HashMap<String,Object> options) {
    	if (options == null) {
    		options = new HashMap<String,Object>();
    	}
    	if (version_code != null) options.put("version_code", version_code);
        
        try {
        	JsonObject response = (JsonObject)apiGet("/v1/force_upgrade", queryParams(options));
        	return ForceUpgrade.fromJSON(response);
        } catch (ApiException ae) {
        	handleApiException(ae);
        	return null;
        }
    }    
	
	
	/* ==== ITEMS ==== */
    public static Integer getItemCount(String auth_token, Date last_sync, HashMap<String,Object> options) {
    	if (options == null) {
    		options = new HashMap<String,Object>();
    	}
    	if (auth_token != null) options.put("token", auth_token);
    	if (last_sync != null) options.put("last_sync", (last_sync.getTime()/1000));
    	
    	try {
    		JsonObject elm = (JsonObject)apiGet("/v1/items/count", queryParams(options));
            return elm.get("count").getAsInt();
    	} catch (ApiException ae) {
    		handleApiException(ae);
    		return null;
    	}        
    }
    
    public static Integer getUsedItemCount(String auth_token, HashMap<String,Object> options) {
    	if (options == null) {
    		options = new HashMap<String,Object>();
    	}
    	if (auth_token != null) options.put("token", auth_token);
    	options.put("last_sync", (new Date(0).getTime()/1000));
    	options.put("used", "true");
    	
    	try {
    		JsonObject elm = (JsonObject)apiGet("/v1/items/count", queryParams(options));
            return elm.get("count").getAsInt();
    	} catch (ApiException ae) {
    		handleApiException(ae);
    		return null;
    	}        
    }
    
    public static ArrayList<Item> getItems(String auth_token, Integer offset, Integer batch_size, Date last_sync, HashMap<String,Object> options) {
    	if (options == null) {
    		options = new HashMap<String,Object>();
    	}
    	if (auth_token != null) options.put("token", auth_token);
    	if (offset != null) options.put("offset", offset);
    	if (batch_size != null) options.put("batch_size", batch_size);
    	if (last_sync != null) options.put("last_sync", (last_sync.getTime()/1000));
    	
    	ArrayList<Item> items = new ArrayList<Item>();
    	Gson gson = new Gson();
    	
    	try {
    		JsonArray array = (JsonArray)apiGet("/v1/items", queryParams(options));
            for(int i=0; i<array.size(); i++) {
            	Item item = Item.fromJSON((JsonObject)array.get(i));
            	items.add(item);
            }
            return items;
    	} catch (ApiException ae) {
    		handleApiException(ae);
    		return null;
    	}        
    }
    
    public static boolean batchUpdateItems(String auth_token, ArrayList<Item> items_to_update, HashMap<String,Object> options) {
    	if (options == null) {
    		options = new HashMap<String,Object>();
    	}
    	if (auth_token != null) options.put("token", auth_token);
    	
    	int count = items_to_update.size();
    	Gson gson = new Gson();
    	String items_to_update_json = "[";
    	for (int i=0; i<count; i++) {
    		items_to_update_json += gson.toJson(items_to_update.get(i)) + ((i < count-1) ? "," : "");
    	}
    	items_to_update_json += "]";
    	options.put("items_to_update", items_to_update_json);
    	
        try {
        	JsonObject response = (JsonObject)apiPost(String.format("/v1/items/batch_update", "0"), queryParams(options));
        	return true;
        } catch (ApiException ae) {
        	handleApiException(ae);
        	return false;
        }
    }
    
    /* ================================================================ */
    /* ========================== CORE ================================ */
    /* ================================================================ */
    private static Object executeParse(HttpUriRequest request) {
        int repeat = 3;
        while (true) {
            try {
                return _executeParse(request);
            } catch (IOException e) {
                // this could be due to SSL exception for some reasons
                // so try again
                if (--repeat <= 0) throw new RuntimeException(e);
            }
        }
    }
    
    private static HttpClient createClient() {
        // create timeouts
        HttpParams httpParams = new BasicHttpParams();     
        // throws java.net.ConnectTimeoutException: Socket is not connected
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
        // throws java.net.SocketTimeoutException: Read timed out
        HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT);
        
        HttpClient client = null;
//        if (!_sslValidateCert) {
            client = createUnsecureClient(httpParams);                        
//        } else {
//            // Create and initialize scheme registry 
//            SchemeRegistry schemeRegistry = new SchemeRegistry();
//            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//            schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
//
//            ClientConnectionManager cm = new SingleClientConnManager(httpParams, schemeRegistry);
//            client = new DefaultHttpClient(cm, httpParams);
//        }
        
        return client;
    }
    
	private static class AndroidSSLSocketFactory extends SSLSocketFactory {   
	    SSLContext _sslContext = SSLContext.getInstance("TLS");   
	    public AndroidSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {   
	        super(truststore);   
    	    TrustManager tm = new X509TrustManager() {   
        	    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {   
        	    }   
        	    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {   
                    for(int i=0; i<chain.length; i++) {
                        Principal p = chain[i].getSubjectDN();
                        System.out.println("\t[" + p.getName() + "]");
                        System.out.println("\t[" + chain[i].getIssuerDN().getName() + "]");
                    }
        	    }   
        	    public X509Certificate[] getAcceptedIssuers() {   
        	        return null;   
        	    }   
    	    };   
    	    _sslContext.init(null, new TrustManager[] { tm }, null);   
	    }   
	    @Override   
	    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {   
	        return _sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);   
	    }   
	    @Override   
	    public Socket createSocket() throws IOException {   
	        return _sslContext.getSocketFactory().createSocket();   
	    }
	}
    
	private static HttpClient createUnsecureClient(HttpParams params) {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());   
            trustStore.load(null, null);   
            SSLSocketFactory ssf = new AndroidSSLSocketFactory(trustStore);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            SchemeRegistry sr = new SchemeRegistry();
            sr.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));   
            sr.register(new Scheme("https", ssf, 443));
            ClientConnectionManager ccm = new SingleClientConnManager(params, sr);   
            return new DefaultHttpClient(ccm, params);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private static Object _executeParse(HttpUriRequest request) throws IOException {
        HttpClient client = null;
        try {
            client = createClient();
            
            // in case server needs authorization, do preemptively
            if (request.getURI().getUserInfo() != null) {
                String[] userInfo = request.getURI().getUserInfo().split(":");
                if (userInfo.length != 2) throw new RuntimeException("Invalid user credentials");
                Credentials credentials = new UsernamePasswordCredentials(userInfo[0], userInfo[1]);
                request.addHeader(new BasicScheme().authenticate(credentials, request));
            } 
            
            request.setHeader("Accept", "application/json");
            request.setHeader("Accept-Encoding", "gzip");

            client.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            HttpResponse response = client.execute(request); // IO
            HttpEntity entity = response.getEntity();
            
            // check response code
            int responseCode = response.getStatusLine().getStatusCode();
            // 403 is delivered by failed authenticated api requests
            if (responseCode != 200 && responseCode != 201 && responseCode != 403) {
                System.err.println("**** http error:" + responseCode);
                if (entity != null) {
                    Reader reader = new InputStreamReader(entity.getContent());
                    char[] chars = new char[3000];
                    reader.read(chars);
                    System.err.println(new String(chars));
                }
                throw new RuntimeException("HTTP ERROR:" + responseCode);
            }
            // no need to continue if no body
            if (entity == null) return null;
            
            // get access to body
            InputStream stream = entity.getContent();
            
            // add support for gzipped content
            Header contentEncoding = response.getFirstHeader("Content-Encoding");
            if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                stream = new GZIPInputStream(stream);
            }

            // parse body
            long start = System.currentTimeMillis();
//            printInputStream(stream);
            Reader reader = new InputStreamReader(stream);
            
            JsonElement elm = new JsonParser().parse(reader);
            if (elm.isJsonArray()) {
            	JsonArray root = elm.getAsJsonArray();
            	return root;
            } else {
	            JsonObject root = elm.getAsJsonObject();
	
	            if (DEBUG) {
	                System.out.println(String.format(">>>> parsed %d %s %s", (System.currentTimeMillis() - start), request.getURI().getPath(), root.toString()));
	            }
	
	            // look for an error in response body and throw exception if found
	            JsonObject error = root.getAsJsonObject("api_exception");
	            if (error != null) {
	                int code = error.getAsJsonPrimitive("code").getAsInt();
	                String message = error.getAsJsonPrimitive("message").getAsString();
	                String name = error.getAsJsonPrimitive("name").getAsString();
	                String debug = error.getAsJsonPrimitive("debug").getAsString();
	                JsonPrimitive url = error.getAsJsonPrimitive("url");
	                System.err.println("**** error returned from API:" + code + " - " + message);
	                //throw new RuntimeException(code +" "+ name +" "+ message +" "+ debug +" "+((url!=null)?url.getAsString():""));
	                throw ApiException.fromJSON(error);
	            }
	            
	            return root;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL", e);
        } catch (AuthenticationException e) {
            throw new RuntimeException("IO Exception", e);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Json Syntax Exception", e);
        } catch (JsonParseException e) {
            throw new RuntimeException("Json Parsing Exception", e);
        } finally {
            // immediate deallocation of all system resources
            if (client != null) {
                client.getConnectionManager().shutdown();
            }
        }
    }
    
    public static String readerToString(Reader in) throws IOException {

    	final char[] buffer = new char[0x10000];
    	StringBuilder out = new StringBuilder();
    	int read;
    	do {
    	  read = in.read(buffer, 0, buffer.length);
    	  if (read>0) {
    	    out.append(buffer, 0, read);
    	  }
    	} while (read>=0);
    	String ret = out.toString();
    	return ret;
}
    
    private static String extractResult(JsonObject response, String tag) {
        try {
            if (response == null) throw new JsonParseException("Invalid json response");
            JsonElement result = response.get(tag);
            if (result == null) throw new JsonParseException("Invalid json response");
            return result.getAsString();
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static List<NameValuePair> getForm(Map<String,Object> fields) {
        List<NameValuePair> form = new ArrayList<NameValuePair>();
        if (fields != null) {
	        Iterator<String> it = fields.keySet().iterator();
	        while (it.hasNext()) {
	            String key = (String) it.next();
	            Object val = null;
	            if (key != null && (val = fields.get(key)) != null) {
	                form.add(new BasicNameValuePair(key, val.toString()));
	            } else {
                    System.out.println(String.format("Invalid param key=%s val=%s", (key!=null)?key:"null", (val!=null)?val:"null"));
	            }
	        }
        }
        return form;
    }
    
    private static URI getURI(String path, String query) {
    	try {
//    		String full = (secure ? "https://" : "http://") + _apiHost + path;
    		String full = "http://" + _apiHost + path;
    	    URI uri = new URI(full);
			// we don't pass the query because it is already urlencoded
			// and we don't want to urlencode again with is what URI does
    	    String delimiter = (uri.getQuery()!=null)?"&":"?";
			return URI.create((query!=null)?uri.toString()+delimiter+query:uri.toString());
		} catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URL syntax", e);
		}
    }
    
    private static Object apiPost(String path, Map<String,Object> fields) {
        return apiCall(path, fields, "POST");
    }
    
    private static Object apiGet(String path, Map<String,Object> fields) {
        return apiCall(path, fields, "GET");
    }
    
    private static Object apiCall(String path, Map<String,Object> fields, String method) {
        
        if (method == "GET" || method == "DELETE") {
            String query = URLEncodedUtils.format(getForm(fields), HTTP.UTF_8);
            URI uri = getURI(path, query);        
            System.out.println(method + ": " + safelog(uri));
            HttpUriRequest request = (method == "GET")?new HttpGet(uri):new HttpDelete(uri);
            return executeParse(request);
        } else if (method == "POST" || method == "PUT") {
            URI uri = getURI(path, null);
            System.out.println(method + ": " + safelog(uri));
            HttpEntityEnclosingRequestBase request = (method == "POST")?new HttpPost(uri):new HttpPut(uri);
            try {
                UrlEncodedFormEntity body = new UrlEncodedFormEntity(getForm(fields));
                if (DEBUG) System.out.println("body: "+ safelog(body));
                request.setEntity(body);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Unsupported Encoding Exception", e);
            }
            return executeParse(request);
        }
        
        throw new RuntimeException("Invalid HTTP method");
    }
    
    
    /* =============================== DEBUGGING =================================*/
    private static String inputStreamToString(InputStream stream) {
    	StringBuilder builder = new StringBuilder();
    	String line = null;
    	BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    	try {
    	while((line = reader.readLine()) != null) {
    		builder.append(line).append("\n");
    	}
    	Log.e("INPUT STREAM", builder.toString());
    	return builder.toString();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return "";
    }
    
    private static void printInputStream(InputStream stream) {
    	Log.e("PRINT STREAM", inputStreamToString(stream));
    }

    
    /* =============================== API EXCEPTIONS =================================*/
    private static void handleApiException(ApiException ae) {
    	if (ae.code == ApiException.API_LOGIN_FAILED) {
    		BaseActivity.getHandler().sendEmptyMessage(BaseActivity.DIALOG_ERROR_INVALID_SIGN_IN);
    	} else if (ae.code == ApiException.API_TOKEN_INVALID) {
    		BaseActivity.getHandler().sendEmptyMessage(BaseActivity.DIALOG_TOKEN_INVALID);
    	}
    }
    
    
    /* =============================== STUBS =================================*/
    public static ArrayList<Item> itemsStub() {
    	Log.v("TIME:", "\n\n\n"+(new Date().getTime()));
    	ArrayList<Item> items = new ArrayList<Item>();
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"content\":\"Lorem Ipsum\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"content\":\"dolor sit amet\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"content\":\"consectetur adipisicing elit\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"content\":\"sed do eiusmod tempor\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"content\":\"incididunt ut labore\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"content\":\"dolore magna aliqua\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"content\":\"Ut enim ad minim veniam\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"content\":\"quis nostrud exercitation\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"content\":\"ullamco laboris\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        items.add(Item.fromJSON((JsonObject)new JsonParser().parse("{\"content\":\"nisi ut aliquip ex ea\", \"created_at\":\"2011-11-01T11:51:36-07:00\"}")));
        return items;
    }
}
