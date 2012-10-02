package com.github.norbo11.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.util.MapMethods;

//This class prevents map duping by disallowing inventory clicks, item drops, and item pickups if the map isnt picked up by the player it was supposed to go to.
public class MapListener implements Listener
{
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        try
        {
            if (e.getCurrentItem().getType() == Material.MAP)
            {
                ItemStack itemStack = null;
                itemStack = new ItemStack(Material.MAP, 1, MapMethods.getSavedMaps().get(e.getWhoClicked().getName()));
                if (itemStack != null && e.getCurrentItem().equals(itemStack))
                {
                    e.setCancelled(true);
                    e.getInventory().remove(itemStack);
                }
            }
        } catch (Exception exc)
        {
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e)
    {
        try
        {
            if (e.getItemDrop().getItemStack().getType() == Material.MAP)
            {
                ItemStack itemStack = null;
                itemStack = new ItemStack(Material.MAP, 1, MapMethods.getSavedMaps().get(e.getPlayer().getName()));
                if (itemStack != null && e.getItemDrop().getItemStack().equals(itemStack))
                {
                    e.setCancelled(true);
                }
            }
        } catch (Exception exc)
        {
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e)
    {
        try
        {
            if (e.getItem().getItemStack().getType() == Material.MAP) // If the item dropped is map that was created by the plugin
            if (MapMethods.getCreatedMaps().contains(e.getItem().getItemStack().getDurability()))
            {
                // If the player trying to pick up the map is NOT the
                // rightful owner, and the map DOES have an owner
                String mapOwner = MapMethods.mapExists(e.getItem().getItemStack());
                if (mapOwner != e.getPlayer().getName() && mapOwner != "")
                {
                    e.setCancelled(true);
                } else if (CardsPlayer.getCardsPlayer(e.getPlayer().getName()) == null)
                {
                    e.setCancelled(true);
                    e.getItem().remove();
                    MapMethods.getCreatedMaps().remove(e.getItem().getItemStack().getDurability());
                } // This results in maps that are no longer in use being
                  // deleted. Maps being picked up, but still in use, are
                  // not deleted, but simply cancelled. (both of this is in
                  // the case if the player is not the owner of the map)
            }
        } catch (Exception exc)
        {
        }
    }
}
