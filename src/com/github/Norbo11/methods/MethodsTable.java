package com.github.Norbo11.methods;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;
import com.github.Norbo11.classes.PokerPlayer;
import com.github.Norbo11.classes.Pot;
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
        player.sendMessage(p.pluginTag + p.gold + "dynamicFrequency [number] - " + p.white
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
            } else player.sendMessage(p.pluginTag + p.red + "Table ID " + p.gold + table.name + p.red + ", ID #" + p.gold + table.id + p.red + " is already closed!");
        } else p.methodsError.notOwnerOfTable(player);
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
            for (PokerPlayer pokerPlayer : table.players)
            {
                pokerPlayer.player.teleport(pokerPlayer.startLocation);
                pokerPlayer.sendMessage(p.pluginTag + "You have been paid " + p.gold + p.methodsMisc.formatMoney(pokerPlayer.money + pokerPlayer.totalBet)+ p.white + " because " + p.gold + table.owner.getName() + p.white + " closed the table.");
                p.economy.depositPlayer(pokerPlayer.player.getName(), pokerPlayer.money + pokerPlayer.totalBet);
            }
            p.tables.remove(table);
            player.sendMessage(p.pluginTag + "Table ID '" + p.gold + table.name + p.white + "', ID #" + p.gold + table.id + p.white + " has been deleted!");
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
            pokerPlayer.table.players.remove(pokerPlayer);
            player.sendMessage(p.pluginTag + "You have left table '" + p.gold + pokerPlayer.table.name + p.white + "', ID #" + p.gold + pokerPlayer.table.id + p.white + ", and received " + p.gold + p.methodsMisc.formatMoney(pokerPlayer.money) + p.white + ".");
        } else p.methodsError.notPokerPlayer(player);
    }

    public void listDetails(Player player, String type, String tableID)
    {
        if (type != null)
        //if (p.methodsCheck.isInteger(id))
        //{
            //Table table = p.methodsCheck.p.methodsCheck.isATable(Integer.parseInt(id));
            //if (table != null)
            //{
                player.sendMessage(p.pluginTag + p.gold + "Settings:");
                player.sendMessage(p.pluginTag + p.lineString);
                //table.listDetails(player, Integer.parseInt(id));
                player.sendMessage(p.pluginTag + p.lineString);
                player.sendMessage(p.pluginTag + p.gold + "Players:");
                //player.sendMessage(table.listPlayers());
                player.sendMessage(p.pluginTag + p.lineString);
                //player.sendMessage(p.pluginTag + "Owner: " + p.gold + table.owner.getName());
                //player.sendMessage(p.pluginTag + "Hand number: " + p.gold + table.handNumber);
                //player.sendMessage(p.pluginTag + "Open: " + p.gold + table.open);
                //player.sendMessage(p.pluginTag + "In progress: " + p.gold + table.inProgress);
               // player.sendMessage(p.pluginTag + "Location: " + p.gold + "X: " + p.white + Math.round(table.location.getX()) + p.gold + " Z: " + p.white + Math.round(table.location.getZ()) + p.gold + " Y: " + p.white + Math.round(table.location.getY()) + p.gold + " World: " + p.white + table.location.getWorld().getName());
            //} else p.methodsError.notATable(player, id);
        //} else p.methodsError.notANumber(player, id);
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
            } else player.sendMessage(p.pluginTag + p.red + "Table ID '" + p.gold + table.name + p.red + "', ID #" + p.gold + table.id + p.red + " is already open!");
        } else p.methodsError.notOwnerOfTable(player);
    }

    public void payPot(Player player, String potID, String playerID)
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (p.methodsCheck.isInteger(playerID))
            {
                if (table.inProgress == false)
                {
                    if (potID == null)
                    {
                        if (table.pots.size() == 1)
                        {
                            PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(table, Integer.parseInt(playerID));
                            if (pokerPlayer != null)
                            {
                                table.pots.get(0).payPot(pokerPlayer);
                            } else p.methodsError.notAPokerPlayerID(player, playerID);
                        } else p.methodsError.moreThanOnePot(player);
                    } else 
                    {
                        if (p.methodsCheck.isInteger(potID))
                        {
                            Pot pot = p.methodsCheck.isAPot(table, Integer.parseInt(potID));
                            if (pot != null)
                            {
                                    PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(table, Integer.parseInt(playerID));
                                    if (pokerPlayer != null)
                                    {
                                        pot.payPot(pokerPlayer);
                                        table.pots.remove(pot);
                                    } else p.methodsError.notAPokerPlayerID(player, playerID);
                            } else p.methodsError.notAPotID(player, potID);
                        } else p.methodsError.notANumber(player, potID);
                    }
                } else p.methodsError.tableIsInProgress(player);
            } else p.methodsError.notANumber(player, playerID);
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
                case "dynamicFrequency":
                    table.setNumberValue(player, "dynamicFrequency", value);
                    break;
                case "rake":
                    table.setNumberValue(player, "rake", value);
                    break;
                default:
                    player.sendMessage(p.pluginTag + p.red + "Invalid setting. Check available settings with /table listsettings");
            }
        } else p.methodsError.notOwnerOfTable(player);
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
                    if (table.banned.contains(player.getName()) == false)
                    {
                        if (table.open == true)
                        {
                            if (table.inProgress == false)
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
                                            player.sendMessage(p.pluginTag + "You have sat at table '" + p.gold + table.name + p.white + "', ID #" + p.gold + table.id + p.white + ", with a buy-in of " + p.gold + p.methodsMisc.formatMoney(Buyin) + p.white + ". Make sure to stay within " + p.gold + p.getConfig().getInt("table.chatrange") + p.white + " blocks of it to see all of it's messages!");
                                        } else p.methodsError.notEnoughMoney(player, buyin, p.economy.getBalance(player.getName()));
                                    } else p.methodsError.notWithinBuyinBounds(player, Buyin, table.minBuy, table.maxBuy);
                                } else p.methodsError.alreadyAtTable(player, table.name, table.id);
                            } else p.methodsError.tableIsInProgress(player);
                        } else p.methodsError.notOpen(player, id);
                    } else p.methodsError.playerBanned(player);
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
                if (table.stopped == false)
                {
                    if (table.players.size() >= 2)
                    {
                        player.sendMessage(p.pluginTag + "You have started the game at table '" + p.gold + table.name + p.white + "', ID #" + p.gold + table.id + p.white + ".");
                        table.deal();
                    } else p.methodsError.notEnoughPlayers(player);
                } else p.methodsError.tableIsStopped(player);
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
                player.sendMessage(p.pluginTag + "You have teleported to table " + p.gold + table.name + p.white + ", ID #" + p.gold + table.id + p.white + ". Sit down with " + p.gold + "/table sit [ID]");
            } else
            {
                p.methodsError.notATable(player, id);
            }
        } else
        {
            p.methodsError.notANumber(player, id);
        }
    }

    public void kick(Player player, String toKick)
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (p.methodsCheck.isInteger(toKick))
            {
                PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(table, Integer.parseInt(toKick));
                if (pokerPlayer != null)
                {
                    p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + table.owner.getName() + p.white + " has kicked " + p.gold + pokerPlayer.player.getName() + p.white + " from the table!");
                    table.players.remove(table.players.indexOf(pokerPlayer));
                    if (pokerPlayer.table.getOnlinePlayers().size() == pokerPlayer.table.players.size() && pokerPlayer.table.stopped == true)
                    {
                        p.methodsMisc.sendToAllWithinRange(pokerPlayer.table.location, p.pluginTag + "All players online again, resuming the table!");
                        pokerPlayer.table.resumeTable();
                    }
                } else p.methodsError.notAPokerPlayerID(player, toKick);
            } else p.methodsError.notANumber(player, toKick);
        } else p.methodsError.notOwnerOfTable(player);
    }
    
    public void ban(Player player, String toBan)
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + table.owner.getName() + p.white + " has banned " + p.gold + toBan + p.white + " from the table!");
            table.banned.add(toBan);
        } else p.methodsError.notOwnerOfTable(player);
    }

    public void unBan(Player player, String toUnBan)
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + table.owner.getName() + p.white + " has unbanned " + p.gold + toUnBan + p.white + " from the table!");
            table.banned.remove(toUnBan);
        } else p.methodsError.notOwnerOfTable(player);
    }

    public void displayPlayers(Player player)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        player.sendMessage(p.pluginTag + "List of players (use " + p.gold + "/table pay [ID]" + p.white + ") to pay pots:");
        if (pokerPlayer != null)
        {
            player.sendMessage(pokerPlayer.table.listPlayers());
        } else p.methodsError.notPokerPlayer(player);
    }
}
