package com.github.norbo11.util;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.norbo11.UltimateCards;

public class Sound
{
    public static UltimateCards p;
    public static HashMap<String, Integer> soundTasks = new HashMap<String, Integer>();

    public static void lost(final Player player)
    {
        soundTasks.put(player.getName(), Bukkit.getScheduler().scheduleAsyncRepeatingTask(p, new Runnable()
        {
            int i = 0;
            float pitch = 2.0F;

            public void run()
            {
                player.playSound(player.getLocation(), org.bukkit.Sound.NOTE_PIANO, 1.0F, pitch);
                pitch -= 0.1F;
                i++;
                if (i == 7)
                {
                    Bukkit.getScheduler().cancelTask(soundTasks.get(player.getName()));
                }
            }
        }, 0L, 1L));
    }

    public static void otherTurn(final Player player)
    {
        player.playSound(player.getLocation(), org.bukkit.Sound.NOTE_BASS_DRUM, 1.0F, 1.0F);
        player.playSound(player.getLocation(), org.bukkit.Sound.NOTE_BASS_DRUM, 1.0F, 2.0F);
    }

    public static void turn(Player player)
    {
        player.playSound(player.getLocation(), org.bukkit.Sound.NOTE_PIANO, 1.0F, 1.0F);
        player.playSound(player.getLocation(), org.bukkit.Sound.NOTE_PIANO, 1.0F, 2.0F);
    }

    public static void won(final Player player)
    {
        soundTasks.put(player.getName(), Bukkit.getScheduler().scheduleAsyncRepeatingTask(p, new Runnable()
        {
            int i = 0;
            float pitch = 1.0F;

            public void run()
            {
                player.playSound(player.getLocation(), org.bukkit.Sound.NOTE_PIANO, 1.0F, pitch);
                pitch += 0.1F;
                i++;
                if (i == 7)
                {
                    Bukkit.getScheduler().cancelTask(soundTasks.get(player.getName()));
                }
            }
        }, 0L, 1L));
    }
}
