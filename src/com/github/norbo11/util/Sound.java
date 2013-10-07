package com.github.norbo11.util;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.github.norbo11.UltimateCards;

public class Sound {
    public static UltimateCards p;
    public static HashMap<String, BukkitTask> soundTasks = new HashMap<String, BukkitTask>();

    public static void lost(final Player player) {
        if (player != null) {
            soundTasks.put(player.getName(), Bukkit.getScheduler().runTaskTimerAsynchronously(p, new Runnable() {
                int i = 0;
                float pitch = 2.0F;

                @Override
                public void run() {
                    player.playSound(player.getLocation(), org.bukkit.Sound.NOTE_PIANO, 1.0F, pitch);
                    pitch -= 0.1F;
                    i++;
                    if (i == 7) {
                        soundTasks.get(player.getName()).cancel();
                    }
                }
            }, 0L, 1L));
        }
    }

    public static void otherTurn(final Player player) {
        if (player != null) {
            player.playSound(player.getLocation(), org.bukkit.Sound.NOTE_BASS_DRUM, 1.0F, 1.0F);
            player.playSound(player.getLocation(), org.bukkit.Sound.NOTE_BASS_DRUM, 1.0F, 2.0F);
        }
    }

    public static void turn(Player player) {
        if (player != null) {
            player.playSound(player.getLocation(), org.bukkit.Sound.NOTE_PIANO, 1.0F, 1.0F);
            player.playSound(player.getLocation(), org.bukkit.Sound.NOTE_PIANO, 1.0F, 2.0F);
        }
    }

    public static void won(final Player player) {
        if (player != null) {
            soundTasks.put(player.getName(), Bukkit.getScheduler().runTaskTimerAsynchronously(p, new Runnable() {
                int i = 0;
                float pitch = 1.0F;

                @Override
                public void run() {
                    player.playSound(player.getLocation(), org.bukkit.Sound.NOTE_PIANO, 1.0F, pitch);
                    pitch += 0.1F;
                    i++;
                    if (i == 7) {
                        soundTasks.get(player.getName()).cancel();
                    }
                }
            }, 0L, 1L));
        }
    }
}
