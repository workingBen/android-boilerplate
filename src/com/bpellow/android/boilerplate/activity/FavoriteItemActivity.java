package com.bpellow.android.boilerplate.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bpellow.android.boilerplate.R;
import com.bpellow.android.boilerplate.activity.model.Item;

public class FavoriteItemActivity extends BaseActivity {
	private EditText item_input;
	private LinearLayout info_bar;
	private TextView results_textview;
	private String inputValue = "#";
	private Button key1;
	private Button key2;
	private Button key3;
	private Button key4;
	private Button key5;
	private Button key6;
	private Button key7;
	private Button key8;
	private Button key9;
	private Button key0;
	private Button keyEnter;
	private ImageButton keyDel;
	private OnClickListener keyClickListener;
	private OnClickListener keyDeleteClickListener;
	private OnClickListener keyEnterClickListener;
	private Boolean invalidInput = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.use_item);
        
        initialize();
        switchKeyboardOff();
        setupKeyboardHandlers();
    }
    
    public void successResult(String result) {
    	info_bar.setBackgroundResource(R.color.lightgreen);
    	setResult(result);
    }    
    public void errorResult(String result) {
    	info_bar.setBackgroundResource(R.color.lightred);
    	setResult(result);
    }
    public void infoResult(String result) {
    	info_bar.setBackgroundResource(R.color.lightblue);
    	setResult(result);
    }
    public void setResult(String result) {
    	results_textview.setText(result);
    }
    public void setInputValue(String v) {
    	item_input.setText(v);
    }
    public void reportInvalidInput(Boolean tooLong) {
    	invalidInput = true;
    	if (tooLong) {
    		errorResult(self.getString(R.string.input_max));
    	} else {
    		errorResult(self.getString(R.string.input_min));
    	}
    }
    public void clearResults() {
    	invalidInput = false;
    	setResult("");
    	info_bar.setBackgroundResource(android.R.color.transparent);
    }
    
    public void inputKey(String c) {
    	if (inputValue.length() == 2) { inputValue += c; inputValue += '-'; }
    	else if (inputValue.length() == 3) { inputValue += '-'; inputValue += c;}
    	else if (inputValue.length() <= 6) { inputValue += c; }
    	else { reportInvalidInput(true); }
    	setInputValue(inputValue);
    }
    public void inputKeyDelete() {
    	if (invalidInput) { clearResults(); }
    	if (inputValue.length() == 4) { // remove an extra character so the '-' gets deleted
    		inputValue = inputValue.substring(0, inputValue.length()-2);
    	} else if (inputValue.length() > 1) {
    		inputValue = inputValue.substring(0, inputValue.length()-1);
    	}
    	setInputValue(inputValue);
    }
    
    public void attemptUseItem(String itemId) {
    	if (invalidInput) { clearResults(); }
    	if (itemId.length() < 5) {
    		reportInvalidInput(false);
    	} else {
    		Item item = dbAdapter.fetchItem(itemId);
    		if (item == null) {
    			errorResult(String.format(self.getString(R.string.error_item_not_found), "#"+itemId));
    		} else if (item.getFavorited()) {
    			errorResult(String.format(self.getString(R.string.error_item_already_redeemed), "#"+itemId));
    		} else {
    			dbAdapter.updateItem(item, true);
    			successResult(String.format(self.getString(R.string.redeem_success), inputValue));
    		}
    		resetInputValue();
    	}
    }
    
    public void resetInputValue() {
    	item_input.setText("#");
    	inputValue = "#";
    }
    
    public void setupKeyboardHandlers() {
        keyClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				inputKey((String)((Button)arg0).getText());
			}
		};
        keyDeleteClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				inputKeyDelete();
			}
		};
        keyEnterClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				attemptUseItem(inputValue.replaceFirst("#", ""));
			}
		};
		key1.setOnClickListener(keyClickListener);
		key2.setOnClickListener(keyClickListener);
		key3.setOnClickListener(keyClickListener);
		key4.setOnClickListener(keyClickListener);
		key5.setOnClickListener(keyClickListener);
		key6.setOnClickListener(keyClickListener);
		key7.setOnClickListener(keyClickListener);
		key8.setOnClickListener(keyClickListener);
		key9.setOnClickListener(keyClickListener);
		key0.setOnClickListener(keyClickListener);
		keyDel.setOnClickListener(keyDeleteClickListener);
		keyEnter.setOnClickListener(keyEnterClickListener);
    }
    
    public void initialize() {
    	super.initialize();
    	
        item_input = (EditText)findViewById(R.id.item_input);
        info_bar = (LinearLayout)findViewById(R.id.info_bar);
        results_textview = (TextView)findViewById(R.id.results_textview);
		key1 = (Button)findViewById(R.id.key1);
		key2 = (Button)findViewById(R.id.key2);
		key3 = (Button)findViewById(R.id.key3);
		key4 = (Button)findViewById(R.id.key4);
		key5 = (Button)findViewById(R.id.key5);
		key6 = (Button)findViewById(R.id.key6);
		key7 = (Button)findViewById(R.id.key7);
		key8 = (Button)findViewById(R.id.key8);
		key9 = (Button)findViewById(R.id.key9);
		key0 = (Button)findViewById(R.id.key0);
		keyEnter = (Button)findViewById(R.id.keyEnter);
		keyDel = (ImageButton)findViewById(R.id.keyDel);	
    }
    
    public void switchKeyboardOff() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(item_input.getWindowToken(), 0);
        
        item_input.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});	
    }

}