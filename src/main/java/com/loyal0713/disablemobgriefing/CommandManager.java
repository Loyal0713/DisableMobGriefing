package com.loyal0713.disablemobgriefing;

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

        // no args, show usage
        if (args.length == 0) {
            sender.sendMessage("Usage: /mobgriefing <mob> <true/false>");
            sender.sendMessage("Example: /mobgriefing creeper false");
            sender.sendMessage("Example: /mobgriefing creeper");
            return true;
        }

        String argument = args[0].toLowerCase();

        // reload command
        if (argument.equals("reload")) {
            plugin.reloadConfig();
            plugin.config = plugin.getConfig();
            sender.sendMessage("DisableMobGriefing config reloaded.");
            return true;
        }

        // normalize argument to key — direct keys first, then mob griefing keys
        String configKey;
        if (config.contains(argument)) {
            configKey = argument;
        } else {
            configKey = argument + "_griefing";
        }

        if (!config.contains(configKey)) {
            sender.sendMessage(argument + " is not a valid key.");
            return true;
        }

        // no value provided, show current value of key
        if (args.length == 1) {
            boolean allowedToGrief = config.getBoolean(configKey);
            sender.sendMessage(configKey + " is " + (allowedToGrief ? "enabled" : "disabled"));
            return true;
        }

        // set value of key
        boolean allowedToGrief = Boolean.parseBoolean(args[1]);
        config.set(configKey, allowedToGrief);
        plugin.saveConfig();
        sender.sendMessage(configKey + " is now " + (allowedToGrief ? "enabled" : "disabled"));
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
                    .collect(Collectors.toList());
            // Add direct boolean config keys
            for (String key : new String[]{"verbose", "explosions_damage_players", "reload"}) {
                if (key.startsWith(partial)) {
                    completions.add(key);
                }
            }
            // Filter completions by partial
            completions = completions.stream()
                    .filter(name -> name.startsWith(partial))
                    .collect(Collectors.toList());
            return completions;
        }

        if (args.length == 2) {
            return Arrays.asList("true", "false");
        }

        return Collections.emptyList();
    }
}
