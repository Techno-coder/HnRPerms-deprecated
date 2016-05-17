package com.weebly.openboxtechnologies.hnrperms;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by zhiyuanqi on 17/05/16.
 */

public class Listener implements org.bukkit.event.Listener {

    private JavaPlugin plugin;

    public Listener(JavaPlugin plugin) {

        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }
}
