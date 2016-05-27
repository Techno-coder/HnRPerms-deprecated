package com.weebly.openboxtechnologies.hnrperms;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class hnrperms extends JavaPlugin {

    private static final ChatMessages chatMessages = new ChatMessages();

    private Connection connection;
    private String host, database, username, password;
    private int port;

    private static FileConfiguration ladder, sql, players, ranks, fixed;
    private static File playersf;

    private List<String> helpChat = new ArrayList<>();
    static List<String> rankOrder = new ArrayList<>();

    static Statement statement;
    static HashMap<UUID,PermissionAttachment> playerHashmap = new HashMap<>();
    static HashMap<UUID,ArrayList<String>> playerRankmap = new HashMap<>();
    static HashMap<String, ArrayList<String>> rankPerms = new HashMap<>();
    static HashMap<UUID, String> highestPlayerRank = new HashMap<>();
    private static ArrayList<String> fixedRankPlayers = new ArrayList<>();

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
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Perms(UUID varchar(36), Rank varchar(255));");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        helpChat = getConfig().getStringList("message");
        List<String> tempOrder = getLadderConfig().getStringList("grouporder");
        for (String t : tempOrder) {
            rankOrder.addAll(getLadderConfig().getStringList(t));
            ArrayList<String> localRankList = new ArrayList<>();
            localRankList.addAll(getLadderConfig().getStringList(t));
            for (int i = 0; i < localRankList.size(); i++) {
                ArrayList<String> tempCastConversion = new ArrayList<>();
                for (int j = i; j >= 0; j--) {
                    List<String> tempCurrentRankPerms = getRanksConfig().getStringList(localRankList.get(j));
                    tempCastConversion.addAll(tempCurrentRankPerms);
                }
                rankPerms.put(localRankList.get(i), tempCastConversion);
            }
        }
        List<String> tempFixedConfig = getFixedConfig().getStringList("players");
        fixedRankPlayers.addAll(tempFixedConfig);
    }

    @Override
    public void onDisable() {
        try {
            players.save(playersf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        playerHashmap.clear();
    }

    @Override
    public boolean onCommand(CommandSender e, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("hnrperms")) {
            return false;
        }

        if (!e.hasPermission("rank.admin")) {
            e.sendMessage(chatMessages.permError);
            return true;
        }

        if (args.length == 0) {
            for (String i : helpChat) {
                e.sendMessage(ChatColor.translateAlternateColorCodes('&', i));
            }
            return true;
        } else if (args.length == 1) {
            e.sendMessage(chatMessages.playerDefine);
            return true;
        } else if (args.length == 2) {
            e.sendMessage(chatMessages.rankDefine);
            return true;
        }

        if (getServer().getPlayer(args[1]) == null) {
            if (e.hasPermission("rank.console")) {
                e.sendMessage(chatMessages.offlineWarning);
                updateOfflinePlayer(UUID.fromString(args[1]), args[2]);
                e.sendMessage(chatMessages.offlineSuccessful1 + args[1] + chatMessages.offlineSuccessful2 + args[2] + chatMessages.suffix);
                return true;
            }
            e.sendMessage(chatMessages.invalidPlayer);
            return true;
        } else if(!rankOrder.contains(args[2])) {
            e.sendMessage(chatMessages.invalidRank);
            return true;
        }

        if (fixedRankPlayers.contains(args[1])) {
            e.sendMessage(chatMessages.errorSet);
            return true;
        }

        if (args[0].equalsIgnoreCase("setrank")) {
            Player p = getServer().getPlayer(args[1]);
            playerRankmap.get(p.getUniqueId()).clear();
            playerRankmap.get(p.getUniqueId()).add(args[2]);
            updatePlayerRank(args[2], p);
            hnrperms.getPlayersConfig().set(p.getUniqueId().toString(), args[2]);
            e.sendMessage(chatMessages.set1 + args[1] + chatMessages.set2 + args[2] + chatMessages.suffix);
            p.sendMessage(chatMessages.playerSet + args[2] + chatMessages.suffix);
        } else if (args[0].equalsIgnoreCase("addrank")) {
            Player p = getServer().getPlayer(args[1]);
            String ranksAsString = "";
            playerRankmap.get(p.getUniqueId()).add(args[2]);
            for (String i : playerRankmap.get(p.getUniqueId())) {
                ranksAsString += (i + " ");
            }
            updatePlayerRank(ranksAsString, p);
            hnrperms.getPlayersConfig().set(p.getUniqueId().toString(), ranksAsString);
            e.sendMessage(chatMessages.rankAdd1 + args[1] + chatMessages.rankAdd2 + args[2] + chatMessages.suffix);
            p.sendMessage(chatMessages.playerAdd + args[2] + chatMessages.suffix);

        } else if (args[0].equalsIgnoreCase("removerank")) {
            Player p = getServer().getPlayer(args[1]);
            String ranksAsString = "";
            playerRankmap.get(p.getUniqueId()).remove(args[2]);
            for (String i : playerRankmap.get(p.getUniqueId())) {
                ranksAsString += (i + " ");
            }
            if (ranksAsString.equalsIgnoreCase("")) {
                ranksAsString = rankOrder.get(0);
            }
            updatePlayerRank(ranksAsString, p);
            hnrperms.getPlayersConfig().set(p.getUniqueId().toString(), ranksAsString);
            e.sendMessage(chatMessages.rankRM1 + args[1] + chatMessages.rankRM2 + args[2] + chatMessages.suffix);
            p.sendMessage(chatMessages.playerRM + args[2] + chatMessages.suffix);
        } else {
            e.sendMessage(chatMessages.unknownArgument);
        }

        return true;
    }

    public String getHighestRank(Player p) {
        return highestPlayerRank.get(p.getUniqueId());
    }
    public ArrayList<String> getRanksList() {
        ArrayList<String> modifiable = new ArrayList<>();
        modifiable.addAll(rankOrder);
        return modifiable;
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
        boolean mkdirs = true;
        try {
            if (!getDataFolder().exists()) {
                mkdirs = getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                saveDefaultConfig();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        File ladderf, sqlf, ranksf, fixedf;

        ladderf = new File(getDataFolder(), "Ladder.yml");
        sqlf = new File(getDataFolder(), "MySQL.yml");
        playersf = new File(getDataFolder(), "Players.yml");
        ranksf = new File(getDataFolder(), "Ranks.yml");
        fixedf = new File(getDataFolder(), "FixedRank.yml");

        if (!ladderf.exists()) {
            mkdirs = ladderf.getParentFile().mkdirs();
            saveResource("Ladder.yml", false);
        }

        if (!sqlf.exists()) {
            mkdirs = sqlf.getParentFile().mkdirs();
            saveResource("MySQL.yml", false);
        }

        if (!playersf.exists()) {
            mkdirs = playersf.getParentFile().mkdirs();
            saveResource("Players.yml", false);
        }

        if (!ranksf.exists()) {
            mkdirs = ranksf.getParentFile().mkdirs();
            saveResource("Ranks.yml", false);
        }

        if (!fixedf.exists()) {
            mkdirs = fixedf.getParentFile().mkdirs();
            saveResource("FixedRank.yml", false);
        }

        if (!mkdirs) {
            getLogger().severe("Some config files were unable to be created!");
        }

        ladder = new YamlConfiguration();
        sql = new YamlConfiguration();
        players = new YamlConfiguration();
        ranks = new YamlConfiguration();
        fixed = new YamlConfiguration();

        try {
            ladder.load(ladderf);
            sql.load(sqlf);
            players.load(playersf);
            ranks.load(ranksf);
            fixed.load(fixedf);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    private void updatePlayerRank(String ranks, Player p) {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    statement.executeUpdate("UPDATE Perms SET Rank = '" + ranks + "' WHERE UUID = '" + p.getUniqueId() + "';");
                } catch (SQLException f) {
                    f.printStackTrace();
                }
            }
        };
        r.runTaskAsynchronously(this);
        p.removeAttachment(playerHashmap.get(p.getUniqueId()));
        playerHashmap.remove(p.getUniqueId());
        playerHashmap.put(p.getUniqueId(), p.addAttachment(this));

        int rank = 0;
        for (String t : playerRankmap.get(p.getUniqueId())) {
            ArrayList<String> rankPermsList = rankPerms.get(t);
            for (String s : rankPermsList) {
                hnrperms.playerHashmap.get(p.getUniqueId()).setPermission(s, true);
            }
            if (rankOrder.indexOf(t) > rank) {
                rank = rankOrder.indexOf(t);
            }
        }
        highestPlayerRank.put(p.getUniqueId(), rankOrder.get(rank));
        RankChangedEvent e = new RankChangedEvent(p.getUniqueId());
        getServer().getPluginManager().callEvent(e);
    }
    private void updateOfflinePlayer(UUID id, String ranks) {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                ResultSet result;
                boolean exists;
                try {
                    result = hnrperms.statement.executeQuery("SELECT COUNT(UUID)" + " FROM Perms" +
                            " WHERE UUID ='"+ id +"';");
                    result.next();
                    exists = result.getInt(1) > 0;

                    if(!exists) {
                        hnrperms.statement.executeUpdate("INSERT INTO Perms (UUID,Rank) VALUES ('" +
                                id.toString() + "','" + ranks + "');");
                    } else {
                        statement.executeUpdate("UPDATE Perms SET Rank = '" + ranks + "' WHERE UUID = '" + id.toString() + "';");
                    }

                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        };
        r.runTaskAsynchronously(this);
    }

    private FileConfiguration getLadderConfig() {
        return this.ladder;
    }
    private FileConfiguration getSQLConfig() {
        return this.sql;
    }
    static FileConfiguration getPlayersConfig() {
        return players;
    }
    private FileConfiguration getRanksConfig() {
        return this.ranks;
    }
    private FileConfiguration getFixedConfig() { return this.fixed; }
}
