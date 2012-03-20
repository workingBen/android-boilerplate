package com.bpellow.android.boilerplate.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
	public static Boolean hasNetworkConnection(Context ctx) {
	  ConnectivityManager conMgr =  (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		
	  NetworkInfo i = conMgr.getActiveNetworkInfo();
	  if (i == null)
	    return false;
	  if (!i.isConnected())
	    return false;
	  if (!i.isAvailable())
	    return false;
	  return true;
	}
}