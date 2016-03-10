package org.tennez.inventory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class ShoppingList {

    private Map<String, List<Item>> storeSectionToItems;

    public ShoppingList() {
        storeSectionToItems = new HashMap<String, List<Item>>();
    }

    public void add(Item item) {
        List<Item> shoppingListItems = storeSectionToItems.get(item.getStoreSection());
        if(shoppingListItems == null) {
            shoppingListItems = new LinkedList<Item>();
            storeSectionToItems.put(item.getStoreSection(),shoppingListItems);
        }
        shoppingListItems.add(item);
    }

    public void remove(Item item) {
        List<Item> shoppingListItems = storeSectionToItems.get(item.getStoreSection());
        if(shoppingListItems != null) {
            shoppingListItems.remove(item);
            if(shoppingListItems.isEmpty()) {
                storeSectionToItems.remove(item.getStoreSection());
            }
        }
    }

    public List<String> getStoreSections() {
        return new LinkedList<String>(storeSectionToItems.keySet());
    }

    public List<Item> getStoreItems(String storeSection) {
        return storeSectionToItems.get(storeSection);
    }
}
