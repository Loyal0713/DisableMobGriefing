package org.loyal0713.disablemobgriefing;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class DisableMobGriefing extends JavaPlugin implements Listener {

    FileConfiguration config = null;

    @Override
    public void onEnable() {

        config = getConfig();
        config.options().copyDefaults(false);
        config.addDefault("require_op", true);
        config.addDefault("verbose", false);
        config.addDefault("bee_griefing", false);
        config.addDefault("creeper_griefing", false);
        config.addDefault("explosion_player_damage", false);
        config.addDefault("minecart_tnt_griefing", false);
        config.addDefault("primed_tnt_griefing", false);
        config.addDefault("ender_crystal_griefing", false);
        config.addDefault("ender_dragon_griefing", false);
        config.addDefault("enderman_griefing", false);
        config.addDefault("falling_block_griefing", false);
        config.addDefault("fireball_griefing", false);
        config.addDefault("player_griefing", false);
        config.addDefault("rabbit_griefing", false);
        config.addDefault("ravager_griefing", false);
        config.addDefault("sheep_griefing", false);
        config.addDefault("snow_golem_griefing", false);
        config.addDefault("frog_griefing", false);
        config.addDefault("turtle_griefing", false);
        config.addDefault("silverfish_griefing", false);
        config.addDefault("villager_griefing", false);
        config.addDefault("wither_griefing", false);
        config.addDefault("wither_skull_griefing", false);
        config.addDefault("zombie_griefing", false);
        config.addDefault("zombie_villager_griefing", false);
        config.options().copyDefaults(true);
        saveConfig();

        // register event
        Bukkit.getPluginManager().registerEvents(this, this);

        // register command
        CommandManager commandManager = new CommandManager(this);
        getCommand("mobgriefing").setExecutor(commandManager);
        getCommand("mobgriefing").setTabCompleter(commandManager);
    }

    /*
    Most mobs that can grief are handled here.
     */
    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        EntityType entityType = event.getEntityType();
        boolean allowedToGrief = config.getBoolean(entityType.toString().toLowerCase() + "_griefing");
        if (!allowedToGrief) {
            if (config.getBoolean("verbose")) {
                getLogger().info(entityType + " griefing is disabled.");
            }
            event.setCancelled(true);
        }
    }

    /*
    Special case for things like ender crystals, fireballs, wither skulls, creepers, etc.
     */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        EntityType entityType = event.getEntityType();
        boolean allowedToGrief = config.getBoolean(entityType.toString().toLowerCase() + "_griefing");
        if (!allowedToGrief) {
            if (config.getBoolean("verbose")) {
                getLogger().info(entityType + " griefing is disabled.");
            }
            if (config.getBoolean("explosion_player_damage")) {
                event.blockList().clear();
            } else {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onDisable() {
        saveConfig();
    }
}
