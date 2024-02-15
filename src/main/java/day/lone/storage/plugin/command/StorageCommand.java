package day.lone.storage.plugin.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import day.lone.storage.plugin.inventory.GuiStorages;
import org.bukkit.entity.Player;

@CommandAlias("storage")
@CommandPermission("storage.admin")
public class StorageCommand extends BaseCommand {

    @Default
    public void help(Player player) {
        new GuiStorages(1).openInventory(player);
    }

}