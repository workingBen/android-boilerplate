package com.bpellow.android.boilerplate.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bpellow.android.boilerplate.R;

public class MenuActivity extends BaseActivity {
	private Button menuBtn1;
	private Button menuBtn2;
	private Button menuBtn3;
	private Button menuBtn4;
	private Button menuBtn5;
	private Button menuBtn6;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        
        initialize();
    }
    
    public void initialize() {
    	super.initialize();
    	menuBtn1 = (Button)findViewById(R.id.menu_btn_1);
    	menuBtn2 = (Button)findViewById(R.id.menu_btn_2);
    	menuBtn3 = (Button)findViewById(R.id.menu_btn_3);
    	menuBtn4 = (Button)findViewById(R.id.menu_btn_4);
    	menuBtn5 = (Button)findViewById(R.id.menu_btn_5);
    	menuBtn6 = (Button)findViewById(R.id.menu_btn_6);
    	
    	menuBtn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				self.goToActivity(ListActivity.class);
			}
		});
    	menuBtn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				self.goToActivity(FavoriteActivity.class);
			}
		});
    	menuBtn3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				self.goToActivity(FavoriteItemActivity.class);
			}
		});
    }   
}