package org.tennez.inventory;

import android.content.Context;
import android.util.Log;

import org.tennez.inventory.db.InventoryDbHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemsRepository {

    private Set<String> itemNames;
    private Storage storage;
    private ShoppingList shoppingList;
    private ShoppingCart shoppingCart;
    private List<Item> allItems;
    private Context context;

    private ItemsRepository(Context context) {
        this.context = context;

        InventoryDbHelper inventoryDb = InventoryDbHelper.getRichContentDatabaseHelper(context);
        itemNames = new HashSet<String>();
        storage = new Storage(inventoryDb.getAllStorageSections());
        Log.e("IVTD","Storage sections: "+storage.getStorageSections().size());
        shoppingList = new ShoppingList();
        Set<String> shoppingCartItems = inventoryDb.getShoppingCartItemNames();
        shoppingCart = new ShoppingCart(shoppingCartItems);

        allItems = inventoryDb.getAllItems();
        Log.e("IVTD", "DB Items: " + allItems.size());
        for(Item item: allItems) {
            Log.e("IVTD", "DB Item: " + item.getName());
            itemNames.add(item.getName());
            storage.add(item);
            if(item.isRequired()) {
                shoppingList.add(item);
            }
            if(shoppingCartItems.contains(item.getName())) {
                shoppingCart.add(item);
            }
        }
    }

    private static ItemsRepository instance = null;

    private static ItemsRepository getInstance() {
        return instance;
    }

    public static ItemsRepository getItemsRepository(Context context) {
        if(instance != null) {
            return instance;
        }
        synchronized (ItemsRepository.class) {
            if(getInstance() == null) {
                instance = new ItemsRepository(context);
            }
        }
        return instance;
    }

    public boolean addItem(Item item) {
        if(!itemNames.contains(item.getName())) {
            InventoryDbHelper dbHelper = InventoryDbHelper.getRichContentDatabaseHelper(context);
            boolean storageSectionAdded = storage.add(item);
            if(storageSectionAdded) {
                dbHelper.updateStorageSections(storage.getStorageSections());
            }
            itemNames.add(item.getName());
            if(item.isRequired()) {
                shoppingList.add(item);
            }
            allItems.add(item);
            return dbHelper.addItem(item);
        } else {
            return false;
        }
    }

    public boolean updateItem(String currentItemName, String newItemName, String newStorageSection, String newStoreSection) {
        Item currentItem = getItem(currentItemName);
        InventoryDbHelper dbHelper = InventoryDbHelper.getRichContentDatabaseHelper(context);
        if(!currentItem.getStoreSection().equals(newStorageSection)) {
            storage.remove(currentItem);
            currentItem.setStorageSection(newStorageSection);
            if(storage.add(currentItem)) {
                dbHelper.updateStorageSections(storage.getStorageSections());
            }
        }
        if(!currentItem.getStoreSection().equals(newStoreSection)) {
            storage.remove(currentItem);
            if(currentItem.isRequired()) {
                shoppingList.remove(currentItem);
            }
            currentItem.setStoreSection(newStoreSection);
            storage.add(currentItem);
            if(currentItem.isRequired()) {
                shoppingList.add(currentItem);
            }
        }
        if(!currentItem.getName().equals(newItemName)) {
            if(shoppingCart.contains(currentItem)) {
                shoppingCart.remove(currentItem);
                currentItem.setName(newItemName);
                shoppingCart.add(currentItem);
            } else {
                currentItem.setName(newItemName);
            }
        }
        dbHelper.updateItem(currentItemName, currentItem);
        return true;
    }

    public boolean removeItem(Item item) {
        InventoryDbHelper dbHelper = InventoryDbHelper.getRichContentDatabaseHelper(context);
        boolean removed = dbHelper.deleteItem(item);
        if(removed) {
            itemNames.remove(item.getName());
            boolean storageSectionRemoved = storage.remove(item);
            if(storageSectionRemoved) {
                dbHelper.updateStorageSections(storage.getStorageSections());
            }
            shoppingList.remove(item);
        }
        allItems.remove(item);
        return removed;
    }

    public boolean setItemRequired(Item item, boolean required) {
        InventoryDbHelper dbHelper = InventoryDbHelper.getRichContentDatabaseHelper(context);
        item.setRequired(required);
        if(dbHelper.setItemRequired(item.getName(), required)) {
            if(required) {
                shoppingList.add(item);
            } else {
                shoppingList.remove(item);
            }
            return true;
        }
        return false;
    }

    public boolean updateStorageSections(List<String> storageSections) {
        storage.updateStorageSection(storageSections);
        InventoryDbHelper dbHelper = InventoryDbHelper.getRichContentDatabaseHelper(context);
        return dbHelper.updateStorageSections(storage.getStorageSections());
    }


    public List<String> getStorageSections() {
        return storage.getStorageSections();
    }

    public List<Item> getStorageItems(String sectionName) {
        return storage.getStorageItems(sectionName);
    }

    public List<String> getShoppingListStoreSections() {
        return shoppingList.getStoreSections();
    }

    public List<String> getStoreSections() {
        return storage.getStoreSections();
    }

    public List<Item> getShoppingListItems(String sectionName) {
        return shoppingList.getStoreItems(sectionName);
    }

    public void addToShoppingCart(Item item) {
        shoppingCart.add(item);
        InventoryDbHelper dbHelper = InventoryDbHelper.getRichContentDatabaseHelper(context);
        dbHelper.addToShoppingCart(item.getName());
    }

    public void removeFromShoppingCart(Item item) {
        shoppingCart.remove(item);
        InventoryDbHelper dbHelper = InventoryDbHelper.getRichContentDatabaseHelper(context);
        dbHelper.removeFromShoppingCart(item.getName());
    }

    public boolean isInShoppingCart(Item item) {
        return shoppingCart.contains(item);
    }

    public void clearShoppingCart() {
        shoppingCart.clear();
        InventoryDbHelper dbHelper = InventoryDbHelper.getRichContentDatabaseHelper(context);
        dbHelper.clearShoppingCart();
    }

    public void completeShopping() {
        List<Item> shoppingCartItems = shoppingCart.getAllItems();
        for(Item item : shoppingCartItems) {
            setItemRequired(item, false);
        }
        shoppingCart.clear();
    }

    public List<Item> getAllItems() {
        return allItems;
    }

    public Item getItem(String itemName) {
        for(Item item : allItems) {
            if(item.getName().equals(itemName)) {
                return item;
            }
        }
        return null;
    }
}
