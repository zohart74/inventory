package org.tennez.inventory;

public class Item {

    private String name;
    private String storageSection;
    private String storeSection;
    private boolean required;

    public Item() {
    }

    public Item(String name, String storageSection, String storeSection, boolean required) {
        this.name = name;
        this.storageSection = storageSection;
        this.storeSection = storeSection;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStorageSection() {
        return storageSection;
    }

    public void setStorageSection(String storageSection) {
        this.storageSection = storageSection;
    }

    public String getStoreSection() {
        return storeSection;
    }

    public void setStoreSection(String storeSection) {
        this.storeSection = storeSection;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return name.equals(item.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
