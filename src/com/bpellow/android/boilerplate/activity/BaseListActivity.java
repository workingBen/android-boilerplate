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

abstract class BaseListActivity extends BaseActivity {
	protected TextView textviewSubtitle;
	protected ListView listviewHistory;
	protected ListAdapter itemListviewAdapter;
	
	protected int itemsShowing = 0;
	protected int itemsTotal = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }
    
    public void initialize() {
    	super.initialize();
    	textviewSubtitle = (TextView)findViewById(R.id.subtitle);
    	refreshSubtitle();
    	listviewHistory = (ListView)findViewById(R.id.list_history);
    	itemListviewAdapter = new ItemAdapter(self, R.layout.list_row_item, getItems());
    	listviewHistory.setAdapter(itemListviewAdapter);
    }
    
    abstract ArrayList<Item> getItems();
    
    public void refreshSubtitle() {
    	textviewSubtitle.setText(String.format(getString(R.string.subtitle_all_items), itemsShowing));
    }
    
    private class ItemAdapter extends ArrayAdapter<Item> {
    	Activity context;
        private ArrayList<Item> items;

        public ItemAdapter(Context context, int textViewResourceId, ArrayList<Item> items) {
                super(context, textViewResourceId, items);
                this.items = items;
                itemsShowing = items.size();
                itemsTotal = dbAdapter.totalUsedHistoryCount();
                refreshSubtitle();
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.list_row_item, null);
                }
                Item item = items.get(position);
                if (item != null) {
                        TextView c = (TextView) v.findViewById(R.id.content);
                        TextView ua = (TextView) v.findViewById(R.id.used_at);
                        TextView undo = (TextView) v.findViewById(R.id.undo);
                        undo.setTag(String.valueOf(position));
                        undo.setContentDescription(item.getContent());
                        
                        if (c != null) { c.setText("#"+item.getContent()); }
                        if (ua != null) { ua.setText("Used: "+ item.getUsedAtAsString()); }
                        if (undo != null) { 
                        	undo.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(final View v) {
									// show confirm
									AlertDialog dialog = new AlertDialog.Builder(self)
									.setMessage(String.format(getString(R.string.dialog_confirm_undo), v.getContentDescription()))
						            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						                public void onClick(DialogInterface dialog, int whichButton) {
						                	dialog.dismiss();
						                	int position = Integer.parseInt((String)v.getTag());
						                	Item item = items.get(position);
						                	self.dbAdapter.updateItem(item, false);
						                	items.remove(position);
						                	((ItemAdapter)itemListviewAdapter).notifyDataSetChanged();
						                }
						            })
						            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											dialog.dismiss();
										}
									})
						            .create();	
									dialog.show();
								}
							});
                        }
                }
                return v;
        }
    }
    
    public ArrayList<Item> allItems() {
    	return dbAdapter.fetchAllItems();
    }
    
}