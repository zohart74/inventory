package org.tennez.inventory.db;

public class InventoryDbSchema {

    public static final String DB_NAME = "Inventory";
    public static final int DB_VERSION = 1;


    public static final String ITEMS_TABLE_NAME = "items";
    public static final String ITEM_NAME_COL = "name";
    public static final String STORAGE_SECTION_COL = "storageSection";
    public static final String STORE_SECTION_COL = "storeSection";
    public static final String REQUIRED_COL = "required";

    public static final String STORAGE_SECTIONS_TABLE_NAME = "storageSections";
    public static final String STORAGE_NAME_COL = "name";
    public static final String SECTION_INDEX_COL = "idx";

    public static final String SHOPPING_CART_TABLE_NAME = "shoppingCart";
    public static final String CART_ITEM_NAME_COL = "name";

}
