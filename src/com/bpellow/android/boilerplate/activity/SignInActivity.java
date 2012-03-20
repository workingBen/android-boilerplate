package com.bpellow.android.boilerplate.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bpellow.android.boilerplate.R;
import com.bpellow.android.boilerplate.activity.model.Token;
import com.bpellow.android.boilerplate.net.ApiProxyStub;
import com.bpellow.android.boilerplate.util.Preferences;

public class SignInActivity extends BaseActivity {
	private EditText inputUsername;
	private EditText inputPassword;
	private Button signinBtn;
	
	private LoginTask loginTask;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        
        initialize();
    }
    
    public void initialize() {
    	inputUsername = (EditText)findViewById(R.id.username);
    	inputPassword = (EditText)findViewById(R.id.password);
    	signinBtn = (Button)findViewById(R.id.sign_in_btn);
    	showKeyboard(inputUsername);
    	
    	signinBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (inputUsername.getText().length() > 0 && inputPassword.getText().length() > 0) {
					attemptSuccessfulLogin();
				} else {
					// error, please enter a username and password dialog
					showDialog(DIALOG_ENTER_UNAME_AND_PASS);
				}
			}
		});
    }
    
    public void attemptSuccessfulLogin() {
    	if (loginTask == null) {
			loginTask = new LoginTask();
			loginTask.execute(self);
		}
    }
    
    /**
     * sub-class of AsyncTask
     */
    protected class LoginTask extends AsyncTask<Context, Integer, Boolean> {
    	String username;
    	String password;
    	Token auth_token;
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
			username = inputUsername.getText().toString();
			password = inputPassword.getText().toString();
			showDialog(DIALOG_SIGN_IN);
    	}

		@Override
		protected Boolean doInBackground(Context... arg0) {
			try {
    		Token auth_token = ApiProxyStub.getAuthToken(username, password, null);
    		if (auth_token != null && auth_token.getAuthToken() != null) {
    			Preferences.put(self, Preferences.AUTH_TOKEN, auth_token.getAuthToken());
    			Preferences.put(self, Preferences.USERNAME, username);
    			Preferences.put(self, Preferences.PASSWORD, password);
    			return true;
    		}
			} catch (Exception e) {
				e.printStackTrace();
				showDialog(self.DIALOG_ERROR_NETWORK);
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dismissDialog(DIALOG_SIGN_IN);
			if (result) {
				// goto deal page
				self.goToActivity(MenuActivity.class, true);
			} else {
				loginTask = null;
			}
		}
    }
}