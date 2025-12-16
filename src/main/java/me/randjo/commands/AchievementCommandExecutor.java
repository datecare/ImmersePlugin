package me.randjo.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.randjo.statistics.Achievements.createBasicMenu;

public class AchievementCommandExecutor implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "achievements":
                return handleACH(sender, args);
            default:
                return false;
        }
    }

    private boolean handleACH(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            createBasicMenu(player);
            return true;
        }
        return false;

    }
}

