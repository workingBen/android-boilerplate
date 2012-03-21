package com.bpellow.android.boilerplate.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bpellow.android.boilerplate.activity.FavoriteActivity;
import com.bpellow.android.boilerplate.activity.model.Item;
import com.bpellow.android.boilerplate.net.ApiProxyStub;

public class DBAdapter {

	// Database tables
	private static final String ITEM_TABLE = "items";
	
	// Database fields
	private static final String KEY_ID = "_id";
	private static final String KEY_CONTENT = "content";
	private static final String KEY_USED = "favorited";
	private static final String KEY_USED_AT = "favorited_at";
	private Context context;
	private SQLiteDatabase database;
	private DBOpenHelper dbHelper;

	public DBAdapter(Context context) {
		this.context = context;
	}

	public DBAdapter open() throws SQLException {
		if (dbHelper == null) {
			dbHelper = new DBOpenHelper(context);
			database = dbHelper.getWritableDatabase();
			seedDatabase();
		} 
		return this;
	}

	public void close() {
		dbHelper.close();
		database.close();
	}
	
	public Boolean deleteDatabase(Context ctx) {
		return ctx.deleteDatabase(DBOpenHelper.DATABASE_NAME);
	}
	
	/** SEED THE DATABASE ONLY IN STUB APPS! **/
	public void seedDatabase() {
		if (localItemCount() == 0) {
			Log.d("DBAdapter", "Database has no items. Seeding.");
			Iterator<Item> seedIterator = ApiProxyStub.getItems(null, null, null, null, null).iterator();
			while(seedIterator.hasNext()) {
				storeItem(seedIterator.next());
			}
		} else {
			Log.d("DBAdapter", "Database has already been seeded.");
		}
	}

	
/**
	 * Save a new item. If the item is successfully created return the new
	 * rowId for that item, otherwise return a -1 to indicate failure.
	 */

	public long storeItem(Item item) {
		ContentValues initialValues = createContentValues(item);

		return database.insert(ITEM_TABLE, null, initialValues);
	}

	
/**
	 * Update the item
	 */

	public boolean updateItem(Item item, Boolean favorited) {
		if (itemIsFavorited(item.getId()) != favorited) {
			ContentValues updateValues = createContentValues(favorited);

			return database.update(ITEM_TABLE, updateValues, KEY_ID + "="
				+ item.getId(), null) > 0;
		}
		return false;
	}

	/**
	 * Return a Cursor over the list of all items in the database that are favorited
	 * 
	 * @return ArrayList of all favorited items
	 */

