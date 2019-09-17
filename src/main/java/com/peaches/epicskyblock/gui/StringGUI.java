package com.peaches.epicskyblock.gui;

import com.peaches.epicskyblock.EpicSkyblock;
import com.peaches.epicskyblock.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Field;

public class StringGUI implements Listener {

    private Inventory inventory;

    private Field field;
    private Object instance;

    public StringGUI(Field field, Object instance) {
        this.field = field;
        this.instance = instance;

        inventory = Bukkit.createInventory(null, InventoryType.ANVIL, "Editing " + field.getName());

        inventory.setItem(0, Utils.makeItem(Material.PAPER, 1, 0, "&b&l" + field.getName()));
        inventory.setItem(2, Utils.makeItem(Material.PAPER, 1, 0, "&b&l" + field.getName()));

        Bukkit.getPluginManager().registerEvents(this, EpicSkyblock.getInstance());
    }

    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(inventory)) {
            e.setCancelled(true);
            EpicSkyblock.getInstance().saveConfigs();
            EpicSkyblock.getInstance().loadConfigs();
        }
    }
}
