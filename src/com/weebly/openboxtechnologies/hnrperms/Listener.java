package com.weebly.openboxtechnologies.hnrperms;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Main.playerHashmap.put(e.getPlayer().getUniqueId(), e.getPlayer().addAttachment(plugin));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Main.playerHashmap.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        Main.playerHashmap.remove(e.getPlayer().getUniqueId());
    }
}
