package com.bpellow.android.boilerplate.activity;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.bpellow.android.boilerplate.R;
import com.bpellow.android.boilerplate.activity.model.Item;
import com.bpellow.android.boilerplate.database.DBAdapter;
import com.bpellow.android.boilerplate.net.ApiProxyStub;
import com.bpellow.android.boilerplate.net.UtilsStub;
import com.bpellow.android.boilerplate.util.Preferences;

public class BaseActivity extends Activity {
	protected static Handler handler;
	protected BaseActivity self;
	
	protected ItemTask itemTask;

	DBAdapter dbAdapter;
	
	/**
	 * DIALOGS
	 */
	public static final int DIALOG_LOADING = 0;
	public static final int DIALOG_ENTER_UNAME_AND_PASS = 1;
	public static final int DIALOG_ERROR_INVALID_SIGN_IN = 2;
	public static final int DIALOG_MOST_RECENT_DEAL_UNAVAILABLE = 3;
	public static final int DIALOG_TOKEN_INVALID = 4;
	public static final int DIALOG_ERROR_SYNC_UP = 5;
	public static final int DIALOG_SYNC_BEFORE_LOGOUT = 6;
	public static final int DIALOG_ERROR_SYNCING_COMPLETELY_CONTACT_SUPPORT = 7;
	public static final int DIALOG_SYNC_UP = 8;
	public static final int DIALOG_SYNC_DOWN = 9;
	public static final int DIALOG_SIGN_IN = 10;
	public static final int DIALOG_CHECK_FORCE_UPGRADE = 11;
	public static final int DIALOG_ERROR_NETWORK = 12;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        self = this;
        handler = new BoilerplateHandler();
    }
	
    @Override
    public void onResume() {
    	super.onResume();
    	initialize();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        uninitialize();
    }
    
    public void initialize() {
    	if (dbAdapter == null) {
    		dbAdapter = new DBAdapter(this);
    		dbAdapter = dbAdapter.open();
    	}
    	ApiProxyStub.setDBAdapter(dbAdapter);
    }
    public void uninitialize() {
    	if (dbAdapter != null) {
    		dbAdapter.close();
    		dbAdapter = null;
    	}
    }
    	
	
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		String msg;
		switch(id) {
		case(DIALOG_LOADING):
			msg = getString(R.string.loading);
			dialog = new ProgressDialog(self);
			((ProgressDialog)dialog).setMessage(msg);
			break;
		case(DIALOG_SIGN_IN):
			msg = getString(R.string.dialog_sign_in);
			dialog = new ProgressDialog(self);
			((ProgressDialog)dialog).setMessage(msg);
			break;
		case(DIALOG_SYNC_UP):
			msg = getString(R.string.dialog_sync_up);
			dialog = new ProgressDialog(self);
			((ProgressDialog)dialog).setMessage(msg);
			dialog.setCancelable(false);
			break;
		case(DIALOG_SYNC_DOWN):
			msg = getString(R.string.dialog_sync_down);
			dialog = new ProgressDialog(self);
			((ProgressDialog)dialog).setMessage(msg);
			dialog.setCancelable(false);
			break;
		case(DIALOG_CHECK_FORCE_UPGRADE):
			msg = getString(R.string.dialog_check_force_upgrade);
			dialog = new ProgressDialog(self);
			((ProgressDialog)dialog).setMessage(msg);
			break;
		case(DIALOG_ENTER_UNAME_AND_PASS):
			dialog = new AlertDialog.Builder(self)
			.setMessage(R.string.error_enter_uname_and_pass)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	dialog.dismiss();
                }
            })
            .create();
			break;
		case(DIALOG_ERROR_INVALID_SIGN_IN):
			dialog = new AlertDialog.Builder(self)
			.setMessage(R.string.api_exception_invalid_sign_in)
			.setTitle(R.string.api_exception_title)
        	.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int whichButton) {
            		dialog.dismiss();
            	}
        	})
        	.create();
			break;
		case(DIALOG_MOST_RECENT_DEAL_UNAVAILABLE):
			msg = getString(R.string.api_exception_deal_unavailable);
			dialog = new AlertDialog.Builder(self)
			.setMessage(msg)
			.setTitle(R.string.api_exception_title)
    		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int whichButton) {
        			dialog.dismiss();
        			Preferences.logout(self);
        		}
    		})
    	.create();
		break;
		case(DIALOG_TOKEN_INVALID):
			msg = getString(R.string.api_exception_token_invalid);
			dialog = new AlertDialog.Builder(self)
			.setMessage(msg)
			.setTitle(R.string.api_exception_title)
        	.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int whichButton) {
            		dialog.dismiss();
            		Preferences.logout(self);
            	}
        	})
        	.create();
			break;
		case(DIALOG_ERROR_SYNC_UP):
			dialog = new AlertDialog.Builder(self)
			.setMessage(R.string.error_sync_up)
			.setTitle(R.string.api_exception_title)
        	.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int whichButton) {
            		dialog.dismiss();
            	}
        	})
        	.create();
			break;
		case(DIALOG_SYNC_BEFORE_LOGOUT):
			dialog = new AlertDialog.Builder(self)
			.setMessage(R.string.error_must_sync_before_logout)
			.setTitle(R.string.api_exception_title)
        	.setPositiveButton(R.string.cmd_sync_now, new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int whichButton) {
            		dialog.dismiss();
            		self.showDialog(DIALOG_SYNC_UP);
        	    	syncUsedItemsToServer();
        	    	self.dismissDialog(DIALOG_SYNC_UP);
        	    	if (logoutAndDeleteDatabase()) {
        	    		// just logged out and deleted database
        	    	} else {
        	    		syncUsedItemsToServer();
        	    		if (logoutAndDeleteDatabase()) {
        	    			// just logged out and deleted database
        	    		} else {
        	    			self.showDialog(DIALOG_ERROR_SYNCING_COMPLETELY_CONTACT_SUPPORT);
        	    		}
        	    	}
            	}
        	})
        	.setNegativeButton(R.string.cmd_dont_logout, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
				}
			})
        	.create();
			break;
		case(DIALOG_ERROR_SYNCING_COMPLETELY_CONTACT_SUPPORT):
			dialog = new AlertDialog.Builder(self)
			.setMessage(R.string.error_problem_syncing_completely)
			.setTitle(R.string.api_exception_title)
        	.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int whichButton) {
            		dialog.dismiss();
            	}
        	})
        	.create();
			break;
		case(DIALOG_ERROR_NETWORK):
			dialog = new AlertDialog.Builder(self)
			.setMessage(R.string.error_network)
			.setTitle(R.string.api_exception_title)
        	.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int whichButton) {
            		dialog.dismiss();
            	}
        	})
        	.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (Preferences.loggedInUser(self)) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.logged_in_menu, menu);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.item_logout:
	    	if (UtilsStub.hasNetworkConnection(self)) {
	    		if (logoutAndDeleteDatabase()) {
	    			// just logged out and deleted
	    		} else {
	    			self.showDialog(DIALOG_SYNC_BEFORE_LOGOUT);
	    		}
	    	} else { self.showDialog(DIALOG_ERROR_NETWORK); }
	    	return true;
	    case R.id.item_history:
	        goToActivity(HistoryActivity.class);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	public Boolean logoutAndDeleteDatabase() {
		Boolean isFullySynced = isFullySynced();
		if (isFullySynced) {
	    	Preferences.logout(self);
	    	dbAdapter.deleteDatabase(self);
		}
		return isFullySynced;
 	}
	
	public boolean isFullySynced() {
		return (ApiProxyStub.getUsedItemCount(Preferences.getToken(self), null) == dbAdapter.usedItemCount());
	}
	
	public void goToActivity(final Class goToClass) {
		goToActivity(goToClass, false);
	}
	
	public void goToActivity(final Class goToClass, boolean clearTop) {
		Intent intent = new Intent(self, goToClass);
		if (clearTop) {
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		self.startActivity(intent);
		if (clearTop) {
			finish();
		}
	}
     
    public void showKeyboard(final EditText ettext) {
    	ettext.requestFocus();
    	ettext.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(ettext, 0);
            }
        },200);	
    }
    
    public void hideKeyboard(final EditText ettext) {
    	ettext.requestFocus();
        ettext.postDelayed(new Runnable() {
        	@Override
        	public void run() {
            	InputMethodManager keyboard = (InputMethodManager)
            	getSystemService(Context.INPUT_METHOD_SERVICE);
            	keyboard.hideSoftInputFromWindow(ettext.getWindowToken(), 0);
        	}
        },200);	
    }
    
    public static Handler getHandler() {
    	return handler;
    }
    
	/* CUSTOM HANDLER FOR DIALOGS */
	class BoilerplateHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	super.handleMessage(msg);
        	final int code = msg.what;
        	switch(code) {
        	case DIALOG_ERROR_INVALID_SIGN_IN:
        	case DIALOG_MOST_RECENT_DEAL_UNAVAILABLE:
        	case DIALOG_TOKEN_INVALID:
        		this.post(new Runnable() { public void run() { self.showDialog(code); }});
        		break;
        	default:
        		break;
        	};
       }	
	}
	
	/* DBADAPTER METHODS */
    public void syncUsedItemsToServer() {
    	ArrayList<Item> items_to_update = dbAdapter.fetchAllUsedItems();
    	syncItemsToServer(items_to_update);
    }
    
    public void syncItemsToServer(ArrayList<Item> items_to_update) {
    	if (items_to_update.size() > 0) {
    		Log.v("syncItemsToServer", "Sending to Server: "+items_to_update.size()+", LastSync: "+(new Date()).toLocaleString());
    		if (ApiProxyStub.batchUpdateItems(Preferences.getToken(self), items_to_update, null) == true) {
    			// success condition
    		} else {
    			showDialog(DIALOG_ERROR_SYNC_UP);
    		}
    	}
    }
    
    /* AsyncTask support methods */
    public void initializeItems() {
		itemTask = new ItemTask();
		itemTask.execute((Date)null);
    }
    
    public void refreshItems() {
		itemTask = new ItemTask();
		itemTask.execute(new Date());
    }
    
    /**
     * sub-class of AsyncTask
     */
    protected class ItemTask extends AsyncTask<Date, Integer, Boolean> {
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
			showDialog(DIALOG_SYNC_DOWN);
    	}

		@Override
		protected Boolean doInBackground(Date... arg0) {
			final int BATCH_SIZE = 500;
			int count = 0;
			int total;
			Date date;
			if (arg0[0] == null) {
				date = new Date(0);
			} else {
				date = arg0[0];
			}
			total = ApiProxyStub.getItemCount(Preferences.getToken(self), date, null);
			Log.v("ItemTask", "Fetching "+total+" Items. LastSync: "+date.toLocaleString());
			while (count < total) {
				if (processItemRange(count, BATCH_SIZE, date) == true) {
					count = Math.min(count+BATCH_SIZE, total);
					final boolean finished = (count == total);
					final String msg = count+" of "+total+" processed";
					handler.post(new Runnable() {
						public void run() {
							Toast.makeText(self, msg, (finished ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG)).show();
						}
					});
				} else {
					return false;
				}
			}
			return true;
		}
		
		private boolean processItemRange(int offset, int batchSize, Date lastSync) {
			ArrayList<Item> items = ApiProxyStub.getItems(Preferences.getToken(self), offset, batchSize, lastSync, null);
			Log.v("processItemRange", "offset: "+offset+", batchSize: "+batchSize+", lastSync: "+lastSync.toLocaleString()+", numDelivered: "+items.size());
    		if (items != null && items.size() > 0) {
    			for (int i=0; i<items.size(); i++) {
    				Item item = items.get(i);
    				if (dbAdapter.itemExists(item.getId())) {
    					Log.v("FetchItems", "exists");
    					Boolean used = item.getUsed();
    					if (used != null && used == true) { // only update items if they are used, don't unuse a item from the server -- TODO: revisit this logic, should server be able to push down unused items to overwrite used ones? not yet.
    						dbAdapter.updateItem(item, used); 
    					}
    				} else {
    					Log.v("FetchItems", "insert");
    					// store items in DB
    					dbAdapter.storeItem(items.get(i));
    				}
    			}
    			return true;
    		} else if (items == null) {
				throw new RuntimeException("failed to get items");
    		}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dismissDialog(DIALOG_SYNC_DOWN);
			if (result) {
				// got items
			} else {
				// no items updated, dont record sync
			}
		}
    }
}