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
    final String playerDefine = returnAlternateColorString("&e&lCommand&7&l> &9You have incorrectly used this command!\n" +
            "&e&lCommand&7&l> &9You must define a player!\n" +
            "&e&lCommand&7&l> &a/setrank <player> <rank>");
    final String rankDefine = returnAlternateColorString("&e&lCommand&7&l> &9You have incorrectly used this command!\n" +
            "&e&lCommand&7&l> &9You must define a rank!\n" +
            "&e&lCommand&7&l> &a/setrank <player> <rank>");
    final String invalidPlayer = returnAlternateColorString("&e&lCommand&7&l> &9You have incorrectly used this command!\n" +
            "&e&lCommand&7&l> &9The player you have defined could not be found!\n" +
            "&e&lCommand&7&l> &a/setrank <player> <rank>");
    final String invalidRank = returnAlternateColorString("&e&lCommand&7&l> &9You have incorrectly used this command!\n" +
            "&e&lCommand&7&l> &9The rank you have defined could not be found!\n" +
            "&e&lCommand&7&l> &a/setrank <player> <rank>");
    final String unknownArgument = returnAlternateColorString("&e&lCommand&7&l> &9You have incorrectly used this command!\n" +
            "&e&lCommand&7&l> &9The argument you have defined could not be identified!\n" +
            "&e&lCommand&7&l> &a/setrank <player> <rank>");

    final String offlineWarning = returnAlternateColorString("&e&lCommand&7&l> &9AddRank/SetRank/RemoveRank is ignored!");
    final String offlineSuccessful1 = returnAlternateColorString("&e&lCommand&7&l> &9UUID: ");
    final String offlineSuccessful2 = returnAlternateColorString(" &9has been updated with ranks: &c");

    private final String returnAlternateColorString(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
