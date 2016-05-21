package com.weebly.openboxtechnologies.hnrperms;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
        ResultSet result;
        boolean exists;
        try {
            result = Main.statement.executeQuery("SELECT COUNT(UUID)" + " FROM Perms" +
                    " WHERE UUID ='"+ e.getPlayer().getUniqueId().toString() +"';");
            result.next();
            exists = result.getInt(1) > 0;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return;
        }

        try {
            if(!exists) {
                Main.statement.executeUpdate("INSERT INTO Perms (UUID,Rank) VALUES ('" +
                        e.getPlayer().getUniqueId().toString() + "','" + Main.rankOrder.get(0) + "');");
            }
            result = Main.statement.executeQuery("SELECT Rank FROM Perms WHERE UUID = '" + e.getPlayer().getUniqueId().toString() + "';");
            result.next();
            String[] playerRanksList = result.getString("Rank").split(" ");
            for (String t : playerRanksList) {
                ArrayList<String> rankPermsList = Main.rankPerms.get(t);
                for (String s : rankPermsList) {
                    Main.playerHashmap.get(e.getPlayer().getUniqueId()).setPermission(s, true);
                }
            }

        } catch (SQLException f) {
            f.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Main.playerHashmap.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        Main.playerHashmap.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        String msg = e.getMessage();
    }
}
