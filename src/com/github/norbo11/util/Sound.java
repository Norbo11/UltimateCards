package com.github.norbo11.util;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.Sounds;

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
                    player.playSound(player.getLocation(), Sounds.NOTE_PIANO.bukkitSound(), 1.0F, pitch);
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
            player.playSound(player.getLocation(), Sounds.NOTE_BASS_DRUM.bukkitSound(), 1.0F, 1.0F);
            player.playSound(player.getLocation(), Sounds.NOTE_BASS_DRUM.bukkitSound(), 1.0F, 2.0F);
        }
    }

    // Plays turn sounds at a table apart from the player specified in the argument
    public static void tableTurnSounds(CardsTable table, String player) {
        for (CardsPlayer p : table.getPlayersThisHand()) {
            if (player.equals(p.getPlayerName())) {
                Sound.otherTurn(p.getPlayer());
            }
        }
    }

    public static void turn(Player player) {
        if (player != null) {
            player.playSound(player.getLocation(), Sounds.NOTE_PIANO.bukkitSound(), 1.0F, 1.0F);
            player.playSound(player.getLocation(), Sounds.NOTE_PIANO.bukkitSound(), 1.0F, 2.0F);
        }
    }

    public static void won(final Player player) {
        if (player != null) {
            soundTasks.put(player.getName(), Bukkit.getScheduler().runTaskTimerAsynchronously(p, new Runnable() {
                int i = 0;
                float pitch = 1.0F;

                @Override
                public void run() {
                    player.playSound(player.getLocation(), Sounds.NOTE_PIANO.bukkitSound(), 1.0F, pitch);
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
