package org.loyal0713.disablemobgriefing;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class DisableMobGriefing extends JavaPlugin implements Listener {

    FileConfiguration config = null;

    @Override
    public void onEnable() {

        config = getConfig();

        // migrate renamed key from v1.1.1
        if (config.contains("explosion_player_damage")) {
            config.set("explosions_damage_players", config.getBoolean("explosion_player_damage"));
            config.set("explosion_player_damage", null);
            saveConfig();
        }

        config.options().copyDefaults(false);
        config.addDefault("verbose", false);
        config.addDefault("bee_griefing", true);
        config.addDefault("creeper_griefing", true);
        config.addDefault("minecart_tnt_griefing", true);
        config.addDefault("primed_tnt_griefing", true);
        config.addDefault("ender_crystal_griefing", true);
        config.addDefault("ender_dragon_griefing", true);
        config.addDefault("enderman_griefing", true);
        config.addDefault("falling_block_griefing", true);
        config.addDefault("fireball_griefing", true);
        config.addDefault("player_griefing", true);
        config.addDefault("rabbit_griefing", true);
        config.addDefault("ravager_griefing", true);
        config.addDefault("sheep_griefing", true);
        config.addDefault("snow_golem_griefing", true);
        config.addDefault("frog_griefing", true);
        config.addDefault("turtle_griefing", true);
        config.addDefault("silverfish_griefing", true);
        config.addDefault("villager_griefing", true);
        config.addDefault("wither_griefing", true);
        config.addDefault("wither_skull_griefing", true);
        config.addDefault("zombie_griefing", true);
        config.addDefault("zombie_villager_griefing", true);
        config.addDefault("explosions_damage_players", true);
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
     * Handles mobs that grief by changing blocks (endermen, sheep, rabbits, villagers, etc.)
     * Defaults to true (allow) for any entity not explicitly configured.
     */
    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        EntityType entityType = event.getEntityType();
        boolean allowedToGrief = config.getBoolean(normalizeEntityName(entityType) + "_griefing", true);
        if (!allowedToGrief) {
            if (config.getBoolean("verbose")) {
                getLogger().info(entityType + " griefing is disabled.");
            }
            event.setCancelled(true);
        }
    }

    /*
     * Handles mobs that grief via explosions (creepers, TNT, fireballs, wither skulls, ender crystals, etc.)
     * When griefing is disabled, clears the block list so the explosion still plays (sound/particles/player
     * damage intact) but no blocks are broken. Player damage is handled separately by onEntityDamage.
     */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        EntityType entityType = event.getEntityType();
        boolean allowedToGrief = config.getBoolean(normalizeEntityName(entityType) + "_griefing", true);
        if (!allowedToGrief) {
            if (config.getBoolean("verbose")) {
                getLogger().info(entityType + " griefing is disabled.");
            }
            event.blockList().clear();
        }
    }

    /*
     * Normalizes entity type names for consistent config key lookups across API versions.
     * END_CRYSTAL (1.21+) -> ender_crystal, TNT -> primed_tnt, TNT_MINECART -> minecart_tnt
     */
    private String normalizeEntityName(EntityType entityType) {
        String name = entityType.toString().toLowerCase();
        if (name.equals("end_crystal")) return "ender_crystal";
        if (name.equals("tnt")) return "primed_tnt";
        if (name.equals("tnt_minecart")) return "minecart_tnt";
        return name;
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    /*
     * Handles player damage from explosions independently of block damage.
     * When explosions_damage_players is false, cancels all explosion damage to entities
     * regardless of whether the exploding mob's griefing is enabled or disabled.
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            boolean explosionPlayerDamage = config.getBoolean("explosions_damage_players", true);
            if (!explosionPlayerDamage) {
                if (config.getBoolean("verbose")) {
                    getLogger().info("Explosion damage to players is disabled.");
                }
                event.setCancelled(true);
            }
        }
    }

}
