package day.lone.storage.plugin.config;

import day.lone.storage.plugin.EQItemStorage;
import day.lone.storage.plugin.data.ItemStorage;
import day.yone.utils.Utils;
import day.yone.utils.config.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class StorageConfig extends FileConfiguration {

    public StorageConfig() {
        super(EQItemStorage.getInstance(), "storages.yml");
        createFile();

        addDefault("storages", new HashSet<>());
        copyDefaults(true);
        saveData();
    }

    public void addStorage(String name) {
        set("storages." + name.toLowerCase() + ".name", name);
        set("storages." + name.toLowerCase() + ".items", new ArrayList<>());
    }

    public void updateStorage(String name, List<ItemStack> items) {
        set("storages." + name.toLowerCase() + ".name", name);
        set("storages." + name.toLowerCase() + ".items", items.stream().map(Utils::encode).collect(Collectors.toList()));
    }

    public void removeStorage(String name) {
        set("storages." + name.toLowerCase(), null);
    }

    public ItemStorage getStorage(String name) {
        return new ItemStorage(
                getString("storages." + name.toLowerCase() + ".name"),
                getList("storages." + name.toLowerCase() + ".items")
                .stream().map(byte[].class::cast).map(Utils::decode).map(ItemStack.class::cast).collect(Collectors.toList())
        );
    }

    public boolean isStorage(String name) {
        return contains("storages." + name.toLowerCase());
    }

    public List<String> getStorages() {
        return new ArrayList<>(getConfigurationSection("storages").getKeys(false));
    }

}