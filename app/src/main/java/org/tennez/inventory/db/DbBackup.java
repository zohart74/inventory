package org.tennez.inventory.db;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tennez.inventory.Item;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class DbBackup {

    private static final String ITEMS_KEY = "items";
    private static final String STORAGE_SECTIONS_KEY = "storageSections";

    public static class ItemBackup {

        private static final String NAME_KEY = "name";
        private static final String STORAGE_SECTION_KEY = "storageSection";
        private static final String STORE_SECTION_KEY = "storeSection";

        public static JSONObject itemToJson(Item item) throws JSONException {
            JSONObject itemJSON = new JSONObject();
            itemJSON.put(NAME_KEY, item.getName());
            itemJSON.put(STORAGE_SECTION_KEY, item.getStorageSection());
            itemJSON.put(STORE_SECTION_KEY, item.getStoreSection());
            return itemJSON;
        }

        public static Item jsonToItem(JSONObject itemJSON) throws JSONException{
            Item item = new Item();
            item.setName(itemJSON.getString(NAME_KEY));
            item.setStorageSection(itemJSON.getString(STORAGE_SECTION_KEY));
            item.setStoreSection(itemJSON.getString(STORE_SECTION_KEY));
            return item;
        }

        public static JSONArray itemsToJSONArray(List<Item> items) throws JSONException{
            JSONArray itemsJSONArray = new JSONArray();
            for(Item item : items) {
                itemsJSONArray.put(itemToJson(item));
            }
            return itemsJSONArray;
        }

        public static List<Item> jsonArrayToItems(JSONArray itemsJSONArray) throws JSONException {
            int itemCount = itemsJSONArray.length();
            List<Item> items = new LinkedList<Item>();
            for(int i = 0 ; i < itemCount ; ++i) {
                items.add(jsonToItem(itemsJSONArray.getJSONObject(i)));
            }
            return items;
        }
    }

    public static JSONArray stringListToJsonArray(List<String> strings) {
        JSONArray stringsJSONArray = new JSONArray();
        for(String string : strings) {
            stringsJSONArray.put(string);
        }
        return stringsJSONArray;
    }

    public static List<String> stringsJsonArrayToStrings(JSONArray stringsJSONArray) throws JSONException {
        int stringCount = stringsJSONArray.length();
        List<String> strings = new LinkedList<String>();
        for(int i = 0 ; i < stringCount ; ++i) {
            strings.add(stringsJSONArray.getString(i));
        }
        return strings;
    }

    public static JSONObject dbToJson(Context context) throws JSONException {
        InventoryDbHelper dbHelper = InventoryDbHelper.getRichContentDatabaseHelper(context);
        JSONObject dbJSON = new JSONObject();
        JSONArray itemsJSONArray = ItemBackup.itemsToJSONArray(dbHelper.getAllItems());
        dbJSON.put(ITEMS_KEY, itemsJSONArray);
        JSONArray storageSectionsJSONArray = stringListToJsonArray(dbHelper.getAllStorageSections());
        dbJSON.put(STORAGE_SECTIONS_KEY, storageSectionsJSONArray);
        return dbJSON;
    }

    public static void jsonToDb(Context context, JSONObject dbJSON) throws JSONException {
        InventoryDbHelper dbHelper = InventoryDbHelper.getRichContentDatabaseHelper(context);
        List<Item> items = ItemBackup.jsonArrayToItems(dbJSON.getJSONArray(ITEMS_KEY));
        for(Item item : items) {
            dbHelper.addItem(item);
        }
        List<String> storageSections = stringsJsonArrayToStrings(dbJSON.getJSONArray(STORAGE_SECTIONS_KEY));
        dbHelper.updateStorageSections(storageSections);
    }

    public static void saveDb(Context context, OutputStream os) throws JSONException, IOException{
        PrintWriter w = new PrintWriter(os);
        JSONObject dbJSON = dbToJson(context);
        w.println(dbJSON.toString());
        w.close();
    }

    public static void loadDb(Context context, InputStream is)  throws JSONException, IOException{
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        String dbJSONStr = r.readLine();
        Log.d("IVTD","DB JSON: "+dbJSONStr);
        JSONObject dbJSON = new JSONObject(dbJSONStr);
        jsonToDb(context, dbJSON);
        r.close();
    }

    public static void saveDb(Context context, File to) throws JSONException, IOException{
        saveDb(context, new FileOutputStream(to));
    }

    public static void loadDb(Context context, File from)  throws JSONException, IOException{
        loadDb(context, new FileInputStream(from));
    }
}
