package org.tennez.inventory;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ShoppingCart {

    private Set<String> shoppingCartItemNames;
    private Map<String, Item> shoppingCartItemNameToItem;

    public ShoppingCart(Set<String> shoppingCartItemNames) {
        this.shoppingCartItemNames = shoppingCartItemNames;
        shoppingCartItemNameToItem = new HashMap<String, Item>();
        Log.e("IVTD","shopiing cart item names: "+shoppingCartItemNames+" , "+this.shoppingCartItemNames);
    }

    public void add(Item item) {
        if(!shoppingCartItemNames.contains(item.getName())) {
            shoppingCartItemNames.add(item.getName());
        }
        shoppingCartItemNameToItem.put(item.getName(), item);
    }

    public void remove(Item item) {
        shoppingCartItemNames.remove(item.getName());
        shoppingCartItemNameToItem.remove(item.getName());
    }

    public void clear() {
        shoppingCartItemNames.clear();
        shoppingCartItemNameToItem.clear();
    }

    public List<Item> getAllItems() {
        return new ArrayList<Item>(shoppingCartItemNameToItem.values());
    }

    public boolean contains(Item item) {
        return shoppingCartItemNames.contains(item.getName());
    }

}