	public ArrayList<Item> fetchAllItems() {
		Cursor c = database.query(true, ITEM_TABLE, new String[] { KEY_CONTENT,
				KEY_USED, KEY_USED_AT, KEY_ID },
				null, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
        ArrayList<Item> favorited_items = new ArrayList<Item>();
        while (c.isAfterLast() == false) {
        	favorited_items.add(Item.fromCursor(c, false));
       	    c.moveToNext();
        }
        c.close();
		return favorited_items;
	}
	
/**
	 * Return a Cursor over the list of all items in the database that are favorited
	 * 
	 * @return ArrayList of all favorited items
	 */

	public ArrayList<Item> fetchAllFavoritedItems() {
		Cursor c = database.query(true, ITEM_TABLE, new String[] { KEY_CONTENT,
				KEY_USED, KEY_USED_AT, KEY_ID },
				KEY_USED + "=" + "1", null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
        ArrayList<Item> favorited_items = new ArrayList<Item>();
        while (c.isAfterLast() == false) {
        	favorited_items.add(Item.fromCursor(c, false));
       	    c.moveToNext();
        }
        c.close();
		return favorited_items;
	}
	
	/**
	 * Return a Cursor over the list of all items in the database that are favorited
	 * 
	 * @return ArrayList of all favorited items
	 */

	public ArrayList<Item> fetchFavoritedHistory() {
		Cursor c = database.query(true, ITEM_TABLE, new String[] { KEY_CONTENT,
				KEY_USED, KEY_USED_AT, KEY_ID },
				KEY_USED + "= 1", null, null, null, KEY_USED_AT+" desc", String.valueOf(FavoriteActivity.MAX_FAVORITES));
		if (c != null) {
			c.moveToFirst();
		}
        ArrayList<Item> favorited_items = new ArrayList<Item>();
        while (c.isAfterLast() == false) {
        	favorited_items.add(Item.fromCursor(c, false));
       	    c.moveToNext();
        }
        c.close();
		return favorited_items;
	}

	/**
	 * Return a Cursor over the list of all items in the database that are favorited
	 * 
	 * @return ArrayList of all favorited items
	 */

	public ArrayList<Item> fetchFavoritedItemsSince(Long last_sync) {
		Cursor c = database.query(true, ITEM_TABLE, new String[] { KEY_CONTENT,
				KEY_USED, KEY_USED_AT, KEY_ID },
				KEY_USED + "=" + "1 and "+KEY_USED_AT+" is not null and "+KEY_USED_AT+" > "+last_sync, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
        ArrayList<Item> favorited_items = new ArrayList<Item>();
        while (c.isAfterLast() == false) {
        	favorited_items.add(Item.fromCursor(c, false));
       	    c.moveToNext();
        }
        c.close();
		return favorited_items;
	}
	
/**
	 * Return a Item constructed from a cursor
	 */

	public Item fetchItem(String content) throws SQLException {
		Cursor mCursor = database.query(true, ITEM_TABLE, new String[] { KEY_CONTENT,
				KEY_USED, KEY_USED_AT, KEY_ID },
				KEY_CONTENT + "=" + "?", new String[]{content}, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return Item.fromCursor(mCursor);
	}

	/**
	 * Does a item exist?
	 */	
	
	public boolean itemExists(Integer _id) {
		Cursor c = database.rawQuery("select 1 from items where _id="+String.valueOf(_id)+";", null);
		boolean exists = (c.getCount() > 0);
		c.close();
		return exists;
	}
	
	/**
	 * Is item favorited?
	 */	
	
	public boolean itemIsFavorited(Integer _id) {
		Cursor c = database.rawQuery("select 1 from items where _id="+String.valueOf(_id)+" and favorited = 1;", null);
		boolean exists = (c.getCount() > 0);
		c.close();
		return exists;
	}
	
	/**
	 * How many favorited Items are there?
	 */	
	
	public Integer localItemCount() {
		Cursor c = database.rawQuery("select count(*) as count from items;", null);
		c.moveToFirst();
		int col = c.getColumnIndex("count");
		int count = c.getInt(col);
		c.close();
		return count;
	}
	
	/**
	 * How many favorited Items are there?
	 */	
	
	public Integer favoritedItemCount() {
		Cursor c = database.rawQuery("select count(*) as count from items where favorited = 1;", null);
		c.moveToFirst();
		int col = c.getColumnIndex("count");
		int count = c.getInt(col);
		c.close();
		return count;
	}
	
	/**
	 * How many favorited with favorited_at Items are there?
	 */	
	
	public Integer totalFavoritedHistoryCount() {
		Cursor c = database.rawQuery("select count(*) as count from items where favorited = 1 order by favorited_at desc;", null);
		c.moveToFirst();
		int col = c.getColumnIndex("count");
		int count = c.getInt(col);
		c.close();
		return count;
	}
	
	private ContentValues createContentValues(Item item) {
		ContentValues values = new ContentValues();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
		
		values.put(KEY_CONTENT, item.getContent());
		values.put(KEY_ID, item.getId());
		if (item.getFavorited() != null)
			values.put(KEY_USED, item.getFavorited());
		if (item.getFavoritedAt() != null) {
			values.put(KEY_USED_AT, item.getFavoritedAt().getTime());
		}
		
		return values;
	}
	
	private ContentValues createContentValues(Boolean favorited) {
		ContentValues values = new ContentValues();
		values.put(KEY_USED, favorited);
		if (favorited) {
			values.put(KEY_USED_AT, new Date().getTime());
		}
		return values;
	}
}

/**
	TO DEBUG: 
	`adb -s emulator-5554 shell`
	`sqlite3 /data/data/com.bpellow.android.boilerplate/databases/android_boilerplate_database`
**/