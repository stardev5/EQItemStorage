package day.lone.storage.plugin.data;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemStorage {

    private String name;
    private List<ItemStack> items;

    public ItemStorage(String name, List<ItemStack> items) {
        this.name = name;
        this.items = items;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

    public void addItem(ItemStack is) {
        this.items.add(is);
    }

    public void removeItem(ItemStack is) {
        this.items.remove(is);
    }

    public boolean isItem(ItemStack is) {
        for (ItemStack item : this.items) {
            if (item.isSimilar(is)) {
                return true;
            }
        }
        return false;
    }

    public List<ItemStack> getItems() {
        return items;
    }

}