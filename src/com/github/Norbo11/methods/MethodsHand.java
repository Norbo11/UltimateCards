package com.github.Norbo11.methods;

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
            if (pokerPlayer.table.inProgress == true)
            {
                if (pokerPlayer.action == true)
                {
                    if (p.methodsCheck.isDouble(amount))
                    {
                        if (Double.parseDouble(amount) > 0)
                        {
                            if (pokerPlayer.money >= Double.parseDouble(amount))
                            {
                                if (Double.parseDouble(amount) >= pokerPlayer.table.currentBet + pokerPlayer.table.minRaise)
                                {
                                    pokerPlayer.bet(Double.parseDouble(amount));
                                } else p.methodsError.cantRaise(player, pokerPlayer.table.minRaise);
                            } else p.methodsError.notEnoughMoney(player, amount, pokerPlayer.money);
                        } else player.sendMessage("You cannot bet 0");
                    } else p.methodsError.notANumber(player, amount);
                } else p.methodsError.notYourTurn(player);
            } else p.methodsError.tableNotInProgress(player);
        } else p.methodsError.notPokerPlayer(player);
    }

    public void call(Player player)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.table.inProgress == true)
            {
                if (pokerPlayer.action == true)
                {
                    if (pokerPlayer.totalBet < pokerPlayer.table.requiredContribution())
                    {
                        if (pokerPlayer.getBalance() >= pokerPlayer.table.currentBet)
                        {
                            pokerPlayer.call();
                        } else p.methodsError.notEnoughMoney(player, Double.toString(pokerPlayer.table.currentBet), p.economy.getBalance(player.getName()));
                    } else p.methodsError.cantCall(player);
                } else p.methodsError.notYourTurn(player);
            } else p.methodsError.tableNotInProgress(player);
        } else p.methodsError.notPokerPlayer(player);
    }

    public void check(Player player)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.table.inProgress == true)
            {
                if (pokerPlayer.action == true)
                {
                    if (pokerPlayer.table.currentBet == 0)
                    {
                        pokerPlayer.check();
                    } else p.methodsError.cantCheck(pokerPlayer);
                } else p.methodsError.notYourTurn(player);
            } else p.methodsError.tableNotInProgress(player);
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
            if (pokerPlayer.table.inProgress == true)
            {
                if (pokerPlayer.action == true)
                {
                    pokerPlayer.fold();
                } else p.methodsError.notYourTurn(player);
            } else p.methodsError.tableNotInProgress(player);
        } else p.methodsError.notPokerPlayer(player);
    }
}
