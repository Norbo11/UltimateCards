/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: MethodsSound.java
 * -Simple class which provides different sounds and plays it to the specified player.
 * -Currently only holds the sound for the player's turn. If you wish to add more sounds,
 * make another method with the name of what the sound is.
 * ===================================================================================================
 */

package com.github.norbo11.util;

import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.norbo11.UltimateCards;

public class Sound
{
    public static UltimateCards p;

    public static void playerTurn(final Player player)
    {
        final Location location = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() - 1, player.getLocation().getZ());
        final Block oldBlock = location.getBlock();

        player.sendBlockChange(location, Material.NOTE_BLOCK, (byte)0);
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(p, new Runnable() 
        {
            public void run()
            {
                player.playNote(new Location(player.getWorld(), 55, 63, 55), Instrument.PIANO, Note.sharp(1, Tone.A));

                player.sendBlockChange(location, oldBlock.getType(), oldBlock.getData());
            }
        }, 1L);
    }
}
