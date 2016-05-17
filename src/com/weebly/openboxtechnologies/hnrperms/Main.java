package com.weebly.openboxtechnologies.hnrperms;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by zhiyuanqi on 17/05/16.
 */

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        new Listener(this);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public boolean onCommand(CommandSender e, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("templatecmd")) {
            if (e instanceof Player) {
                e.sendMessage("This command works!");
            }
            return true;
        }
        return false;
    }

}
