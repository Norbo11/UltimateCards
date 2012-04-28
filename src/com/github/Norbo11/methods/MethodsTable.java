package com.github.Norbo11.methods;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;
import com.github.Norbo11.classes.PokerPlayer;
import com.github.Norbo11.classes.Table;

public class MethodsTable
{

    UltimatePoker p;

    public MethodsTable(UltimatePoker p)
    {
        this.p = p;
    }

    public void availableSettings(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "Usage: /table set [setting] [value]");
        player.sendMessage(p.pluginTag + p.white + "Available settings:");
        player.sendMessage(p.pluginTag + p.gold + "elimination [true|false] - " + p.white + "If true, players can't re-buy.");
        player.sendMessage(p.pluginTag + p.gold + "minBuy [number] - " + p.white + "The mininmum number that players can buy-in (and re-buy) for.");
        player.sendMessage(p.pluginTag + p.gold + "maxBuy [number] - " + p.white + "The maximum number that players can buy-in (and re-buy) for.");
        player.sendMessage(p.pluginTag + p.gold + "sb [number] - " + p.white + "Set the small blind.");
        player.sendMessage(p.pluginTag + p.gold + "bb [number] - " + p.white + "Set the big blind");
        player.sendMessage(p.pluginTag + p.gold + "ante [number] - " + p.white + "Sets the ante.");
        player.sendMessage(p.pluginTag + p.gold + "dynamicAnteFrequency [number] - " + p.white
        + "This decides that every [number] hands, the ante + blinds will increase by themselves. 0 = disabled.");
        player.sendMessage(p.pluginTag + p.gold + "rake [number] - " + p.white + "How much of the pot you will get every hand, in percentages. Example: 0.05 = 5% rake.");
    }

    public void closeTable(Player player)
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (table.open == true)
            {
                table.open = false;
                player.sendMessage(p.pluginTag + "Table ID '" + p.gold + table.name + p.white + "', ID #" + p.gold + table.id + p.white + " is now closed! Players now can't join!");
            } else
            {
                player.sendMessage(p.pluginTag + "Table ID '" + p.gold + table.name + p.white + "', ID #" + p.gold + table.id + p.white + " is already closed!");
            }
        } else
        {
            p.methodsError.notOwnerOfTable(player);
        }
    }

    public void createTable(Player player, String tableName)
    {
        if (p.methodsCheck.p.methodsCheck.isOwnerOfTable(player) == null)
        {
            PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
            if (pokerPlayer == null)
            {
                Table newTable = new Table(player, tableName, p.tables.size(), player.getLocation(), p);
                p.tables.add(newTable);
                player.sendMessage(p.pluginTag + "Created table named " + p.gold + "'" + tableName + "'" + p.white + ", ID " + p.gold + Integer.toString(p.tables.size() - 1)
                + p.white + "!");
                player.sendMessage(p.pluginTag + "Edit the rules of your table with " + p.gold + "'/table set'" + p.white + ", and open it with " + p.gold + "'/table open'"
                + p.white + " when ready!");
            } else
            {
                player.sendMessage(p.pluginTag + "You are currently playing at a table called '" + p.gold + pokerPlayer.table + p.white + "', and can't make another one!");
            }
        } else
        {
            p.methodsError.alreadyOwnTable(player);
        }
    }

    public void deleteTable(Player player)
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (table.inProgress == false)
            {
                if (table.pot == 0)
                {
                    for (PokerPlayer pokerPlayer : table.players)
                    {
                        pokerPlayer.player.teleport(pokerPlayer.startLocation);
                        pokerPlayer.sendMessage(p.pluginTag + "You have been paid " + p.gold + p.methodsMisc.formatMoney(pokerPlayer.money)+ p.white + " because " + p.gold + table.owner.getName() + p.white + " closed the table.");
                        p.economy.depositPlayer(pokerPlayer.player.getName(), pokerPlayer.money);
                    }
                    p.tables.remove(table);
                    player.sendMessage(p.pluginTag + "Table ID '" + p.gold + table.name + p.white + "', ID #" + p.gold + table.id + p.white + " has been deleted!");
                } else p.methodsError.potExists(player, table.pot);
            } else p.methodsError.tableIsInProgress(player);
        } else p.methodsError.notOwnerOfTable(player);
    }

    public void displayBoard(Player player, String id)
    {
        if (p.methodsCheck.isInteger(id))
        {
            Table table = p.methodsCheck.isATable(Integer.parseInt(id));
            if (table != null)
            {
                table.displayBoard(player);
            } else
            {
                p.methodsError.notATable(player, id);
            }
        } else
        {
            p.methodsError.notANumber(player, id);
        }
    }

    public void displayTables(Player player)
    {
        player.sendMessage(p.pluginTag + "List of currently created poker tables:");
        if (p.tables.size() > 0)
        {
            for (int i = 0; i < p.tables.size(); i++)
            {
                if (p.tables.get(i).open == true)
                {
                    player.sendMessage(p.pluginTag + ChatColor.GREEN + "#" + p.tables.get(i).id + " " + p.tables.get(i).name);
                } else
                {
                    player.sendMessage(p.pluginTag + p.red + "#" + p.tables.get(i).id + " " + p.tables.get(i).name);
                }
            }
            player.sendMessage(p.pluginTag + ChatColor.GREEN + "GREEN = Open. " + p.red + "RED = Closed.");
            player.sendMessage(p.pluginTag + "Use " + p.gold + "/table sit [table ID] [buy-in] " + p.white + "to join a table.");
        } else
        {
            player.sendMessage(p.pluginTag + p.red + "No tables available.");
        }
    }

    public void leaveTable(Player player)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            player.teleport(pokerPlayer.startLocation);
            p.economy.depositPlayer(player.getName(), pokerPlayer.money);
            pokerPlayer.table.players.set(pokerPlayer.table.players.indexOf(pokerPlayer), null);
            player.sendMessage(p.pluginTag + "You have left table '" + p.gold + pokerPlayer.table.name + p.white + "', ID #" + p.gold + pokerPlayer.table.id + p.white
            + ", and received " + p.gold + p.methodsMisc.formatMoney(pokerPlayer.money) + p.white + ".");
        } else
        {
            p.methodsError.notPokerPlayer(player);
        }
    }

    public void listDetails(Player player, String id)
    {
        if (p.methodsCheck.isInteger(id))
        {
            Table table = p.methodsCheck.p.methodsCheck.isATable(Integer.parseInt(id));
            if (table != null)
            {
                player.sendMessage(p.pluginTag + p.gold + "Settings:");
                player.sendMessage(p.pluginTag + p.lineString);
                listSettings(player, Integer.parseInt(id));
                player.sendMessage(p.pluginTag + p.lineString);
                player.sendMessage(p.pluginTag + p.gold + "Players:");
                for (PokerPlayer i : table.players)
                {
                    player.sendMessage(p.pluginTag + "[" + i.id + "] " + p.gold + i.player.getName());
                }
                player.sendMessage(p.pluginTag + p.lineString);
                player.sendMessage(p.pluginTag + "Owner: " + p.gold + table.owner.getName());
                player.sendMessage(p.pluginTag + "Hand number: " + p.gold + table.handNumber);
                player.sendMessage(p.pluginTag + "Open: " + p.gold + table.open);
                player.sendMessage(p.pluginTag + "In progress: " + p.gold + table.inProgress);
                player.sendMessage(p.pluginTag + "Location: " + p.gold + "X: " + p.white + Math.round(table.location.getX()) + p.gold + " Z: " + p.white
                + Math.round(table.location.getZ()) + p.gold + " Y: " + p.white + Math.round(table.location.getY()) + p.gold + " World: " + p.white
                + table.location.getWorld().getName());
            } else
            {
                p.methodsError.notATable(player, id);
            }
        } else
        {
            p.methodsError.notANumber(player, id);
        }
    }

    public void listSettings(Player player, int id)
    {
        Table table = p.methodsCheck.isATable(id);
        if (table != null)
        {
            player.sendMessage(p.pluginTag + "Elimination mode: " + p.gold + table.elimination);
            player.sendMessage(p.pluginTag + "Minimum buy-in: " + p.gold + p.methodsMisc.formatMoney(table.minBuy));
            player.sendMessage(p.pluginTag + "Maximum buy-in: " + p.gold + p.methodsMisc.formatMoney(table.maxBuy));
            player.sendMessage(p.pluginTag + "Small blind: " + p.gold + p.methodsMisc.formatMoney(table.sb));
            player.sendMessage(p.pluginTag + "Big blind: " + p.gold + p.methodsMisc.formatMoney(table.bb));
            player.sendMessage(p.pluginTag + "Ante: " + p.gold + p.methodsMisc.formatMoney(table.ante));
            if (table.dynamicAnteFreq > 0)
            {
                player.sendMessage(p.pluginTag + "Dynamic ante frequency: " + p.gold + "every " + table.dynamicAnteFreq + " hands.");
            } else
            {
                player.sendMessage(p.pluginTag + "Dynamic ante is turned " + p.gold + "OFF");
            }
            player.sendMessage(p.pluginTag + "Rake percentage: " + p.gold + p.methodsMisc.convertToPercentage(table.rake));
        } else
        {
            p.methodsError.notOwnerOfTable(player);
        }
    }

    public void openTable(Player player)
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (table.open == false)
            {
                table.open = true;
                player.sendMessage(p.pluginTag + "Table ID '" + p.gold + table.name + p.white + "', ID #" + p.gold + table.id + p.white + " is now open! Players can now join!");
            } else
            {
                player.sendMessage(p.pluginTag + p.red + "Table ID '" + p.gold + table.name + p.red + "', ID #" + p.gold + table.id + p.red + " is already open!");
            }
        } else
        {
            p.methodsError.notOwnerOfTable(player);
        }
    }

    public void payPot(Player player, String id)
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (table.pot > 0)
            {
                if (p.methodsCheck.isInteger(id))
                {
                    PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(table, Integer.parseInt(id));
                    if (pokerPlayer != null)
                    {
                        pokerPlayer.payPot();
                    } else p.methodsError.notAPokerPlayerID(player, id);
                } else p.methodsError.notANumber(player, id);
            } else p.methodsError.potDoesntExist(player);
        } else p.methodsError.notOwnerOfTable(player);
    }

    public void setSetting(Player player, String setting, String value)
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            switch (setting)
            {
                case "elimination":
                    table.setElimination(player, value);
                    break;
                case "minBuy":
                    table.setNumberValue(player, "minBuy", value);
                    break;
                case "maxBuy":
                    table.setNumberValue(player, "maxBuy", value);
                    break;
                case "sb":
                    table.setNumberValue(player, "sb", value);
                    break;
                case "bb":
                    table.setNumberValue(player, "bb", value);
                    break;
                case "ante":
                    table.setNumberValue(player, "ante", value);
                    break;
                case "dynamicAnteFrequency":
                    table.setNumberValue(player, "dynamicAnteFrequency", value);
                    break;
                default:
                    player.sendMessage(p.pluginTag + p.red + "Invalid setting. Check available settings with /table listsettings");
            }
        }
    }

    public void sitTable(Player player, String id, String buyin)
    {
        if (p.methodsCheck.isInteger(id))
        {
            if (p.methodsCheck.isDouble(buyin))
            {
                Table table = p.methodsCheck.isATable(Integer.parseInt(id));
                if (table != null)
                {
                    if (table.open == true)
                    {
                        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
                        if (pokerPlayer == null)
                        {
                            double Buyin = Double.parseDouble(buyin);
                            if (Buyin >= table.minBuy && Buyin <= table.maxBuy)
                            {
                                if (p.economy.has(player.getName(), Buyin))
                                {
                                    table.players.add(new PokerPlayer(player, table, Buyin, p));
                                    player.teleport(table.location);
                                    p.economy.withdrawPlayer(player.getName(), Buyin);
                                    player.sendMessage(p.pluginTag + "You have sat at table '" + p.gold + table.name + p.white + "', ID #" + p.gold + table.id + p.white
                                    + ", with a buy-in of " + p.gold + p.methodsMisc.formatMoney(Buyin) + p.white + ". Make sure to within " + p.gold + p.getConfig().getInt("table.chatrange") + p.white + " blocks of it to see all of it's messages!");
                                } else p.methodsError.notEnoughMoney(player, buyin, p.economy.getBalance(player.getName()));
                            } else p.methodsError.notWithinBuyinBounds(player, Buyin, table.minBuy, table.maxBuy);
                        } else p.methodsError.alreadyAtTable(player, table.name, table.id);
                    } else p.methodsError.notOpen(player, id);
                } else p.methodsError.notATable(player, id);
            } else p.methodsError.notANumber(player, buyin);
        } else p.methodsError.notANumber(player, id);
    }

    public void startTable(Player player)
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (table.inProgress != true)
            {
                if (table.players.size() > 0)
                {
                    player.sendMessage(p.pluginTag + "You have started the game at table '" + p.gold + table.name + p.white + "', ID #" + p.gold + table.id + p.white + ".");
                    table.setInProgress(true);
                } else p.methodsError.noPlayersAtTable(player, table.name, table.id);
            } else p.methodsError.tableIsInProgress(player);
        } else p.methodsError.notOwnerOfTable(player);
    }

    public void tpToTable(Player player, String id)
    {
        if (p.methodsCheck.isInteger(id))
        {
            Table table = p.methodsCheck.isATable(Integer.parseInt(id));
            if (table != null)
            {
                player.teleport(table.location);
                player.sendMessage(p.pluginTag + "You have teleported to table '" + p.gold + table.name + p.white + "', ID #" + p.gold + table.id + p.white
                + ". Sit down with /table sit [ID]");
            } else
            {
                p.methodsError.notATable(player, id);
            }
        } else
        {
            p.methodsError.notANumber(player, id);
        }
    }
}
