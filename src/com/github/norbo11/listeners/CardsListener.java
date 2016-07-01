package com.github.norbo11.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.util.MapMethods;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.config.PluginConfig;

//This class prevents map duping by disallowing inventory clicks, item drops, and item pickups if the map isnt picked up by the player it was supposed to go to.
public class CardsListener implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        CardsPlayer cardsPlayer = CardsPlayer.getCardsPlayer(player.getName());
        
        if (cardsPlayer != null) {
            if (cardsPlayer.getTable().getSettings().autoKickOnLeave.getValue())
                cardsPlayer.getTable().kick(cardsPlayer);
        }
    }
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        CardsPlayer cardsPlayer = CardsPlayer.getCardsPlayer(player.getName());
        String command = e.getMessage().split(" ")[0];
        String[] allowables = new String[] {
            "/table",
            "/cards",
            "/poker",
            "/bj",
            "/blackjack"
        };
        
        if (cardsPlayer != null) {
            
            boolean allowed = false;
            
            for (String allowable : allowables) {
                if (allowable.equalsIgnoreCase(command)) allowed = true;
            }
            
            if (!player.isOp() && PluginConfig.isDisableCommandsWhilePlaying() && !allowed) {
                e.setCancelled(true);
                Messages.sendMessage(player, "You may not use any commands while playing cards!");
            }                
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (PluginConfig.isPreventMovementOutsideChatRange()) {
            Player player = e.getPlayer();
            CardsPlayer cardsPlayer = CardsPlayer.getCardsPlayer(player.getName());
            
            if (cardsPlayer != null) {
                int chatRange = PluginConfig.getPublicChatRange();
                if (e.getTo().distance(cardsPlayer.getTable().getSettings().startLocation.getValue()) >= chatRange) {
                    e.setCancelled(true);
                    Messages.sendMessage(player, "You may not move further than &6" + chatRange + " &fblocks from the table!");
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        try {
            if (e.getCurrentItem().getType() == Material.MAP) {
                ItemStack itemStack = MapMethods.getSavedMaps().get(e.getWhoClicked().getName());
                if (itemStack != null && e.getCurrentItem().equals(itemStack)) {
                    Messages.sendMessage((Player) e.getWhoClicked(), "You may not move your cards interface map!");
                    e.setCancelled(true);
                    e.getInventory().remove(itemStack);
                }
            }
        } catch (Exception exc) {
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        try {
            if (e.getItemDrop().getItemStack().getType() == Material.MAP) {
                ItemStack itemStack = MapMethods.getSavedMaps().get(e.getPlayer().getName());
                if (itemStack != null && e.getItemDrop().getItemStack().equals(itemStack)) {
                    Messages.sendMessage((Player) e.getPlayer(), "You may not drop your cards interface map!");
                    e.setCancelled(true);
                }
            }
        } catch (Exception exc) {
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        try {
            if (e.getItem().getItemStack().getType() == Material.MAP) // If the item dropped is map that was created by the plugin
            {
                if (MapMethods.getCreatedMaps().contains(e.getItem().getItemStack().getDurability())) {
                    // If the player trying to pick up the map is NOT the rightful owner, and the map DOES have an owner
                    String mapOwner = MapMethods.mapExists(e.getItem().getItemStack());
                    if (mapOwner != e.getPlayer().getName() && mapOwner != "") {
                        e.setCancelled(true);
                    } else if (CardsPlayer.getCardsPlayer(e.getPlayer().getName()) == null) {
                        e.setCancelled(true);
                        e.getItem().remove();
                        MapMethods.getCreatedMaps().remove(e.getItem().getItemStack().getDurability());
                    } // This results in maps that are no longer in use being deleted. Maps being picked up, but still in use, are
                      // not deleted, but simply cancelled. (both of this is in the case if the player is not the owner of the map)
                }
            }
        } catch (Exception exc) {
        }
    }
}
