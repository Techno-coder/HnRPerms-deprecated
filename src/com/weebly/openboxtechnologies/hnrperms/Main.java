package com.weebly.openboxtechnologies.hnrperms;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhiyuanqi on 17/05/16.
 */

public class Main extends JavaPlugin {

    private static final ChatMessages chatMessages = new ChatMessages();

    private Connection connection;
    private String host, database, username, password;
    private int port;

    private File ladderf, sqlf, playersf, ranksf, chatf;
    private FileConfiguration ladder, sql, players, ranks, chat;

    private List<String> helpChat;
    public static List<String> rankOrder;

    public static Statement statement;
    public static HashMap<UUID,PermissionAttachment> playerHashmap = new HashMap<>();
    public static HashMap<UUID,ArrayList<String>> playerRankmap = new HashMap<>();
    public static HashMap<String, ArrayList<String>> rankPerms = new HashMap<>();

    //TODO Add SQL Permission Storage and Rank Storage

    @Override
    public void onEnable() {
        new Listener(this);
        createFiles();
        host = getSQLConfig().getString("host");
        database = getSQLConfig().getString("database");
        username = getSQLConfig().getString("username");
        password = getSQLConfig().getString("password");
        port = getSQLConfig().getInt("port");
        try {
            openConnection();
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        helpChat = getConfig().getStringList("message");
        if (helpChat == null) {
            helpChat.clear();
            helpChat.add(0, chatMessages.helpChat);
        }
        List<String> tempOrder = getLadderConfig().getStringList("grouporder");
        for (String t : tempOrder) {
            rankOrder.addAll(getLadderConfig().getStringList(t));
            ArrayList<String> localRankList = new ArrayList<>();
            localRankList.addAll(getLadderConfig().getStringList(t));
            for (String s : localRankList) {
                List<String> tempCurrentRankPerms = getRanksConfig().getStringList(s);
                ArrayList<String> tempCastConversion = new ArrayList<>();
                tempCastConversion.addAll(tempCurrentRankPerms);
                rankPerms.put(s, tempCastConversion);
            }
        }
    }

    @Override
    public void onDisable() {
        playerHashmap.clear();
    }

    @Override
    public boolean onCommand(CommandSender e, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("hnrperms")) {
            return false;
        }

        if (!e.hasPermission("rank.admin")) {
            e.sendMessage(chatMessages.permError);
        }

        if (args.length == 0) {
            for (String i : helpChat) {
                ChatColor.translateAlternateColorCodes('&', i);
                e.sendMessage(i);
            }
            return true;
        } else if (args.length == 1) {
            //TODO Missing argument PLAYER
            return true;
        } else if (args.length == 2) {
            //TODO Missing argument RANK
            return true;
        }

        if (args[0].equalsIgnoreCase("setrank")) {
            //TODO Handle Set rank
        } else if (args[0].equalsIgnoreCase("addrank")) {
            //TODO Handle Add rank
        } else if (args[0].equalsIgnoreCase("removerank")) {
            //TODO Handle Remove rank
        } else {
            //TODO Handle unknown argument
        }

        return true;
    }

    private void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
        }
    }

    private void createFiles() {
        ladderf = new File(getDataFolder(), "Ladder.yml");
        sqlf = new File(getDataFolder(), "MySQL.yml");
        playersf = new File(getDataFolder(), "Players.yml");
        ranksf = new File(getDataFolder(), "Ranks.yml");
        chatf = new File(getDataFolder(), "Chat.yml");

        if (!ladderf.exists()) {
            ladderf.getParentFile().mkdirs();
            saveResource("Ladder.yml", false);
        }

        if (!sqlf.exists()) {
            sqlf.getParentFile().mkdirs();
            saveResource("MySQL.yml", false);
        }

        if (!playersf.exists()) {
            playersf.getParentFile().mkdirs();
            saveResource("Players.yml", false);
        }

        if (!ranksf.exists()) {
            ranksf.getParentFile().mkdirs();
            saveResource("Ranks.yml", false);
        }

        if (!chatf.exists()) {
            chatf.getParentFile().mkdirs();
            saveResource("Chat.yml", false);
        }

        ladder = new YamlConfiguration();
        sql = new YamlConfiguration();
        players = new YamlConfiguration();
        ranks = new YamlConfiguration();
        chat = new YamlConfiguration();

        try {
            ladder.load(ladderf);
            sql.load(sqlf);
            players.load(playersf);
            ranks.load(ranksf);
            chat.load(chatf);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getLadderConfig() {
        return this.ladder;
    }
    public FileConfiguration getSQLConfig() {
        return this.sql;
    }
    public FileConfiguration getPlayersConfig() {
        return this.players;
    }
    public FileConfiguration getRanksConfig() {
        return this.ranks;
    }
    public FileConfiguration getChatConfig() {
        return this.chat;
    }

}
