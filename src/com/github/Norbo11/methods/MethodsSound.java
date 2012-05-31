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

package com.github.norbo11.methods;

import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import com.github.norbo11.UltimatePoker;

public class MethodsSound
{

    public MethodsSound(UltimatePoker p)
    {
        this.p = p;
    }

    UltimatePoker p;

    public void playerTurn(final Player player)
    {
        // First we save the location of the player and then the block he is currently standing on (Hence the Y-1).
        final Location location = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() - 1, player.getLocation().getZ());
        final BlockState oldstate = location.getBlock().getState();

        // We set the block under him to a note block (required for the sound to be played)
        location.getBlock().setType(Material.NOTE_BLOCK);

        // We schedule a task which runs after 1 tick. This is for some reason necessary for the sound to play, otherwise the client tries to play the sound too quickly and nothing happends.
        Bukkit.getScheduler().scheduleSyncDelayedTask(p, new Runnable()
        {
            public void run()
            {
                player.playNote(location, Instrument.PIANO, Note.flat(1, Tone.D));
                player.playNote(location, Instrument.PIANO, Note.flat(1, Tone.F));

                // After we play our sound we revert the block to what it was before.
                oldstate.update(true);
            }
        }, 1);
    }
}
