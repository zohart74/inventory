package org.tennez.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Storage {

    private List<String> storageSections;
    private Set<String> storeSections;
    private List<Item> allItems;

    private Map<String, List<Item>> storageSectionToItems;

    public Storage(List<String> storageSections) {
        this.storageSections = storageSections;
        storeSections = new HashSet<String>();
        allItems = new LinkedList<Item>();
        storageSectionToItems = new HashMap<String, List<Item>>();
        for(String section : storageSections) {
            storageSectionToItems.put(section, new LinkedList<Item>());
        }
    }

    public boolean add(Item item) {
        boolean addedStorageSection = false;
        List<Item> storageItems = storageSectionToItems.get(item.getStorageSection());
        if(storageItems == null) {
            storageItems = new LinkedList<Item>();
            storageSectionToItems.put(item.getStorageSection(), storageItems);
            storageSections.add(item.getStorageSection());
            addedStorageSection = true;
        }
        storageItems.add(item);
        allItems.add(item);
        storeSections.add(item.getStoreSection());
        return addedStorageSection;
    }

    public boolean remove(Item item) {
        boolean removedStorageSection = false;
        List<Item> storageItems = storageSectionToItems.get(item.getStorageSection());
        if(storageItems != null) {
            storageItems.remove(item);
            if(storageItems.isEmpty()) {
                storageSectionToItems.remove(item.getStorageSection());
                storageSections.remove(item.getStorageSection());
                removedStorageSection = true;
            }
        }
        allItems.remove(item);
        boolean lastInStoreSection = true;
        for(Item testItem : allItems ) {
            if(item.getStoreSection().equals(testItem.getStoreSection())) {
                lastInStoreSection = false;
            }
        }
        if(lastInStoreSection) {
            storeSections.remove(item.getStoreSection());
        }
        return removedStorageSection;
    }

    public List<String> getStorageSections() {
        return storageSections;
    }

    public List<Item> getStorageItems(String storageSection) {
        return storageSectionToItems.get(storageSection);
    }

    public void updateStorageSection(List<String> storageSections) {
        this.storageSections = storageSections;
    }

    public List<String> getStoreSections() {
        return new ArrayList<String>(storeSections);
    }
}
