package com.devilswrath.plugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class DevilsWrathSword {

    public static final String SWORD_NAME = ChatColor.DARK_RED + "" + ChatColor.BOLD + "Devils Wrath";

    /**
     * Creates the Devils Wrath sword with all passive enchantments.
     * Enchantment keys updated for Paper 1.21.x.
     */
    public static ItemStack create() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = sword.getItemMeta();

        meta.setDisplayName(SWORD_NAME);
        meta.setUnbreakable(true);
        meta.setLore(Arrays.asList(
                ChatColor.DARK_GRAY + "A blade forged in the depths of the underworld.",
                ChatColor.RED + "Ability: " + ChatColor.GRAY + "Sneak to awaken demonic power.",
                ChatColor.DARK_RED + "Cooldown: " + ChatColor.GRAY + "48 seconds after use.",
                ChatColor.DARK_PURPLE + "Passive: " + ChatColor.GRAY + "Demonic particles follow the blade."
        ));

        sword.setItemMeta(meta);

        // FIXED: Use correct enchantment keys for 1.21.x
        // DAMAGE_ALL  -> SHARPNESS
        // SWEEPING_EDGE -> SWEEPING_EDGE (same name but accessed via NamespacedKey in 1.21)
        // LOOT_BONUS_MOBS -> LOOTING
        sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 5);
        sword.addUnsafeEnchantment(Enchantment.MENDING, 1);
        sword.addUnsafeEnchantment(Enchantment.SWEEPING_EDGE, 3);
        sword.addUnsafeEnchantment(Enchantment.LOOTING, 4);

        return sword;
    }

    /**
     * Returns true if the given ItemStack is the Devils Wrath sword.
     */
    public static boolean isDevilsWrath(ItemStack item) {
        if (item == null) return false;
        if (item.getType() != Material.DIAMOND_SWORD) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return false;
        return meta.getDisplayName().equals(SWORD_NAME);
    }
}
