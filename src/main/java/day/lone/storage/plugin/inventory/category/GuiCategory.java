package day.lone.storage.plugin.inventory.category;

import day.lone.storage.plugin.EQItemStorage;
import day.lone.storage.plugin.data.ItemStorage;
import day.lone.storage.plugin.inventory.GuiStorages;
import day.lone.storage.plugin.service.StorageService;
import day.yone.utils.ItemUtils;
import day.yone.utils.PlayerUtils;
import day.yone.utils.SchedulerUtils;
import day.yone.utils.inventory.CustomInventoryHolder;
import day.yone.utils.paginated.PaginatedComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GuiCategory extends CustomInventoryHolder {

    public int page;
    public String name;
    public ItemStorage itemStorage;
    public PaginatedComponent<ItemStack> component;

    public boolean isPrevent = false;

    private final List<Integer> airSlots = List.of(
            45,
            46,
            47,
            48,
            49,
            50,
            51,
            52,
            53
    );

    public GuiCategory(int page, String name) {
        super(6, true, name + " 카테고리 | 아이템 목록");
        this.page = page;
        this.name = name;
        this.itemStorage = StorageService.getInstance().getStorage(name);
        this.component = new PaginatedComponent<>(this.itemStorage.getItems(), 45);
    }

    @Override
    protected void prevInit(Inventory inventory) {
        this.airSlots.forEach(slot ->
                inventory.setItem(slot, ItemUtils.setType(Material.WHITE_STAINED_GLASS_PANE).setDisplayName("&f").getItemStack())
        );
        inventory.setItem(45, getPreviousPage());
        inventory.setItem(53, getNextPage());
        inventory.setItem(49, getEditCategory());
    }

    @Override
    protected void init(Inventory inventory) {
        for (ItemStack is : this.component.getPanes(this.page)) {
            inventory.addItem(getCategoryItem(is));
        }
    }

    @Override
    protected void init(Inventory inventory, int page) {
        this.page = page;
        this.itemStorage = StorageService.getInstance().getStorage(name);
        this.component = new PaginatedComponent<>(this.itemStorage.getItems(), 45);

        for (int i = 0; i < 45; i++) {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }

        for (ItemStack is : this.component.getPanes(this.page)) {
            inventory.addItem(getCategoryItem(is));
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null) return;

        Player player = (Player) event.getWhoClicked();
        if (itemStack.isSimilar(getPreviousPage())) {
            if ((this.page - 1) < 1) return;
            init(getInventory(), this.page - 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
            return;
        }

        if (itemStack.isSimilar(getNextPage())) {
            init(getInventory(), this.page + 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
            return;
        }

        if (itemStack.isSimilar(getEditCategory())) {
            this.isPrevent = true;
            new GuiEditCategory(this.page, this.name).openInventory(player);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
            return;
        }

        for (ItemStack is : this.itemStorage.getItems()) {
            if (itemStack.isSimilar(getCategoryItem(is))) {
                int amount = 1;
                if (event.isShiftClick()) {
                    amount = 64;
                }

                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                player.getInventory().addItem(PlayerUtils.getItemStack(is.clone(), amount));
            }
        }
    }

    @Override
    protected void onClose(InventoryCloseEvent event) {
        if (this.isPrevent) return;
        if (event.getPlayer() instanceof Player player) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
            new SchedulerUtils(EQItemStorage.getInstance()).later(() -> new GuiStorages(1).openInventory(player));
        }
    }

    private ItemStack getPreviousPage() {
        return ItemUtils.setType(Material.RED_STAINED_GLASS_PANE)
                .setDisplayName("&c이전 페이지 이동").getItemStack();
    }

    private ItemStack getNextPage() {
        return ItemUtils.setType(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName("&a다음 페이지 이동").getItemStack();
    }

    private ItemStack getEditCategory() {
        return ItemUtils.setType(Material.ANVIL)
                .setDisplayName("&7카테고리 편집")
                .setLore(List.of("", "&a[ 클릭 시 카테고리를 편집합니다. ]")).getItemStack();
    }

    private ItemStack getCategoryItem(ItemStack is) {
        return ItemUtils.setItemStack(is.clone())
                .setLore(List.of(
                        "&e[1개 지급] &f좌클릭",
                        "&c[64개 지급] &f쉬프트 + 좌클릭",
                        "",
                        "&a[ 클릭 시 해당 아이템을 지급 받습니다. ]"
                )).getItemStack();
    }

}