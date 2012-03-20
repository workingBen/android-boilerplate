package database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.bpellow.android.boilerplate.activity.HistoryActivity;
import com.bpellow.android.boilerplate.activity.model.Item;

public class DBAdapter {

	// Database tables
	private static final String ITEM_TABLE = "items";
	
	// Database fields
	private static final String KEY_ID = "_id";
	private static final String KEY_CONTENT = "content";
	private static final String KEY_USED = "used";
	private static final String KEY_USED_AT = "used_at";
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

	public boolean updateItem(Item item, Boolean used) {
		if (itemIsUsed(item.getId()) != used) {
			ContentValues updateValues = createContentValues(used);

			return database.update(ITEM_TABLE, updateValues, KEY_ID + "="
				+ item.getId(), null) > 0;
		}
		return false;
	}

	
/**
	 * Return a Cursor over the list of all items in the database that are used
	 * 
	 * @return ArrayList of all used items
	 */

	public ArrayList<Item> fetchAllUsedItems() {
		Cursor c = database.query(true, ITEM_TABLE, new String[] { KEY_CONTENT,
				KEY_USED, KEY_USED_AT, KEY_ID },
				KEY_USED + "=" + "1", null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
        ArrayList<Item> used_items = new ArrayList<Item>();
        while (c.isAfterLast() == false) {
        	used_items.add(Item.fromCursor(c, false));
       	    c.moveToNext();
        }
        c.close();
		return used_items;
	}
	
	/**
	 * Return a Cursor over the list of all items in the database that are used
	 * 
	 * @return ArrayList of all used items
	 */

	public ArrayList<Item> fetchUsedHistory() {
		Cursor c = database.query(true, ITEM_TABLE, new String[] { KEY_CONTENT,
				KEY_USED, KEY_USED_AT, KEY_ID },
				KEY_USED + "= 1", null, null, null, KEY_USED_AT+" desc", String.valueOf(HistoryActivity.HISTORY_MAX));
		if (c != null) {
			c.moveToFirst();
		}
        ArrayList<Item> used_items = new ArrayList<Item>();
        while (c.isAfterLast() == false) {
        	used_items.add(Item.fromCursor(c, false));
       	    c.moveToNext();
        }
        c.close();
		return used_items;
	}

	/**
	 * Return a Cursor over the list of all items in the database that are used
	 * 
	 * @return ArrayList of all used items
	 */

	public ArrayList<Item> fetchUsedItemsSince(Long last_sync) {
		Cursor c = database.query(true, ITEM_TABLE, new String[] { KEY_CONTENT,
				KEY_USED, KEY_USED_AT, KEY_ID },
				KEY_USED + "=" + "1 and "+KEY_USED_AT+" is not null and "+KEY_USED_AT+" > "+last_sync, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
        ArrayList<Item> used_items = new ArrayList<Item>();
        while (c.isAfterLast() == false) {
        	used_items.add(Item.fromCursor(c, false));
       	    c.moveToNext();
        }
        c.close();
		return used_items;
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
	 * Is item used?
	 */	
	
	public boolean itemIsUsed(Integer _id) {
		Cursor c = database.rawQuery("select 1 from items where _id="+String.valueOf(_id)+" and used = 1;", null);
		boolean exists = (c.getCount() > 0);
		c.close();
		return exists;
	}
	
	/**
	 * How many used Items are there?
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
	 * How many used Items are there?
	 */	
	
	public Integer usedItemCount() {
		Cursor c = database.rawQuery("select count(*) as count from items where used = 1;", null);
		c.moveToFirst();
		int col = c.getColumnIndex("count");
		int count = c.getInt(col);
		c.close();
		return count;
	}
	
	/**
	 * How many used with used_at Items are there?
	 */	
	
	public Integer totalUsedHistoryCount() {
		Cursor c = database.rawQuery("select count(*) as count from items where used = 1 order by used_at desc;", null);
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
		if (item.getUsed() != null)
			values.put(KEY_USED, item.getUsed());
		if (item.getUsedAt() != null) {
			values.put(KEY_USED_AT, item.getUsedAt().getTime());
		}
		
		return values;
	}
	
	private ContentValues createContentValues(Boolean used) {
		ContentValues values = new ContentValues();
		values.put(KEY_USED, used);
		if (used) {
			values.put(KEY_USED_AT, new Date().getTime());
		}
		return values;
	}
}