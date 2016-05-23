package com.weebly.openboxtechnologies.hnrperms;

/**
 * Created by zhiyuanqi on 17/05/16.
 */

import org.bukkit.ChatColor;

public class ChatMessages {
    final String permError = returnAlternateColorString("&e&lHnRPerms&7&l> &9To use this feature, you must be rank &8(&cADMIN&8)&9!");

    final String suffix = returnAlternateColorString("&9!");

    final String set1 = returnAlternateColorString("&e&lHnRPerms&7&l> &9You have set &c");
    final String set2 = returnAlternateColorString("&9's rank to &b");

    final String rankAdd1 = returnAlternateColorString("&e&lHnRPerms&7&l> &9You have added &c");
    final String rankAdd2 = returnAlternateColorString(" to &b");

    final String rankRM1 = returnAlternateColorString("&e&lHnRPerms&7&l> &9You have removed &c");
    final String rankRM2 = returnAlternateColorString(" from &b");

    final String playerSet = returnAlternateColorString("&e&lHnRPerms&7&l> &9Your rank has been set to &b");
    final String playerAdd = returnAlternateColorString("&e&lHnRPerms&7&l> &9You have been added to &b");
    final String playerRM = returnAlternateColorString("&e&lHnRPerms&7&l> &9You have been removed from &b");

    final String errorSet = returnAlternateColorString("&e&lHnRPerms&7&l> &9This player's rank cannot be changed!");

    private final String returnAlternateColorString(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
