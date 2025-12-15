package me.randjo.commands;

import me.randjo.jobs.JobsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.randjo.jobs.JobsListener.createJobShop;
import static me.randjo.restriction.RestrictionListener.createJobMenu;

public class JobCommandExecutor implements CommandExecutor {
    private JobsPlugin jobPlugin;
    public JobCommandExecutor(JobsPlugin jobPlugin) {
        this.jobPlugin = jobPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "jobs":
                return handleJOBS(sender, args);
            case "setjob":
                return handleSETJOBS(sender, args);
            case "addjob":
                return handleADDJOB(sender, args);
            case "removejob":
                return handleREMOVEJOB(sender, args);
            case "jobshop":
                return handleJOBSHOP(sender, args);

            default:
                return false;
        }
    }

    private boolean handleJOBSHOP(CommandSender sender, String[] args) {
        if (sender instanceof Player player) createJobShop(player);
        return true;
    }

    public boolean handleJOBS(CommandSender sender, String[] args) {
        if (sender instanceof Player player)  createJobMenu(player);
        return true;
    }

    public boolean handleSETJOBS(CommandSender sender, String[] args)
    {
        jobPlugin.setJobData(args[0], args[1], Long.parseLong(args[2]), Double.parseDouble(args[3]));
        return true;
    }

    public boolean handleADDJOB(CommandSender sender, String[] args)
    {
        jobPlugin.addSelectedJob(args[0], args[1]);
        return true;
    }

    public boolean handleREMOVEJOB(CommandSender sender, String[] args)
    {
        jobPlugin.removeSelectedJob(args[0], args[1]);
        return true;
    }
}
