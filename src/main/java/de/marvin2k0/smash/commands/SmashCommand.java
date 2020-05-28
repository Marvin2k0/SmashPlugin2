package de.marvin2k0.smash.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SmashCommand implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        sender.sendMessage("§9Smash§7Plugin by §9Marvin2k0");
        return true;
    }
}
