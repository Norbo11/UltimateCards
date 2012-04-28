package com.github.Norbo11;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Norbo11.classes.Table;

public class ListenerCommandExecutor implements CommandExecutor
{

    UltimatePoker p;

    ListenerCommandExecutor(UltimatePoker p)
    {
        this.p = p;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("tables"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                p.methodsTable.displayTables(player);
                return true;
            } else
            {
                p.methodsError.notPlayer(sender);
            }
        }
        if (command.getName().equalsIgnoreCase("hand"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                if (args.length > 0)
                {
                    String action = args[0];
                    if (action.equalsIgnoreCase("help"))
                    {
                        if (args.length == 1)
                        {
                            p.methodsError.displayHelp(player, "hand");
                        } else
                        {
                            p.methodsError.usage(player, "handhelp");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("bet"))
                    {
                        if (args.length == 2)
                        {
                            p.methodsHand.bet(player, args[1]);
                        } else
                        {
                            p.methodsError.usage(player, "bet");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("fold"))
                    {
                        if (args.length == 1)
                        {
                            p.methodsHand.fold(player);
                        } else
                        {
                            p.methodsError.usage(player, "fold");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("call"))
                    {
                        if (args.length == 1)
                        {
                            p.methodsHand.call(player);
                        } else
                        {
                            p.methodsError.usage(player, "call");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("check"))
                    {
                        if (args.length == 1)
                        {
                            p.methodsHand.check(player);
                        } else
                        {
                            p.methodsError.usage(player, "check");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("board"))
                    {
                        if (args.length == 1)
                        {
                            p.methodsHand.displayBoard(player);
                        } else
                        {
                            p.methodsError.usage(player, "board");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("money"))
                    {
                        if (args.length == 1)
                        {
                            p.methodsHand.displayMoney(player);
                        } else
                        {
                            p.methodsError.usage(player, "money");
                        }
                        return true;
                    }
                    player.sendMessage(p.pluginTag + p.red + "No such hand command. Check help with /hand help.");
                    return true;
                } else
                {
                    p.methodsHand.displayHand(player);
                }
            } else
            {
                p.methodsError.notPlayer(sender);
            }
        }
        if (command.getName().equalsIgnoreCase("table"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                if (args.length > 0)
                {
                    String action = args[0];
                    if (action.equalsIgnoreCase("help"))
                    {
                        if (args.length == 1)
                        {
                            p.methodsError.displayHelp(player, "table");
                        } else if (args.length == 2)
                        {
                            p.methodsError.displayHelp(player, args[1]);
                        } else
                        {
                            p.methodsError.usage(player, "tablehelp");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("list"))
                    {
                        if (args.length == 1)
                        {
                            p.methodsTable.displayTables(player);
                        } else
                        {
                            p.methodsError.usage(player, "list");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("create") || action.equalsIgnoreCase("new"))
                    {
                        if (args.length == 2)
                        {
                            p.methodsTable.createTable(player, args[1]);
                        } else
                        {
                            p.methodsError.usage(player, "create");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("delete") || action.equalsIgnoreCase("del"))
                    {
                        if (args.length == 1)
                        {
                            p.methodsTable.deleteTable(player);
                        } else
                        {
                            p.methodsError.usage(player, "delete");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("open") || action.equalsIgnoreCase("o"))
                    {
                        if (args.length == 1)
                        {
                            p.methodsTable.openTable(player);
                        } else
                        {
                            p.methodsError.usage(player, "open");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("close") || action.equalsIgnoreCase("c"))
                    {
                        if (args.length == 1)
                        {
                            p.methodsTable.closeTable(player);
                        } else
                        {
                            p.methodsError.usage(player, "close");
                        }
                        return true;
                    }

                    if (action.equalsIgnoreCase("set"))
                    {
                        if (args.length == 3)
                        {
                            p.methodsTable.setSetting(player, args[1], args[2]);
                        } else
                        {
                            p.methodsError.usage(player, "set");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("listsettings"))
                    {
                        if (args.length == 1)
                        {
                            p.methodsTable.availableSettings(player);
                        } else
                        {
                            p.methodsError.usage(player, "listsettings");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("sit"))
                    {
                        if (args.length == 3)
                        {
                            p.methodsTable.sitTable(player, args[1], args[2]);
                        } else
                        {
                            p.methodsError.usage(player, "sit");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("getup") || action.equalsIgnoreCase("standup"))
                    {
                        if (args.length == 1)
                        {
                            p.methodsTable.leaveTable(player);
                        } else
                        {
                            p.methodsError.usage(player, "getup");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("tp"))
                    {
                        if (args.length == 2)
                        {
                            p.methodsTable.tpToTable(player, args[1]);
                        } else
                        {
                            p.methodsError.usage(player, "teleport");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("start"))
                    {
                        if (args.length == 1)
                        {
                            p.methodsTable.startTable(player);
                        } else
                        {
                            p.methodsError.usage(player, "start");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("details"))
                    {
                        if (args.length == 2)
                        {
                            p.methodsTable.listDetails(player, args[1]);
                        } else
                        {
                            p.methodsError.usage(player, "details");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("board"))
                    {
                        if (args.length == 2)
                        {
                            p.methodsTable.displayBoard(player, args[1]);
                        } else
                        {
                            p.methodsError.usage(player, "board");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("pay"))
                    {
                        if (args.length == 2)
                        {
                            p.methodsTable.payPot(player, args[1]);
                        } else
                        {
                            p.methodsError.usage(player, "pay");
                        }
                        return true;
                    }
                    if (action.equalsIgnoreCase("cardsleft"))
                    {
                        Table table = p.methodsCheck.isATable(0);
                        player.sendMessage(Integer.toString(table.deck.cards.size()));
                    }
                    player.sendMessage(p.pluginTag + p.red + "No such table command. Check help with /table help.");
                    return true;
                } else
                {
                    p.methodsError.displayHelp(player, "table");
                }
            } else
            {
                p.methodsError.notPlayer(sender);
            }
        }
        return true;
    }
}
