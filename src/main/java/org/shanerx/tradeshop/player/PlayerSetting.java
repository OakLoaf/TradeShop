/*
 *
 *                         Copyright (c) 2016-2023
 *                SparklingComet @ http://shanerx.org
 *               KillerOfPie @ http://killerofpie.github.io
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *                http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  NOTICE: All modifications made by others to the source code belong
 *  to the respective contributor. No contributor should be held liable for
 *  any damages of any kind, whether be material or moral, which were
 *  caused by their contribution(s) to the project. See the full License for more information.
 *
 */

package org.shanerx.tradeshop.player;

import com.google.common.collect.Sets;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.shanerx.tradeshop.TradeShop;
import org.shanerx.tradeshop.data.config.Setting;
import org.shanerx.tradeshop.shop.Shop;
import org.shanerx.tradeshop.shoplocation.IllegalWorldException;
import org.shanerx.tradeshop.shoplocation.ShopLocation;
import org.shanerx.tradeshop.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerSetting {

    @Getter
    private final Set<String> ownedShops;
    private final String uuidString;
    @Getter
    private final Set<String> staffShops;
    @Getter
    private transient UUID uuid;
    @Getter
    @Setter
    private boolean showInvolvedStatus, adminEnabled = true;

    @Setter
    @Getter
    private int multi = Setting.MULTI_TRADE_DEFAULT.getInt();

    private transient Utils utils = new Utils();

    public PlayerSetting(UUID playerUUID, Map<String, Integer> data) {
        this.uuid = playerUUID;
        this.uuidString = uuid.toString();

        if (data.containsKey("multi")) multi = data.get("multi");

        ownedShops = Sets.newHashSet();
        staffShops = Sets.newHashSet();

        load();
    }

    public PlayerSetting(UUID playerUUID) {
        this.uuid = playerUUID;
        this.uuidString = uuid.toString();

        ownedShops = Sets.newHashSet();
        staffShops = Sets.newHashSet();

        load();
    }

    public static PlayerSetting deserialize(Map<String, Object> serialized) {
        if (!serialized.containsKey("uuidString")) return null; //TODO: Add error message (Player UUID is null)

        UUID plUUID = UUID.fromString(serialized.get("uuidString").toString());
        PlayerSetting playerSetting = new PlayerSetting(plUUID);

        for (Map.Entry<String, Object> entry : serialized.entrySet()) {
            switch (entry.getKey()) {
                case "ownedShops":
                    playerSetting.ownedShops.addAll((List<String>) entry.getValue());
                    break;
                case "staffShops":
                    playerSetting.staffShops.addAll((List<String>) entry.getValue());
                    break;
                case "showInvolvedStatus":
                    playerSetting.showInvolvedStatus = (boolean) entry.getValue();
                    break;
                case "adminEnabled":
                    playerSetting.adminEnabled = (boolean) entry.getValue();
                    break;
                case "multi":
                    playerSetting.multi = (int) entry.getValue();
                    break;
            }
        }


        return playerSetting;
    }

    public Map<String, Object> serialize() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("uuidString", uuidString);
        data.put("ownedShops", ownedShops);
        data.put("staffShops", staffShops);
        data.put("showInvolvedStatus", showInvolvedStatus);
        data.put("adminEnabled", adminEnabled);
        data.put("multi", multi);

        return data;
    }

    public void addShop(Shop shop) {
        if (shop.getOwner().getUUID().equals(uuid) &&
            !ownedShops.contains(shop.getShopLocationAsSL().toString()))
            ownedShops.add(shop.getShopLocationAsSL().toString());
        else if (shop.getUsersUUID(ShopRole.MANAGER, ShopRole.MEMBER).contains(uuid) &&
            !ownedShops.contains(shop.getShopLocationAsSL().toString()))
            staffShops.add(shop.getShopLocationAsSL().toString());
    }

    public void removeShop(Shop shop) {
        ownedShops.remove(shop.getShopLocationAsSL().toString());
        staffShops.remove(shop.getShopLocationAsSL().toString());
    }

    public void removeShop(String shop) {
        ownedShops.remove(shop);
        staffShops.remove(shop);
    }

    public void updateShop(Shop shop) {
        if (!shop.getUsersUUID(ShopRole.OWNER, ShopRole.MANAGER, ShopRole.MEMBER).contains(uuid))
            removeShop(shop);
        else
            addShop(shop);

    }

    public void load() {
        if (uuid == null) uuid = UUID.fromString(uuidString);
        if (multi > Setting.MULTI_TRADE_MAX.getInt()) multi = Setting.MULTI_TRADE_MAX.getInt();
        utils = new Utils();
    }

    public String getInvolvedStatusesString() {
        Set<String> nullShops = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        sb.append("&eStatus of your shops: \n");
        sb.append("&eShop Role &f| &eType &f| &eAvailable Trades &f| &eLocation &f| &eInventory Status\n&b");
        if (!getOwnedShops().isEmpty()) {
            getOwnedShops().forEach(s -> {
                try {
                    Shop shop = TradeShop.getPlugin().getDataStorage().loadShopFromSign(ShopLocation.deserialize(s));
                    if (shop == null) {
                        nullShops.add(s);
                    } else if (shop.checkRole(uuid) != ShopRole.SHOPPER) {
                        sb.append(shop.checkRole(uuid).toString()).append(" &f|&a ");
                        sb.append(shop.getShopType().toString()).append(" &f|&b ");
                        sb.append(shop.getAvailableTrades()).append(" &f|&d ");
                        sb.append(s).append(" &f| ");
                        sb.append(shop.getStatus().getLine()).append("\n&b");
                    }
                } catch (IllegalWorldException ignored) {
                    //Prevents IllegalWorldException when a player has shops in a world that is not loaded, They are not removed in case the world is loaded again...
                }
            });
        }
        if (!getStaffShops().isEmpty()) {
            getStaffShops().forEach(s -> {
                try {
                    Shop shop = TradeShop.getPlugin().getDataStorage().loadShopFromSign(ShopLocation.deserialize(s));
                    if (shop == null) {
                        nullShops.add(s);
                    } else if (shop.checkRole(uuid) != ShopRole.SHOPPER) {
                        sb.append(shop.checkRole(uuid).toString()).append(" &f|&a ");
                        sb.append(shop.getShopType().toString()).append(" &f|&b ");
                        sb.append(shop.getAvailableTrades()).append(" &f|&d ");
                        sb.append(s).append(" &f| ");
                        sb.append(shop.getStatus().getLine()).append("\n&b");
                    }
                } catch (IllegalWorldException ignored) {
                    //Prevents IllegalWorldException when a player has shops in a world that is not loaded, They are not removed in case the world is loaded again...
                }
            });
        }

        nullShops.forEach(this::removeShop);

        sb.deleteCharAt(sb.lastIndexOf("\n"));
        return utils.colorize(sb.toString());
    }

    public InventoryGui getInvolvedStatusesInventory() {
        Set<String> nullShops = new HashSet<>();
        InventoryGui gui = new InventoryGui(TradeShop.getPlugin(), Bukkit.getOfflinePlayer(uuid).getName() + "'s Shops", new String[]{"ggggggggg", "ggggggggg", " fp   ln "});
        GuiElementGroup group = new GuiElementGroup('g');
        if (!getOwnedShops().isEmpty()) {
            getOwnedShops().forEach(s -> {
                try {
                    Shop shop = TradeShop.getPlugin().getDataStorage().loadShopFromSign(ShopLocation.deserialize(s));
                    if (shop == null) {
                        nullShops.add(s);
                    } else if (shop.checkRole(uuid) != ShopRole.SHOPPER) {
                        group.addElement(new StaticGuiElement('e',
                                new ItemStack(shop.getInventoryLocation() != null ?
                                        shop.getInventoryLocation().getBlock().getType() :
                                        Material.getMaterial(shop.getShopLocation().getBlock().getType().toString().replaceAll("WALL_", ""))),
                                Math.min(shop.getAvailableTrades(), 64),
                                click -> {
                                    return true; //Prevents clicking the item from doing anything, required parameter when using amount
                                },
                                utils.colorize("&d" + s),
                                utils.colorize("&a" + shop.getShopType().toString()),
                                utils.colorize("&b" + shop.checkRole(uuid).toString()),
                                utils.colorize("&bAvailable Trades: " + shop.getAvailableTrades()),
                                utils.colorize(shop.getStatus().getLine())));
                    }
                } catch (IllegalWorldException ignored) {
                    //Prevents IllegalWorldException when a player has shops in a world that is not loaded, They are not removed in case the world is loaded again...
                }
            });
        }
        if (!getStaffShops().isEmpty()) {
            getStaffShops().forEach(s -> {
                try {
                    Shop shop = TradeShop.getPlugin().getDataStorage().loadShopFromSign(ShopLocation.deserialize(s));
                    if (shop == null) {
                        nullShops.add(s);
                    } else if (shop.checkRole(uuid) != ShopRole.SHOPPER) {
                        group.addElement(new StaticGuiElement('e',
                                new ItemStack(shop.getInventoryLocation() != null ?
                                        shop.getInventoryLocation().getBlock().getType() :
                                        Material.getMaterial(shop.getShopLocation().getBlock().getType().toString().replaceAll("WALL_", ""))),
                                Math.min(shop.getAvailableTrades(), 64),
                                click -> {
                                    return true; //Prevents clicking the item from doing anything, required parameter when using amount
                                },
                                utils.colorize("&d" + s),
                                utils.colorize("&a" + shop.getShopType().toString()),
                                utils.colorize("&b" + shop.checkRole(uuid).toString()),
                                utils.colorize("&bAvailable Trades: " + shop.getAvailableTrades()),
                                utils.colorize(shop.getStatus().getLine())));
                    }
                } catch (IllegalWorldException ignored) {
                    //Prevents IllegalWorldException when a player has shops in a world that is not loaded, They are not removed in case the world is loaded again...
                }
            });
        }

        nullShops.forEach(this::removeShop);

        gui.addElement(group);

        // First page
        gui.addElement(new GuiPageElement('f', new ItemStack(Material.STICK), GuiPageElement.PageAction.FIRST, "Go to first page (current: %page%)"));

        // Previous page
        gui.addElement(new GuiPageElement('p', new ItemStack(Material.POTION), GuiPageElement.PageAction.PREVIOUS, "Go to previous page (%prevpage%)"));

        // Next page
        gui.addElement(new GuiPageElement('n', new ItemStack(Material.SPLASH_POTION), GuiPageElement.PageAction.NEXT, "Go to next page (%nextpage%)"));

        // Last page
        gui.addElement(new GuiPageElement('l', new ItemStack(Material.ARROW), GuiPageElement.PageAction.LAST, "Go to last page (%pages%)"));

        //Blank Item
        gui.setFiller(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1));

        return gui;
    }

    public boolean showInvolvedStatus() {
        return showInvolvedStatus;
    }

}
