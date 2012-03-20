package com.bpellow.android.boilerplate.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import com.bpellow.android.boilerplate.R;
import com.bpellow.android.boilerplate.activity.model.ForceUpgrade;
import com.bpellow.android.boilerplate.net.ApiProxyStub;
import com.bpellow.android.boilerplate.net.UtilsStub;
import com.bpellow.android.boilerplate.util.Preferences;
import com.bpellow.android.boilerplate.util.RunnableUtils;

public class SplashActivity extends BaseActivity {
	private final int SPLASH_DELAY = 3000;
	private String versionName;
	private String versionCode;
	private ForceUpgradeTask forceUpgradeTask = new ForceUpgradeTask();
		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        try {
        	PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        	TextView vers = (TextView)findViewById(R.id.version);
        	versionName = pinfo.versionName;
        	versionCode = String.valueOf(pinfo.versionCode);
        	vers.setText(versionName);
        } catch (PackageManager.NameNotFoundException pe) {
        	// do nothing
        }
        
        if (UtilsStub.hasNetworkConnection(self)) {
        	forceUpgradeTask.execute((Context)null);
        } else {
        	progressToNextActivity();
        }
    }
    
    public void progressToNextActivity() {
        Runnable signInRunnable = RunnableUtils.startActivityRunnableFactory(this, SignInActivity.class, true);
        Runnable mainMenuRunnable = RunnableUtils.startActivityRunnableFactory(this, MenuActivity.class, true);
        
        // after a splash delay, go to the logged out selector or current deal
        if (Preferences.loggedInUser(this)) {
        	handler.postDelayed(mainMenuRunnable, SPLASH_DELAY);
        } else {
        	handler.postDelayed(signInRunnable, SPLASH_DELAY);
        }
    }
   
    public void installForceUpgrade(String apkUrl) {
    	try {
    		URL url = new URL(apkUrl);
    		HttpURLConnection c = (HttpURLConnection) url.openConnection();
    		c.setRequestMethod("GET");
    		c.setDoOutput(true);
    		c.connect();

    		String PATH = Environment.getExternalStorageDirectory() + "/download/";
    		File file = new File(PATH);
    		file.mkdirs();
    		File outputFile = new File(file, "boilderplate_update.apk");
    		FileOutputStream fos = new FileOutputStream(outputFile);
    		InputStream is = c.getInputStream();
    		
    		byte[] buffer = new byte[1024];
    		int len1 = 0;
    		while ((len1 = is.read(buffer)) != -1) {
    			fos.write(buffer, 0, len1);
    		}
    		
    		fos.close();
    		is.close();//till here, it works fine - .apk is download to my sdcard in download file
    		
    		Intent promptInstall = new Intent(Intent.ACTION_VIEW);
    		promptInstall.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "boilerplate_update.apk")), "application/vnd.android.package-archive");
    		startActivity(promptInstall);  
    	} catch (IOException e) {
    		Toast.makeText(getApplicationContext(), "Update error!", Toast.LENGTH_LONG).show();
    	}
    }  
    
    	
    	
    /**
     * sub-class of AsyncTask
     */
    protected class ForceUpgradeTask extends AsyncTask<Context, Integer, Boolean> {
    	private ForceUpgrade forceUpgrade;
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
			showDialog(DIALOG_CHECK_FORCE_UPGRADE);
    	}

		@Override
		protected Boolean doInBackground(Context... arg0) {
			try {
				forceUpgrade = ApiProxyStub.isForceUpgradeRequired(versionCode, null);
				return forceUpgrade.requireForceUpgrade();
			} catch (Exception e) {
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dismissDialog(DIALOG_CHECK_FORCE_UPGRADE);
			if (result) {
				String url = forceUpgrade.url();
				// do force upgrade to this url
				installForceUpgrade(url);
			} else {
				progressToNextActivity();
			}
		}
    }
    
}