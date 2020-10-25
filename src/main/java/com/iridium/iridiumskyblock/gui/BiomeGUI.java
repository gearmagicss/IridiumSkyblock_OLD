package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.*;
import com.iridium.iridiumskyblock.configs.Config;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BiomeGUI extends GUI implements Listener {

    private int i = 0;

    private int slot = 0;

    public int page;

    public BiomeGUI root;

    public Map<Integer, BiomeGUI> pages = new HashMap<>();

    public Map<Integer, XBiome> biomes = new HashMap<>();

    public BiomeGUI(Island island) {
        IridiumSkyblock.getInstance().registerListeners(this);
        int size = (int) (Math.floor(Biome.values().length / ((double) IridiumSkyblock.getInventories().biomeGUISize - 9)) + 1);
        for (int i = 1; i <= size; i++) {
            pages.put(i, new BiomeGUI(island, i, this));
        }
    }

    public BiomeGUI(Island island, int page, BiomeGUI root) {
        super(island, IridiumSkyblock.getInventories().biomeGUISize, IridiumSkyblock.getInventories().biomeGUITitle);
        this.page = page;
        this.root = root;
    }

    @Override
    public void addContent() {
        if (getInventory().getViewers().isEmpty()) return;
        this.i = 0;
        this.slot = 0;
        super.addContent();
        IridiumSkyblock.getConfiguration().islandBiomes.keySet().stream().sorted(Comparator.comparing(XBiome::toString)).forEach((biome) -> {
            if (biome.parseBiome() != null) {
                if (i >= 45 * (page - 1) && slot < 45) {
                    Config.BiomeConfig biomeConfig = IridiumSkyblock.getConfiguration().islandBiomes.get(biome);
                    ItemStack itemStack = Utils.makeItem(IridiumSkyblock.getInventories().biome, Arrays.asList(
                            new Utils.Placeholder("price", Utils.NumberFormatter.format(biomeConfig.price)),
                            new Utils.Placeholder("crystals", Utils.NumberFormatter.format(biomeConfig.crystals)),
                            new Utils.Placeholder("biome", WordUtils.capitalize(biome.name().toLowerCase().replace("_", " ")))));
                    Material icon = biomeConfig.icon.parseMaterial();
                    if (icon != null) itemStack.setType(icon);
                    setItem(slot, itemStack);
                    biomes.put(slot, biome);
                    slot++;
                }
                i++;
            }
        });
        setItem(getInventory().getSize() - 3, Utils.makeItem(IridiumSkyblock.getInventories().nextPage));
        if (IridiumSkyblock.getInventories().backButtons)
            setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.getInventories().back));
        setItem(getInventory().getSize() - 7, Utils.makeItem(IridiumSkyblock.getInventories().previousPage));
    }

    public void sendBiomeChangeMessage(String biome, Player p) {
        for (String member : getIsland().getMembers()) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().biomeChanged
                        .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)
                        .replace("%biome%", biome).replace("%player%", p.getName())));
            }
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (getInventory() == null) {
            for (BiomeGUI gui : pages.values()) {
                gui.onInventoryClick(e);
            }
        } else {
            if (e.getInventory().equals(getInventory())) {
                e.setCancelled(true);
                Player p = (Player) e.getWhoClicked();
                if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
                if (e.getSlot() == getInventory().getSize() - 3) {
                    if (root.pages.containsKey(page + 1)) {
                        p.openInventory(root.pages.get(page + 1).getInventory());
                    }
                }
                if (e.getSlot() == getInventory().getSize() - 7) {
                    if (root.pages.containsKey(page - 1)) {
                        p.openInventory(root.pages.get(page - 1).getInventory());
                    }
                }
                if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInventories().backButtons) {
                    p.openInventory(getIsland().getIslandMenuGUI().getInventory());
                }
                if (biomes.containsKey(e.getSlot())) {
                    Config.BiomeConfig biomeConfig = IridiumSkyblock.getConfiguration().islandBiomes.get(biomes.get(e.getSlot()));
                    Utils.BuyResponce responce = Utils.canBuy(p, IridiumSkyblock.getConfiguration().islandBiomes.getOrDefault(biomes.get(e.getSlot()), new Config.BiomeConfig()).price, biomeConfig.crystals);
                    if (responce == Utils.BuyResponce.SUCCESS) {
                        getIsland().setBiome(biomes.get(e.getSlot()));
                        p.sendMessage(Utils.color(IridiumSkyblock.getMessages().biomePurchased
                                .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)
                                .replace("%biome%", WordUtils.capitalize(biomes.get(e.getSlot()).name().toLowerCase().replace("_", " ")))
                                .replace("%crystals%", biomeConfig.crystals + "")
                                .replace("%money", Utils.NumberFormatter.format(biomeConfig.price))));
                        sendBiomeChangeMessage(WordUtils.capitalize(biomes.get(e.getSlot()).name().toLowerCase().replace("_", " ")), p);
                    } else {
                        p.sendMessage(Utils.color(responce == Utils.BuyResponce.NOT_ENOUGH_VAULT ? IridiumSkyblock.getMessages().cantBuy : IridiumSkyblock.getMessages().notEnoughCrystals.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                }
            }
        }
    }
}