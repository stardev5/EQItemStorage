package day.lone.storage.plugin;

import co.aikar.commands.BukkitCommandManager;
import day.lone.storage.plugin.command.StorageCommand;
import day.lone.storage.plugin.config.StorageConfig;
import day.yone.utils.inventory.CustomInventoryListener;
import day.yone.utils.translate.TranslateEnum;
import day.yone.utils.translate.Translator;
import org.bukkit.plugin.java.JavaPlugin;

public final class EQItemStorage extends JavaPlugin {

    private static EQItemStorage instance;

    @Override
    public void onEnable() {
        instance = this;

        new StorageConfig();

        new CustomInventoryListener(this);

        Translator.setTranslateEnum(this, TranslateEnum.KOREAN);

        BukkitCommandManager commandManager = new BukkitCommandManager(this);
        commandManager.registerCommand(new StorageCommand());
    }

    public static EQItemStorage getInstance() {
        return instance;
    }

}
