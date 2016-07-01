package com.github.norbo11.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryHelper {
    public static boolean hasOpenSlotInInventory(Player player) {
        int stacks = 0;
        for (ItemStack itemStack : player.getInventory().getContents())
            if (itemStack != null) {
                stacks++;
            }
        return stacks < 36;
    }
}
