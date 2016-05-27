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

import static com.weebly.openboxtechnologies.hnrperms.hnrperms.playerRankmap;

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
                hnrperms.playerHashmap.put(e.getPlayer().getUniqueId(), e.getPlayer().addAttachment(plugin));
                ResultSet result;
                boolean exists;
                try {
                    result = hnrperms.statement.executeQuery("SELECT COUNT(UUID)" + " FROM Perms" +
                            " WHERE UUID ='"+ e.getPlayer().getUniqueId().toString() +"';");
                    result.next();
                    exists = result.getInt(1) > 0;
                } catch (SQLException exception) {
                    exception.printStackTrace();
                    return;
                }

                try {
                    if(!exists) {
                        hnrperms.statement.executeUpdate("INSERT INTO Perms (UUID,Rank) VALUES ('" +
                                e.getPlayer().getUniqueId().toString() + "','" + hnrperms.rankOrder.get(0) + "');");
                    }
                    result = hnrperms.statement.executeQuery("SELECT Rank FROM Perms WHERE UUID = '" + e.getPlayer().getUniqueId().toString() + "';");
                    result.next();
                    String[] playerRanksList = result.getString("Rank").split(" ");
                    playerRankmap.put(e.getPlayer().getUniqueId(), new ArrayList<>(Arrays.asList(playerRanksList)));
                    String ranksAsString = "";
                    for (String t : playerRanksList) {
                        ArrayList<String> rankPermsList = hnrperms.rankPerms.get(t);
                        for (String s : rankPermsList) {
                            hnrperms.playerHashmap.get(e.getPlayer().getUniqueId()).setPermission(s, true);
                        }
                        ranksAsString += (t + " ");
                    }
                    hnrperms.getPlayersConfig().set(e.getPlayer().getUniqueId().toString(), ranksAsString);

                } catch (SQLException f) {
                    f.printStackTrace();
                }

                int rank = 0;
                for (String t : playerRankmap.get(e.getPlayer().getUniqueId())) {
                    if (hnrperms.rankOrder.indexOf(t) > rank) {
                        rank = hnrperms.rankOrder.indexOf(t);
                    }
                }
                hnrperms.highestPlayerRank.put(e.getPlayer().getUniqueId(), hnrperms.rankOrder.get(rank));
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
        hnrperms.playerHashmap.remove(p.getUniqueId());
        playerRankmap.remove(p.getUniqueId());
    }
}
