package com.github.Norbo11.methods;

import java.text.NumberFormat;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;

public class MethodsMisc
{
    UltimatePoker p;
    NumberFormat nf = NumberFormat.getCurrencyInstance();

    public MethodsMisc(UltimatePoker p)
    {
        this.p = p;
    }

    public String convertToPercentage(double value)
    {
        return Double.toString(value * 100) + '%';
    }

    public String formatMoney(double amount)
    {
        return p.economy.format(amount);
    }

    public void sendToAllWithinRange(Location location, String message)
    {
        Player[] players = p.getServer().getOnlinePlayers();
        for (Player player : players)
        {
            if (player.getLocation().distance(location) <= p.getConfig().getInt("table.chatrange"))
            {
                player.sendMessage(message);
            }
        }
    }

    public void sendToAllWithinRange(Location location, String[] message)
    {
        Player[] players = p.getServer().getOnlinePlayers();
        for (Player player : players)
        {
            if (player.getLocation().distance(location) <= p.getConfig().getInt("table.chatrange"))
            {
                player.sendMessage(message);
            }
        }
    }
}
