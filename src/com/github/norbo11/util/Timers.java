package com.github.norbo11.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class Timers {
    public static Plugin p;

    public static BukkitTask startTimerAsync(final Runnable method, final int seconds) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(p, method, seconds * 20);
    }

    public static BukkitTask startTimerSync(final Runnable method, final int seconds) {
        return Bukkit.getScheduler().runTaskLater(p, method, seconds * 20);
    }
}
