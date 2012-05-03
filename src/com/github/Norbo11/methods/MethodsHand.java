package com.github.Norbo11.methods;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;
import com.github.Norbo11.classes.PokerPlayer;

public class MethodsHand
{
    UltimatePoker p;

    public MethodsHand(UltimatePoker p)
    {
        this.p = p;
    }

    public void bet(Player player, String amount)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.table.stopped == false)
            {
                if (pokerPlayer.table.inProgress == true)
                {
                    if (pokerPlayer.folded == false)
                    {
                        if (pokerPlayer.pot == null)
                        {
                            if (pokerPlayer.action == true)
                            {
                                if (p.methodsCheck.isDouble(amount))
                                {
                                    List<PokerPlayer> haveLessThanRaise = new ArrayList<PokerPlayer>();
                                    for (PokerPlayer temp : pokerPlayer.table.getNonFoldedPlayers())
                                    {
                                        if (temp.money < Double.parseDouble(amount) - temp.currentBet)
                                        haveLessThanRaise.add(temp);
                                    }
                                    if (pokerPlayer.table.players.size() != haveLessThanRaise.size() + 1)
                                    {
                                        if (Double.parseDouble(amount) >= pokerPlayer.table.currentBet + pokerPlayer.table.minRaise)
                                        {
                                            if (pokerPlayer.money >= Double.parseDouble(amount) - pokerPlayer.currentBet)
                                            {
                                                pokerPlayer.bet(Double.parseDouble(amount));
                                            } else p.methodsError.notEnoughMoney(player, amount, pokerPlayer.money);
                                        } else p.methodsError.cantRaise(player, pokerPlayer.table.minRaise, pokerPlayer.table.currentBet);
                                    } else p.methodsError.nobodyCanCall(player, amount);
                                } else p.methodsError.notANumber(player, amount);
                            } else p.methodsError.notYourTurn(player);
                        } else p.methodsError.playerIsAllIn(player);
                    } else p.methodsError.playerHasFolded(player);
                } else p.methodsError.tableNotInProgress(player);
            } else p.methodsError.tableIsStopped(player);
        } else p.methodsError.notPokerPlayer(player);
    }

    public void call(Player player)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.table.stopped == false)
            {
                if (pokerPlayer.table.inProgress == true)
                {
                    if (pokerPlayer.folded == false)
                    {
                        if (pokerPlayer.pot == null)
                        {
                            if (pokerPlayer.action == true)
                            {
                                if (pokerPlayer.currentBet < pokerPlayer.table.currentBet)
                                {
                                    if (pokerPlayer.money >= pokerPlayer.table.currentBet - pokerPlayer.currentBet)
                                    {
                                        pokerPlayer.call();
                                    } else p.methodsError.notEnoughMoney(player, Double.toString(pokerPlayer.table.currentBet), pokerPlayer.money);
                                } else p.methodsError.cantCall(player);
                            } else p.methodsError.notYourTurn(player);
                        } else p.methodsError.playerIsAllIn(player);
                    } else p.methodsError.playerHasFolded(player);
                } else p.methodsError.tableNotInProgress(player);
            } else p.methodsError.tableIsStopped(player);
        } else p.methodsError.notPokerPlayer(player);
    }

    public void check(Player player)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.table.stopped == false)
            {
                if (pokerPlayer.table.inProgress == true)
                {
                    if (pokerPlayer.folded == false)
                    {
                        if (pokerPlayer.pot == null)
                        {
                            if (pokerPlayer.action == true)
                            {
                                if (pokerPlayer.currentBet == pokerPlayer.table.currentBet)
                                {
                                    pokerPlayer.check();
                                } else p.methodsError.cantCheck(pokerPlayer);
                            } else p.methodsError.notYourTurn(player);
                        } else p.methodsError.playerIsAllIn(player);
                    } else p.methodsError.playerHasFolded(player);
                } else p.methodsError.tableNotInProgress(player);
            } else p.methodsError.tableIsStopped(player);
        } else p.methodsError.notPokerPlayer(player);
    }

    public void displayBoard(Player player)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            pokerPlayer.table.displayBoard(player);
        } else p.methodsError.notPokerPlayer(player);
    }

    public void displayHand(Player player)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            pokerPlayer.displayHand();
        } else p.methodsError.notPokerPlayer(player);
    }

    public void displayMoney(Player player)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (p.methodsCheck.isAPokerPlayer(player) != null)
        {
            pokerPlayer.displayMoney();
        } else p.methodsError.notPokerPlayer(player);
    }

    public void fold(Player player)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.table.stopped == false)
            {
                if (pokerPlayer.table.inProgress == true)
                {
                    if (pokerPlayer.folded == false)
                    {
                        if (pokerPlayer.pot == null)
                        {
                            if (pokerPlayer.action == true)
                            {
                                pokerPlayer.fold();
                            } else p.methodsError.notYourTurn(player);
                        } else p.methodsError.playerIsAllIn(player);
                    } else p.methodsError.playerHasFolded(player);
                } else p.methodsError.tableNotInProgress(player);
            } else p.methodsError.tableIsStopped(player);
        } else p.methodsError.notPokerPlayer(player);
    }

    public void reBuy(Player player, String amount)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.table.stopped == false)
            {
                if (pokerPlayer.eliminated == false && pokerPlayer.table.elimination == false)
                {
                    if (pokerPlayer.table.inProgress == false)
                    {
                        if (p.methodsCheck.isDouble(amount))
                        {
                            if (p.economy.has(player.getName(), Double.parseDouble(amount)))
                            {
                                pokerPlayer.reBuy(Double.parseDouble(amount));
                            } else p.methodsError.notEnoughMoney(player, amount, p.economy.getBalance(player.getName()));
                        } else p.methodsError.notANumber(player, amount);
                    } else p.methodsError.tableIsInProgress(player);
                } else p.methodsError.tableInEliminationMode(player);
            } else p.methodsError.tableIsStopped(player);
        } else p.methodsError.notPokerPlayer(player);
    }

    public void displayPot(Player player)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.table.inProgress == true)
            {
                pokerPlayer.displayPot();
            } else p.methodsError.tableNotInProgress(player);
        } else p.methodsError.notPokerPlayer(player);
    }

    public void allIn(Player player)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.table.stopped == false)
            {
                if (pokerPlayer.table.pots.size() > 0)
                {
                    if (pokerPlayer.folded == false)
                    {
                        if (pokerPlayer.pot == null)
                        {
                            if (pokerPlayer.table.currentBet - pokerPlayer.currentBet > pokerPlayer.money)
                            {
                                pokerPlayer.allIn();
                            } else p.methodsError.enoughMoneyToCall(player);
                        } else p.methodsError.playerIsAllIn(player);
                    } else p.methodsError.playerHasFolded(player);
                } else p.methodsError.noPotsOnTable(player);
            } else p.methodsError.tableIsStopped(player);
        } else p.methodsError.notPokerPlayer(player);
    }
}
