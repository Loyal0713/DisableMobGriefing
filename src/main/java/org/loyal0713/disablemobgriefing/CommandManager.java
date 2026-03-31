package org.loyal0713.disablemobgriefing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final DisableMobGriefing plugin;

    public CommandManager(DisableMobGriefing plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();

        if (!sender.isOp() && config.getBoolean("require_op")) {
            sender.sendMessage("You must be an op to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /mobgriefing <mob> <true/false>");
            sender.sendMessage("Example: /mobgriefing creeper false");
            sender.sendMessage("Example: /mobgriefing creeper");
            return true;
        }

        String entityName = args[0].toLowerCase();

        if (entityName.equals("reload")) {
            plugin.reloadConfig();
            plugin.config = plugin.getConfig();
            sender.sendMessage("DisableMobGriefing config reloaded.");
            return true;
        }

        if (!config.contains(entityName + "_griefing")) {
            sender.sendMessage(entityName + " is not a valid entity.");
            return true;
        }

        if (args.length == 1) {
            boolean allowedToGrief = config.getBoolean(entityName + "_griefing");
            sender.sendMessage(entityName + " griefing is " + (allowedToGrief ? "enabled" : "disabled"));
            return true;
        }

        boolean allowedToGrief = Boolean.parseBoolean(args[1]);
        config.set(entityName + "_griefing", allowedToGrief);
        plugin.saveConfig();
        sender.sendMessage(entityName + " griefing is now " + (allowedToGrief ? "enabled" : "disabled"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            List<String> completions = config.getKeys(false).stream()
                    .filter(key -> key.endsWith("_griefing"))
                    .map(key -> key.replace("_griefing", ""))
                    .filter(name -> name.startsWith(partial))
                    .collect(Collectors.toList());
            if ("reload".startsWith(partial)) completions.add("reload");
            return completions;
        }

        if (args.length == 2) {
            return Arrays.asList("true", "false");
        }

        return Collections.emptyList();
    }
}
