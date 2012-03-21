package com.bpellow.android.boilerplate.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bpellow.android.boilerplate.R;
import com.bpellow.android.boilerplate.activity.model.Item;

public class ListActivity extends BaseListActivity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.all_items);
        super.onCreate(savedInstanceState);
    }
    
    public void initialize() {
    	super.initialize(R.layout.list_row_item);
    	refreshSubtitle();
    }
    
    public void refreshSubtitle() {
    	textviewSubtitle.setText(String.format(getString(R.string.subtitle_all_items), itemsShowing));
    }
    
    public ArrayList<Item> getItems() {
    	return dbAdapter.fetchAllItems();
    }
    
}