package com.weebly.openboxtechnologies.hnrperms;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Listener implements org.bukkit.event.Listener {

    private JavaPlugin plugin;

    Listener(JavaPlugin plugin) {

        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
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
                    Main.playerRankmap.put(e.getPlayer().getUniqueId(), new ArrayList<>(Arrays.asList(playerRanksList)));
                    String ranksAsString = "";
                    for (String t : playerRanksList) {
                        ArrayList<String> rankPermsList = Main.rankPerms.get(t);
                        for (String s : rankPermsList) {
                            Main.playerHashmap.get(e.getPlayer().getUniqueId()).setPermission(s, true);
                        }
                        ranksAsString += (t + " ");
                    }
                    Main.getPlayersConfig().set(e.getPlayer().getUniqueId().toString(), ranksAsString);

                } catch (SQLException f) {
                    f.printStackTrace();
                }
            }
        };
        r.runTaskAsynchronously(plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        cleanupPlayerQuit(e.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        cleanupPlayerQuit(e.getPlayer());
    }

    private void cleanupPlayerQuit(Player p) {
        Main.playerHashmap.remove(p.getUniqueId());
        Main.playerRankmap.remove(p.getUniqueId());
    }
}
