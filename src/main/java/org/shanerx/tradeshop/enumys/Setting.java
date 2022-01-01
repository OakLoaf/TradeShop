/*
 *
 *                         Copyright (c) 2016-2019
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

package org.shanerx.tradeshop.enumys;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.shanerx.tradeshop.TradeShop;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public enum Setting {

    // PostComment " " adds single newline below setting and "\n" adds 2 newlines below
    // PreComment `/n ` will have a new comment marker added after a sufficient space for proper formatting

    CONFIG_VERSION(SettingSectionKeys.NONE, "config-version", 1.2, "", "\n"),

    // System Options
    DATA_STORAGE_TYPE(SettingSectionKeys.SYSTEM_OPTIONS, "data-storage-type", "FLATFILE", "How would you like your servers data stored? (FLATFILE)"),
    ENABLE_DEBUG(SettingSectionKeys.SYSTEM_OPTIONS, "enable-debug", 0, "What debug code should be run. This will add significant amounts of spam to the console/log, generally not used unless requested by Devs"),
    CHECK_UPDATES(SettingSectionKeys.SYSTEM_OPTIONS, "check-updates", true, "Should we check for updates when the server starts"),
    ALLOW_METRICS(SettingSectionKeys.SYSTEM_OPTIONS, "allow-metrics", true, "Allow us to connect anonymous metrics so we can see how our plugin is being used to better develop it"),
    UNLIMITED_ADMIN(SettingSectionKeys.SYSTEM_OPTIONS, "unlimited-admin", false, "We do not recommend enabling this setting since any editing an admin should need to do can be done without this \n Should players with Admin permission be able to use any commands on any shops?"),
    USE_INTERNAL_PERMISSIONS(SettingSectionKeys.SYSTEM_OPTIONS, "use-internal-permissions", false, "Should our internal permission system be used? (Only enable if you aren't using a permission plugin)", "\n"),

    // Language Options
    MESSAGE_PREFIX(SettingSectionKeys.LANGUAGE_OPTIONS, "message-prefix", "&a[&eTradeShop&a] ", "The prefix the displays before all plugin messages", "\n"),

    SHOP_GOOD_COLOUR(SettingSectionKeys.LANGUAGE_OPTIONS, "shop-good-colour", "&2", "Header Colours, if the codes are showing in the header, set to \"\"\n Color for successfully created and stocked signs"),
    SHOP_INCOMPLETE_COLOUR(SettingSectionKeys.LANGUAGE_OPTIONS, "shop-incomplete-colour", "&7", "Color for shops that are missing data to make trades"),
    SHOP_BAD_COLOUR(SettingSectionKeys.LANGUAGE_OPTIONS, "shop-bad-colour", "&4", "Color for shops that were not successfully created", "\n"),

    SHOP_OPEN_STATUS(SettingSectionKeys.LANGUAGE_OPTIONS, "shop-open-status", "&a<Open>", "Status Text, What will be shown in the bottom line of shop sign for each status\n Open"),
    SHOP_CLOSED_STATUS(SettingSectionKeys.LANGUAGE_OPTIONS, "shop-closed-status", "&c<Closed>", "Closed"),
    SHOP_INCOMPLETE_STATUS(SettingSectionKeys.LANGUAGE_OPTIONS, "shop-incomplete-status", "&c<Incomplete>", "Incomplete"),
    SHOP_OUTOFSTOCK_STATUS(SettingSectionKeys.LANGUAGE_OPTIONS, "shop-outofstock-status", "&c<Out Of Stock>", "Out of Stock", "\n"),

    // Global Options
    ALLOWED_DIRECTIONS(SettingSectionKeys.GLOBAL_OPTIONS, "allowed-directions", new String[]{"DOWN", "WEST", "SOUTH", "EAST", "NORTH", "UP"}, "Directions an allowed shop can be from a sign. Allowed directions are:\n Up, Down, North, East, South, West"),
    ALLOWED_SHOPS(SettingSectionKeys.GLOBAL_OPTIONS, "allowed-shops", new String[]{"CHEST", "TRAPPED_CHEST", "SHULKER"}, "Inventories to allow for shops. Allowed blocks are:\n Chest, Trapped_Chest, Dropper, Hopper, Dispenser, Shulker, ..."),
    MAX_EDIT_DISTANCE(SettingSectionKeys.GLOBAL_OPTIONS, "max-edit-distance", 4, "Max distance a player can be from a shop to edit it"),
    ALLOW_TOGGLE_STATUS(SettingSectionKeys.GLOBAL_OPTIONS, "allow-toggle-status", true, "Can players toggle view of involved shops?"),
    ALLOW_SIGN_BREAK(SettingSectionKeys.GLOBAL_OPTIONS, "allow-sign-break", false, "Should we allow anyone to destroy a shops sign?"),
    ALLOW_CHEST_BREAK(SettingSectionKeys.GLOBAL_OPTIONS, "allow-chest-break", false, "Should we allow anyone to destroy a shops storage?", "\n"),

    // ^ Multi Trade
    ALLOW_MULTI_TRADE(SettingSectionKeys.GLOBAL_MULTI_TRADE, "enable", true, "Should we allow multi trades with shift + click (true/false)"),
    MULTI_TRADE_DEFAULT(SettingSectionKeys.GLOBAL_MULTI_TRADE, "default-multi", 2, "Default multiplier for trades using shift + click"),
    MULTI_TRADE_MAX(SettingSectionKeys.GLOBAL_MULTI_TRADE, "max-multi", 6, "Maximum amount a player can set their multiplier to. Not recommended to set any higher than 6 as this can cause bugs with iTrade Shops", "\n"),

    // Illegal Item Options
    GLOBAL_ILLEGAL_ITEMS_TYPE(SettingSectionKeys.GLOBAL_ILLEGAL_ITEMS, "type", ListType.BLACKLIST.toString()),
    GLOBAL_ILLEGAL_ITEMS_LIST(SettingSectionKeys.GLOBAL_ILLEGAL_ITEMS, "list", new String[]{"Bedrock", "Command_Block", "Barrier"}, "", " "),
    COST_ILLEGAL_ITEMS_TYPE(SettingSectionKeys.COST_ILLEGAL_ITEMS, "type", ListType.DISABLED.toString()),
    COST_ILLEGAL_ITEMS_LIST(SettingSectionKeys.COST_ILLEGAL_ITEMS, "list", new String[]{}, "", " "),
    PRODUCT_ILLEGAL_ITEMS_TYPE(SettingSectionKeys.PRODUCT_ILLEGAL_ITEMS, "type", ListType.DISABLED.toString()),
    PRODUCT_ILLEGAL_ITEMS_LIST(SettingSectionKeys.PRODUCT_ILLEGAL_ITEMS, "list", new String[]{}, "", " "),

    // Shop Options
    MAX_SHOP_USERS(SettingSectionKeys.SHOP_OPTIONS, "max-shop-users", 5, "Maximum users(Managers/Members) a shop can have"),
    MAX_SHOPS_PER_CHUNK(SettingSectionKeys.SHOP_OPTIONS, "max-shops-per-chunk", 128, "Maximum shops that can exist in a single chunk"),
    MAX_ITEMS_PER_TRADE_SIDE(SettingSectionKeys.SHOP_OPTIONS, "max-items-per-trade-side", 6, "Maximum amount of item stacks per side of trade"),
    ALLOW_USER_PURCHASING(SettingSectionKeys.SHOP_OPTIONS, "allow-user-purchasing", false, "Can players purchase from a shop in which they are a user of (true/false)"),
    MULTIPLE_ITEMS_ON_SIGN(SettingSectionKeys.SHOP_OPTIONS, "multiple-items-on-sign", "Use '/ts what'", "Text that shows on trade signs that contain more than 1 item", "\n"),

    // Trade Shop Options
    TRADESHOP_HEADER(SettingSectionKeys.TRADE_SHOP_OPTIONS, "header", "Trade", "The header that appears at the top of the shop signs, this is also what the player types to create the sign"),
    TRADESHOP_EXPLODE(SettingSectionKeys.TRADE_SHOP_OPTIONS, "allow-explode", false, "Can explosions damage the shop sign/storage (true/false)"),
    TRADESHOP_HOPPER_EXPORT(SettingSectionKeys.TRADE_SHOP_OPTIONS, "allow-hopper-export", false, "Can hoppers pull items from the shop storage (true/false)"),
    TRADESHOP_HOPPER_IMPORT(SettingSectionKeys.TRADE_SHOP_OPTIONS, "allow-hopper-import", false, "Can hoppers push items into the shop storage (true/false)", "\n"),

    // ITrade Shop Options
    ITRADESHOP_OWNER(SettingSectionKeys.ITRADE_SHOP_OPTIONS, "owner", "Server Shop", "Name to put on the bottom of iTrade signs"),
    ITRADESHOP_HEADER(SettingSectionKeys.ITRADE_SHOP_OPTIONS, "header", "iTrade", "The header that appears at the top of the shop signs, this is also what the player types to create the sign"),
    ITRADESHOP_EXPLODE(SettingSectionKeys.ITRADE_SHOP_OPTIONS, "allow-explode", false, "Can explosions damage the shop sign (true/false)", ""),
    ITRADESHOP_NO_COST_TEXT(SettingSectionKeys.ITRADE_SHOP_OPTIONS, "no-cost-text", "nothing", "What text should be used for successful trades when no cost is present", "\n"),

    // BiTrade Shop Options
    BITRADESHOP_HEADER(SettingSectionKeys.BITRADE_SHOP_OPTIONS, "header", "BiTrade", "The header that appears at the top of the shop signs, this is also what the player types to create the sign"),
    BITRADESHOP_EXPLODE(SettingSectionKeys.BITRADE_SHOP_OPTIONS, "allow-explode", false, "Can explosions damage the shop sign/storage (true/false)"),
    BITRADESHOP_HOPPER_EXPORT(SettingSectionKeys.BITRADE_SHOP_OPTIONS, "allow-hopper-export", false, "Can hoppers pull items from the shop storage (true/false)"),
    BITRADESHOP_HOPPER_IMPORT(SettingSectionKeys.BITRADE_SHOP_OPTIONS, "allow-hopper-import", false, "Can hoppers push items into the shop storage (true/false)", "\n");


    private static final TradeShop plugin = (TradeShop) Bukkit.getPluginManager().getPlugin("TradeShop");
    private static final File file = new File(plugin.getDataFolder(), "config.yml");
    private static FileConfiguration config = YamlConfiguration.loadConfiguration(file);
    private final String key;
    private final String path;
    private final Object defaultValue;
    private final SettingSectionKeys sectionKey;
    private String preComment = "";
    private String postComment = "";

    Setting(SettingSectionKeys sectionKey, String path, Object defaultValue) {
        this.sectionKey = sectionKey;
        this.key = path;
        this.path = sectionKey.getKey() + path;
        this.defaultValue = defaultValue;
    }

    Setting(SettingSectionKeys sectionKey, String path, Object defaultValue, String preComment) {
        this.sectionKey = sectionKey;
        this.key = path;
        this.path = sectionKey.getKey() + path;
        this.defaultValue = defaultValue;
        this.preComment = fixPreCommentNewLines(preComment);
    }

    Setting(SettingSectionKeys sectionKey, String path, Object defaultValue, String preComment, String postComment) {
        this.sectionKey = sectionKey;
        this.key = path;
        this.path = sectionKey.getKey() + path;
        this.defaultValue = defaultValue;
        this.preComment = fixPreCommentNewLines(preComment);
        this.postComment = postComment;
    }

    public static Setting findSetting(String search) {
        return valueOf(search.toUpperCase().replace("-", "_"));
    }

    private static void setDefaults() {
        config = YamlConfiguration.loadConfiguration(file);

        for (Setting set : Setting.values()) {
            addSetting(set.path, set.defaultValue);
        }

        save();
    }

    private String fixPreCommentNewLines(String str) {
        return str.replace("\n ", "\n" + sectionKey.getValueLead() + "# ");
    }

    private static void addSetting(String node, Object value) {
        if (config.get(node) == null) {
            config.set(node, value);
        }
    }

    private static void save() {
        Validate.notNull(file, "File cannot be null");

		if (config != null)
			try {
                Files.createParentDirs(file);

                StringBuilder data = new StringBuilder();

                data.append("##########################\n").append("#    TradeShop Config    #\n").append("##########################\n");
                Set<SettingSectionKeys> settingSectionKeys = Sets.newHashSet(SettingSectionKeys.values());

                for (Setting setting : values()) {
                    if (settingSectionKeys.contains(setting.sectionKey)) {
                        SettingSectionKeys tempSectionKey = setting.sectionKey;
                        while (tempSectionKey.hasParent() && settingSectionKeys.contains(tempSectionKey.getParent())) {
                            data.append(tempSectionKey.getParent().getFormattedHeader());
                            settingSectionKeys.remove(tempSectionKey.getParent());
                            tempSectionKey = tempSectionKey.getParent();
                        }
                        data.append(setting.sectionKey.getFormattedHeader());
                        settingSectionKeys.remove(setting.sectionKey);
                    }

                    if (!setting.preComment.isEmpty()) {
                        data.append(setting.sectionKey.getValueLead()).append("# ").append(setting.preComment).append("\n");
                    }

                    data.append(setting.sectionKey.getValueLead()).append(setting.key).append(": ").append(new Yaml().dump(setting.getSetting()));

                    if (!setting.postComment.isEmpty()) {
                        data.append(setting.postComment).append("\n");
                    }
                }

                Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);

                try {
                    writer.write(data.toString());
                } finally {
                    writer.close();
                }


			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static void reload() {
		try {
			if (!plugin.getDataFolder().isDirectory()) {
				plugin.getDataFolder().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create Config file! Disabling plugin!", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

        fixUp();

        setDefaults();
        config = YamlConfiguration.loadConfiguration(file);

        plugin.setUseInternalPerms(USE_INTERNAL_PERMISSIONS.getBoolean());
    }

    // Method to fix any values that have changed with updates
    private static void fixUp() {
        boolean changes = false;

        //Changes if CONFIG_VERSION is below 1, then sets config version to 1.0
        if (CONFIG_VERSION.getDouble() < 1.0) {
            CONFIG_VERSION.setSetting(1.0);
            changes = true;
        }

        // 2.2.2 Changed enable debug from true/false to integer
        if (!config.isInt(ENABLE_DEBUG.path)) {
            ENABLE_DEBUG.clearSetting();
            changes = true;
        }

        // 2.2.2 Better Sorted/potentially commented config
        if (CONFIG_VERSION.getDouble() < 1.1) {
            if (config.contains("itradeshop.owner")) {
                config.set(ITRADESHOP_OWNER.path, config.get("itradeshop.owner"));
                config.set("itradeshop.owner", null);
                changes = true;
            }

            if (config.contains("itradeshop.header")) {
                config.set(ITRADESHOP_HEADER.path, config.get("itradeshop.header"));
                config.set("itradeshop.header", null);
                changes = true;
            }

            if (config.contains("itradeshop.allow-explode")) {
                config.set(ITRADESHOP_EXPLODE.path, config.get("itradeshop.allow-explode"));
                config.set("itradeshop.allow-explode", null);
                changes = true;
            }

            if (config.contains("tradeshop.header")) {
                config.set(TRADESHOP_HEADER.path, config.get("tradeshop.header"));
                config.set("tradeshop.header", null);
                changes = true;
            }

            if (config.contains("tradeshop.allow-explode")) {
                config.set(TRADESHOP_EXPLODE.path, config.get("tradeshop.allow-explode"));
                config.set("tradeshop.allow-explode", null);
                changes = true;
            }

            if (config.contains("tradeshop.allow-hopper-export")) {
                config.set(TRADESHOP_HOPPER_EXPORT.path, config.get("tradeshop.allow-hopper-export"));
                config.set("tradeshop.allow-hopper-export", null);
                changes = true;
            }

            if (config.contains("bitradeshop.header")) {
                config.set(BITRADESHOP_HEADER.path, config.get("bitradeshop.header"));
                config.set("bitradeshop.header", null);
                changes = true;
            }

            if (config.contains("bitradeshop.allow-explode")) {
                config.set(BITRADESHOP_EXPLODE.path, config.get("bitradeshop.allow-explode"));
                config.set("bitradeshop.allow-explode", null);
                changes = true;
            }

            if (config.contains("bitradeshop.allow-hopper-export")) {
                config.set(BITRADESHOP_HOPPER_EXPORT.path, config.get("bitradeshop.allow-hopper-export"));
                config.set("bitradeshop.allow-hopper-export", null);
                changes = true;
            }


            CONFIG_VERSION.setSetting(1.1);
        }

        if (CONFIG_VERSION.getDouble() < 1.2) {
            if (config.contains("global-options.illegal-items")) {
                config.set(GLOBAL_ILLEGAL_ITEMS_LIST.path, config.get("global-options.illegal-items"));
                GLOBAL_ILLEGAL_ITEMS_LIST.setSetting(config.getStringList("global-options.illegal-items").removeAll(Arrays.asList("Air", "Void_Air", "Cave_Air")));
                config.set("global-options.illegal-items", null);
                changes = true;
            }

            CONFIG_VERSION.setSetting(1.2);
        }

        if (changes)
            save();
    }

	public static FileConfiguration getConfig() {
		return config;
	}

	public String toPath() {
		return path;
	}

    public void setSetting(Object obj) {
        config.set(toPath(), obj);
    }

    public void clearSetting() {
        config.set(toPath(), null);
    }

	public Object getSetting() {
		return config.get(toPath());
	}

	public String getString() {
		return config.getString(toPath());
	}

	public List<String> getStringList() {
		return config.getStringList(toPath());
	}

	public int getInt() {
		return config.getInt(toPath());
	}

	public double getDouble() {
		return config.getDouble(toPath());
	}

	public boolean getBoolean() {
		return config.getBoolean(toPath());
	}
}