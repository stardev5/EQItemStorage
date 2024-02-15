package day.lone.storage.plugin.service;

import day.lone.storage.plugin.config.StorageConfig;
import day.lone.storage.plugin.data.ItemStorage;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class StorageService {

    public static StorageService getInstance() {
        return new StorageService();
    }

    public void addStorage(String name) {
        StorageConfig storageConfig = new StorageConfig();
        storageConfig.addStorage(name);
    }

    public void updateStorage(ItemStorage itemStorage) {
        StorageConfig storageConfig = new StorageConfig();
        storageConfig.updateStorage(itemStorage.getName(), itemStorage.getItems());
    }

    public void removeStorage(String name) {
        StorageConfig storageConfig = new StorageConfig();
        storageConfig.removeStorage(name);
    }

    public ItemStorage getStorage(String name) {
        StorageConfig storageConfig = new StorageConfig();
        return storageConfig.getStorage(name);
    }

    public boolean isStorage(String name) {
        StorageConfig storageConfig = new StorageConfig();
        return storageConfig.isStorage(name);
    }

    public List<String> getStorages() {
        StorageConfig storageConfig = new StorageConfig();
        return storageConfig.getStorages();
    }

}