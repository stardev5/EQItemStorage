package day.lone.storage.plugin.inventory;

import day.lone.storage.plugin.EQItemStorage;
import day.lone.storage.plugin.data.ItemStorage;
import day.lone.storage.plugin.inventory.category.GuiCategory;
import day.lone.storage.plugin.service.StorageService;
import day.yone.sign.utils.SignGUI;
import day.yone.sign.utils.SignGUIAction;
import day.yone.utils.ItemUtils;
import day.yone.utils.SchedulerUtils;
import day.yone.utils.color.ColorUtils;
import day.yone.utils.inventory.CustomInventoryHolder;
import day.yone.utils.paginated.PaginatedComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class GuiStorages extends CustomInventoryHolder {

    public int page;
    public List<String> categories;
    public PaginatedComponent<String> component;

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

    public GuiStorages(int page) {
        super(6, true, "메인 카테고리");
        this.page = page;
        this.categories = StorageService.getInstance().getStorages();
        this.component = new PaginatedComponent<>(this.categories, 45);
    }

    @Override
    protected void prevInit(Inventory inventory) {
        this.airSlots.forEach(slot ->
                inventory.setItem(slot, ItemUtils.setType(Material.WHITE_STAINED_GLASS_PANE).setDisplayName("&f").getItemStack())
        );
        inventory.setItem(45, getPreviousPage());
        inventory.setItem(53, getNextPage());
        inventory.setItem(49, getAddCategory());
    }

    @Override
    protected void init(Inventory inventory) {
        for (String name : this.component.getPanes(this.page)) {
            inventory.addItem(getCategory(name));
        }
    }

    @Override
    protected void init(Inventory inventory, int page) {
        this.page = page;
        this.categories = StorageService.getInstance().getStorages();
        this.component = new PaginatedComponent<>(this.categories, 45);

        for (int i = 0; i < 45; i++) {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }

        for (String name : this.component.getPanes(page)) {
            inventory.addItem(getCategory(name));
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

        if (itemStack.isSimilar(getAddCategory())) {
            SignGUI.builder()
                    .setLine(1, "카테고리 ID를 입력하세요.")
                    .setLine(2, "[ 취소 : - ]")
                    .setHandler((p, e) -> {
                        String category = e.getLine(0);
                        if (category.isEmpty()) {
                            return List.of(SignGUIAction.displayNewLines(e.getLines()));
                        } else if (category.equals("-")) {
                            return List.of(SignGUIAction.openInventory(EQItemStorage.getInstance(), new GuiStorages(this.page).getOpenInventory()));
                        } else if (StorageService.getInstance().isStorage(category)) {
                            return List.of(
                                    SignGUIAction.displayNewLines(e.getLines()),
                                    SignGUIAction.run(() -> p.sendMessage(ColorUtils.getColor("&c이미 존재하는 카테고리 ID입니다.")))
                            );
                        }

                        StorageService.getInstance().addStorage(category);
                        return List.of(
                                SignGUIAction.run(() -> {
                                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
                                    p.sendMessage(ColorUtils.getColor("&a" + category + "(을)를 추가했습니다!"));
                                }),
                                SignGUIAction.openInventory(EQItemStorage.getInstance(), new GuiStorages(this.page).getOpenInventory())
                        );
                    }).build().open(player);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
            return;
        }

        for (String name : this.component.getPanes(this.page)) {
            if (itemStack.isSimilar(getCategory(name))) {
                if (event.isShiftClick()) {
                    if (event.isLeftClick()) {
                        StorageService.getInstance().removeStorage(name);
                        new GuiStorages(this.page).openInventory(player);
                        player.sendMessage(ColorUtils.getColor("&c" + name + "(을)를 삭제했습니다."));
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
                    } else if (event.isRightClick()) {
                        SignGUI.builder()
                                .setLine(1, "카테고리 ID를 입력하세요.")
                                .setLine(2, "[ 취소 : - ]")
                                .setHandler((p, e) -> {
                                    String category = e.getLine(0);
                                    if (category.isEmpty()) {
                                        return List.of(SignGUIAction.displayNewLines(e.getLines()));
                                    } else if (category.equals("-")) {
                                        return List.of(SignGUIAction.openInventory(EQItemStorage.getInstance(), new GuiStorages(this.page).getOpenInventory()));
                                    } else if (StorageService.getInstance().isStorage(category)) {
                                        return List.of(
                                                SignGUIAction.displayNewLines(e.getLines()),
                                                SignGUIAction.run(() -> p.sendMessage(ColorUtils.getColor("&c이미 존재하는 카테고리 ID 입니다.")))
                                        );
                                    }

                                    ItemStorage itemStorage = StorageService.getInstance().getStorage(name);
                                    StorageService.getInstance().removeStorage(name);
                                    itemStorage.setName(category);
                                    StorageService.getInstance().updateStorage(itemStorage);

                                    return List.of(
                                            SignGUIAction.run(() -> {
                                                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
                                                p.sendMessage(ColorUtils.getColor("&a" + name + "의 이름을 편집했습니다!"));
                                            }),
                                            SignGUIAction.openInventory(EQItemStorage.getInstance(), new GuiStorages(this.page).getOpenInventory())
                                    );
                                }).build().open(player);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                    }
                } else {
                    if (event.isLeftClick()) {
                        new GuiCategory(1, name).openInventory(player);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                    }
                }
            }
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

    private ItemStack getAddCategory() {
        return ItemUtils.setType(Material.BEACON)
                .setDisplayName("&7카테고리 추가")
                .setLore(List.of("&a[ 클릭 시 카테고리를 추가합니다. ]")).getItemStack();
    }

    private ItemStack getCategory(String name) {
        ItemStorage storage = StorageService.getInstance().getStorage(name);
        return ItemUtils.setItemStack(storage.getItems().stream().findFirst().orElse(new ItemStack(Material.BEDROCK)))
                .setDisplayName("&7" + name + " 카테고리").setLore(List.of(
                        "&e[카테고리 이동] &f좌클릭",
                        "&c[카테고리 삭제] &f쉬프트 + 좌클릭",
                        "&c[카테고리 편집] &f쉬프트 + 우클릭",
                        "",
                        "&a[ 클릭 시 해당 카테고리로 이동합니다. ]"
                )).getItemStack();
    }

}