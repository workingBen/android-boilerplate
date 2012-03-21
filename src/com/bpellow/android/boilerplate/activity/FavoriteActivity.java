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

public class FavoriteActivity extends BaseListActivity {
	public static int MAX_FAVORITES = 20;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.favorite_items);
        super.onCreate(savedInstanceState);
    }
    
    public void initialize() {
    	super.initialize(R.layout.favorite_list_row_item);
    	refreshTitles();
    }
    
    public void refreshTitles() {
    	textviewSubtitle = (TextView)findViewById(R.id.subtitle);
    	textviewSubtitle.setText(String.format(getString(R.string.subtitle_favorite_items), itemsShowing, itemsTotal));
    }

    public ArrayList<Item> getItems() {
    	return dbAdapter.fetchFavoritedHistory();
    }
    
}