package com.devilswrath.plugin;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class DevilsWrathParticleTask extends BukkitRunnable {

    private final DevilsWrathPlugin plugin;
    private int tick = 0;

    // Offset positions along the sword blade (relative to player hand)
    private static final double[][] BLADE_OFFSETS = {
            {0.0,  0.0,  0.0},
            {0.0,  0.3,  0.0},
            {0.0,  0.6,  0.0},
            {0.0,  0.9,  0.0},
            {0.0,  1.1,  0.0},
    };

    public DevilsWrathParticleTask(DevilsWrathPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        tick++;

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            ItemStack held = player.getInventory().getItemInMainHand();

            if (!DevilsWrathSword.isDevilsWrath(held)) continue;

            boolean abilityOn = DevilsWrathListener.abilityActive.getOrDefault(uuid, false);
            spawnSwordParticles(player, abilityOn);
        }
    }

    private void spawnSwordParticles(Player player, boolean abilityActive) {
        World world = player.getWorld();

        // Base location: player's right hand (approximate)
        Location base = player.getLocation().clone().add(0, 1.3, 0);

        // Direction the player is facing
        double yaw = Math.toRadians(player.getLocation().getYaw());
        double pitch = Math.toRadians(player.getLocation().getPitch());

        // Forward vector
        double fx = -Math.sin(yaw) * Math.cos(pitch);
        double fy = -Math.sin(pitch);
        double fz = Math.cos(yaw) * Math.cos(pitch);

        // Right vector (perpendicular to forward, horizontal)
        double rx = Math.cos(yaw);
        double rz = Math.sin(yaw);

        // Offset blade slightly to the right of center
        base.add(rx * 0.4, 0, rz * 0.4);

        if (abilityActive) {
            // ── ACTIVE: aggressive, large, dense black particles ──
            for (double[] offset : BLADE_OFFSETS) {
                double t = offset[1]; // along blade
                Location pos = base.clone().add(
                        fx * t + rx * offset[0],
                        fy * t + offset[1] * 0.05,
                        fz * t + rz * offset[0]
                );

                // Dense SQUID_INK (pure black)
                world.spawnParticle(Particle.SQUID_INK, pos, 4, 0.12, 0.12, 0.12, 0.04);
                // FIXED: SMOKE_LARGE -> LARGE_SMOKE (renamed in 1.20.2+)
                world.spawnParticle(Particle.LARGE_SMOKE, pos, 2, 0.15, 0.15, 0.15, 0.03);

                // Every other tick, spawn spiraling chaos particles around blade
                if (tick % 2 == 0) {
                    double angle = (tick * 0.5) + (t * Math.PI);
                    double spiralR = 0.18;
                    double sx = Math.cos(angle) * spiralR;
                    double sz = Math.sin(angle) * spiralR;
                    world.spawnParticle(Particle.SQUID_INK,
                            pos.clone().add(sx, 0, sz), 2, 0.05, 0.05, 0.05, 0.02);
                }
            }

            // Aura cloud around the player
            spawnAmbientAura(player, world, true);

        } else {
            // ── PASSIVE: calm, small, wispy black particles ──
            for (double[] offset : BLADE_OFFSETS) {
                double t = offset[1];
                Location pos = base.clone().add(
                        fx * t + rx * offset[0],
                        fy * t + offset[1] * 0.05,
                        fz * t + rz * offset[0]
                );

                if (tick % 3 == 0) {
                    world.spawnParticle(Particle.SQUID_INK, pos, 1, 0.06, 0.06, 0.06, 0.01);
                    // FIXED: SMOKE_NORMAL -> SMOKE (renamed in 1.20.2+)
                    world.spawnParticle(Particle.SMOKE, pos, 1, 0.08, 0.08, 0.08, 0.01);
                }
            }

            // Subtle passive aura
            if (tick % 5 == 0) {
                spawnAmbientAura(player, world, false);
            }
        }
    }

    private void spawnAmbientAura(Player player, World world, boolean aggressive) {
        Location center = player.getLocation().add(0, 0.1, 0);

        if (aggressive) {
            // Dense spinning ring at ground level
            int points = 16;
            double radius = 1.5 + (Math.sin(tick * 0.3) * 0.3); // pulsing radius
            for (int i = 0; i < points; i++) {
                double angle = (2 * Math.PI / points) * i + (tick * 0.15);
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                world.spawnParticle(Particle.SQUID_INK,
                        center.clone().add(x, 0.05, z), 1, 0.05, 0.05, 0.05, 0.01);
            }
            // Rising dark tendrils
            for (int i = 0; i < 4; i++) {
                double angle = (tick * 0.2) + (i * Math.PI / 2.0);
                double x = Math.cos(angle) * 0.7;
                double z = Math.sin(angle) * 0.7;
                double y = ((tick * 0.05 + i * 0.25) % 1.5);
                // FIXED: SMOKE_LARGE -> LARGE_SMOKE
                world.spawnParticle(Particle.LARGE_SMOKE,
                        center.clone().add(x, y, z), 1, 0.04, 0.04, 0.04, 0.01);
            }
        } else {
            // Calm, slow drift
            int points = 8;
            double radius = 1.2;
            for (int i = 0; i < points; i++) {
                double angle = (2 * Math.PI / points) * i + (tick * 0.05);
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                if (Math.random() < 0.4) {
                    // FIXED: SMOKE_NORMAL -> SMOKE
                    world.spawnParticle(Particle.SMOKE,
                            center.clone().add(x, 0.05, z), 1, 0.03, 0.03, 0.03, 0.005);
                }
            }
        }
    }
}
