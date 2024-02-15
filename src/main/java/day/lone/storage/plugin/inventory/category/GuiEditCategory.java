package day.lone.storage.plugin.inventory.category;

import day.lone.storage.plugin.EQItemStorage;
import day.lone.storage.plugin.data.ItemStorage;
import day.lone.storage.plugin.service.StorageService;
import day.yone.utils.ItemUtils;
import day.yone.utils.PlayerUtils;
import day.yone.utils.SchedulerUtils;
import day.yone.utils.color.ColorUtils;
import day.yone.utils.inventory.CustomInventoryHolder;
import day.yone.utils.paginated.PaginatedComponent;
import day.yone.utils.translate.TranslateEnum;
import day.yone.utils.translate.Translator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GuiEditCategory extends CustomInventoryHolder {

    public int page;
    public String name;
    public ItemStorage itemStorage;
    public PaginatedComponent<ItemStack> component;

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

    public GuiEditCategory(int page, String name) {
        super(6, true, name + " 카테고리 | 아이템 편집");
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
    }

    @Override
    protected void init(Inventory inventory) {
        for (ItemStack is : this.component.getPanes(this.page)) {
            inventory.addItem(is);
        }
    }

    @Override
    protected void init(Inventory inventory, int page) {
        this.page = page;
        this.component = new PaginatedComponent<>(this.itemStorage.getItems(), 45);

        for (int i = 0; i < 45; i++) {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }

        for (ItemStack is : this.component.getPanes(this.page)) {
            inventory.addItem(is);
        }
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
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

        String itemName = Translator.getItemLanguageName(EQItemStorage.getInstance(), TranslateEnum.KOREAN, itemStack);

        Inventory inventory = event.getClickedInventory();
        if (inventory != null && inventory.equals(player.getInventory())) {
            if (!this.itemStorage.isItem(getCategoryItem(itemStack))) {
                this.itemStorage.addItem(getCategoryItem(itemStack));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
                player.sendMessage(ColorUtils.getColor("&a" + itemName + "(을)를 등록했습니다!"));
                init(getInventory(), this.page);
            } else {
                player.sendMessage(ColorUtils.getColor("&c이미 등록되어 있는 아이템입니다."));
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 2);
            }
            return;
        }

        for (ItemStack is : this.component.getPanes(this.page)) {
            if (itemStack.isSimilar(is)) {
                this.itemStorage.removeItem(is);
                player.sendMessage(ColorUtils.getColor("&c" + itemName + "(을)를 제거했습니다."));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
                init(getInventory(), this.page);
            }
        }
    }

    @Override
    protected void onClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            StorageService.getInstance().updateStorage(this.itemStorage);
            player.sendMessage(ColorUtils.getColor("&a" + this.name + "(을)를 편집했습니다!"));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
            new SchedulerUtils(EQItemStorage.getInstance()).later(() -> new GuiCategory(1, this.name).openInventory(player));
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

    private ItemStack getCategoryItem(ItemStack is) {
        return PlayerUtils.getItemStack(is.clone(), 1);
    }

}