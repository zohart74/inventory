package org.tennez.inventory.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.tennez.inventory.Item;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final String ITEMS_TABLE_CREATE_STMT = "CREATE TABLE IF NOT EXISTS \""+InventoryDbSchema.ITEMS_TABLE_NAME+"\"("+"" +
            "\""+InventoryDbSchema.ITEM_NAME_COL+"\" Text not null, "+
            "\""+InventoryDbSchema.STORE_SECTION_COL+"\" Text not null, "+
            "\""+InventoryDbSchema.STORAGE_SECTION_COL+"\" Text not null, "+
            "\""+InventoryDbSchema.REQUIRED_COL+"\" INTEGER not null);";

    private static final String STORAGE_SECTIONS_TABLE_CREATE_STMT = "CREATE TABLE IF NOT EXISTS \""+InventoryDbSchema.STORAGE_SECTIONS_TABLE_NAME+"\"("+"" +
            "\""+InventoryDbSchema.STORAGE_NAME_COL+"\" Text not null, "+
            "\""+InventoryDbSchema.SECTION_INDEX_COL+"\" INTEGER not null);";

    private static final String SHOPPING_CART_TABLE_CREATE_STMT = "CREATE TABLE IF NOT EXISTS \""+InventoryDbSchema.SHOPPING_CART_TABLE_NAME+"\"("+"" +
            "\""+InventoryDbSchema.CART_ITEM_NAME_COL+"\" Text not null);";

    private static final String ITEMS_TABLE_DROP_STMT = "DROP TABLE IF EXISTS "+InventoryDbSchema.ITEMS_TABLE_NAME;
    private static final String STORAGE_SECTIONS_TABLE_DROP_STMT = "DROP TABLE IF EXISTS "+InventoryDbSchema.STORAGE_SECTIONS_TABLE_NAME;
    private static final String SHOPPING_CART_TABLE_DROP_STMT = "DROP TABLE IF EXISTS "+InventoryDbSchema.SHOPPING_CART_TABLE_NAME;

    private static InventoryDbHelper inventoryDatabaseHelper;
    public static InventoryDbHelper getRichContentDatabaseHelper(Context context) {
        if (inventoryDatabaseHelper == null)
            inventoryDatabaseHelper = new InventoryDbHelper(context);
        return inventoryDatabaseHelper;
    }

    public InventoryDbHelper(Context context) {
        super(context, InventoryDbSchema.DB_NAME, null, InventoryDbSchema.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ITEMS_TABLE_CREATE_STMT);
        db.execSQL(STORAGE_SECTIONS_TABLE_CREATE_STMT);
        db.execSQL(SHOPPING_CART_TABLE_CREATE_STMT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ITEMS_TABLE_DROP_STMT);
        db.execSQL(STORAGE_SECTIONS_TABLE_DROP_STMT);
        db.execSQL(SHOPPING_CART_TABLE_DROP_STMT);
        onCreate(db);
    }

    public List<Item> getAllItems() {
        Cursor wrapped = getReadableDatabase().query(InventoryDbSchema.ITEMS_TABLE_NAME, null, null, null, null, null, null);
        ItemsCursor cursor = new ItemsCursor(wrapped);
        int numOfItems = cursor.getCount();
        List<Item> items = new ArrayList<Item>(numOfItems);
        cursor.moveToFirst();
        for(int i = 0; i < numOfItems ; ++i) {
            items.add(cursor.getItem());
            cursor.moveToNext();
        }
        return items;
    }

    public List<String> getAllStorageSections() {
        Cursor wrapped = getReadableDatabase().query(InventoryDbSchema.STORAGE_SECTIONS_TABLE_NAME, null, null, null, null, null, InventoryDbSchema.SECTION_INDEX_COL+" asc");
        ItemsCursor cursor = new ItemsCursor(wrapped);
        int numOfSections = cursor.getCount();
        List<String> sections = new ArrayList<String>(numOfSections);
        cursor.moveToFirst();
        for(int i = 0; i < numOfSections ; ++i) {
            String sectionName = cursor.getString(cursor.getColumnIndex(InventoryDbSchema.STORAGE_NAME_COL));
            Log.e("IVTD","Reading storage section: "+sectionName);
            sections.add(sectionName);
            cursor.moveToNext();
        }
        return sections;
    }

    public boolean updateStorageSections(List<String> sections) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + InventoryDbSchema.STORAGE_SECTIONS_TABLE_NAME);
        for(int i = 0; i< sections.size() ; ++i) {
            Log.e("IVTD","Adding storage section: "+sections.get(i));
            ContentValues cv = new ContentValues();
            cv.put(InventoryDbSchema.STORAGE_NAME_COL, sections.get(i));
            cv.put(InventoryDbSchema.SECTION_INDEX_COL, i);
            db.insert(InventoryDbSchema.STORAGE_SECTIONS_TABLE_NAME, null, cv);
        }
        return true;

    }

    public boolean addItem(Item item) {
        Log.e("IVTD","adding item: "+item.getName());
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(InventoryDbSchema.ITEM_NAME_COL, item.getName());
        cv.put(InventoryDbSchema.STORAGE_SECTION_COL, item.getStorageSection());
        cv.put(InventoryDbSchema.STORE_SECTION_COL, item.getStoreSection());
        cv.put(InventoryDbSchema.REQUIRED_COL, item.isRequired() ? 1 : 0);
        db.insert(InventoryDbSchema.ITEMS_TABLE_NAME, null, cv);
        return true;
    }

    public boolean updateItem(String currentItemName, Item updatedItem) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + InventoryDbSchema.ITEMS_TABLE_NAME + " SET " + InventoryDbSchema.ITEM_NAME_COL + " = ?, " + InventoryDbSchema.STORAGE_SECTION_COL + " =?, " + InventoryDbSchema.STORE_SECTION_COL + " = ? WHERE " + InventoryDbSchema.ITEM_NAME_COL + " = ?", new Object[]{updatedItem.getName(), updatedItem.getStorageSection(), updatedItem.getStoreSection(), currentItemName});
        return true;
    }

    public boolean deleteItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + InventoryDbSchema.ITEMS_TABLE_NAME + " WHERE " + InventoryDbSchema.ITEM_NAME_COL + " = ?", new String[]{item.getName()});
        return true;
    }

    public boolean setItemRequired(String itemName, boolean required) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + InventoryDbSchema.ITEMS_TABLE_NAME+" SET "+InventoryDbSchema.REQUIRED_COL+" = ? WHERE "+InventoryDbSchema.ITEM_NAME_COL+" = ?", new Object[] {required ? 1 : 0, itemName});
        return true;
    }

    public boolean addToShoppingCart(String itemName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(InventoryDbSchema.CART_ITEM_NAME_COL, itemName);
        db.insert(InventoryDbSchema.SHOPPING_CART_TABLE_NAME, null, cv);
        return true;
    }

    public boolean removeFromShoppingCart(String itemName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + InventoryDbSchema.SHOPPING_CART_TABLE_NAME + " WHERE " + InventoryDbSchema.CART_ITEM_NAME_COL + " = ?", new String[]{itemName});
        return true;
    }

    public boolean clearShoppingCart() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + InventoryDbSchema.SHOPPING_CART_TABLE_NAME);
        return true;
    }

    public Set<String> getShoppingCartItemNames() {
        Cursor wrapped = getReadableDatabase().query(InventoryDbSchema.SHOPPING_CART_TABLE_NAME, null, null, null, null, null, null);
        ItemsCursor cursor = new ItemsCursor(wrapped);
        int numOfItems = cursor.getCount();
        Set<String> shoppingCartItems = new HashSet<String>();
        cursor.moveToFirst();
        for(int i = 0; i < numOfItems ; ++i) {
            shoppingCartItems.add(cursor.getString(cursor.getColumnIndex(InventoryDbSchema.CART_ITEM_NAME_COL)));
            cursor.moveToNext();
        }
        Log.e("IVTD", "Retrieving cart items: " + shoppingCartItems);
        return shoppingCartItems;
    }

    private static class ItemsCursor extends CursorWrapper {

        public ItemsCursor(Cursor cursor) {
            super(cursor);
        }

        public Item getItem() {
            Item item =  new Item();
            item.setName(getString(getColumnIndex(InventoryDbSchema.ITEM_NAME_COL)));
            item.setStorageSection(getString(getColumnIndex(InventoryDbSchema.STORAGE_SECTION_COL)));
            item.setStoreSection(getString(getColumnIndex(InventoryDbSchema.STORE_SECTION_COL)));
            item.setRequired(getInt(getColumnIndex(InventoryDbSchema.REQUIRED_COL)) == 1);
            return item;
        }
    }
}
