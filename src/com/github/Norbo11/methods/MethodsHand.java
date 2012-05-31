/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: MethodsHand.java
 * -Contains methods that handle /hand commands. These methods mostly point to the PokerPlayer methods
 * and simply act as "checkers" - they simply test for conditions with if statements.
 * ===================================================================================================
 */

package com.github.norbo11.methods;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.norbo11.UltimatePoker;
import com.github.norbo11.classes.PokerPlayer;

public class MethodsHand
{
    public MethodsHand(UltimatePoker p)
    {
        this.p = p;
    }

    UltimatePoker p;

    // Declares the specified player all in
    public void allIn(Player player) throws Exception
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.eliminated == false)
            {
                if (pokerPlayer.table.stopped == false)
                {
                    if (pokerPlayer.table.inProgress) // Check if the table is in progress
                    {
                        if (pokerPlayer.table.currentPhase != 4)
                        {
                            if (pokerPlayer.action == true)
                            {
                                if (pokerPlayer.folded == false)
                                {
                                    if (pokerPlayer.allIn == 0) // Check if the player is not lready all in
                                    {
                                        // Check if the player has enough money to call. This is done by taking their current bet away from the table current bet, effectively getting the amount that they need to call.
                                        // Then add the minimum raise to it.
                                        if ((pokerPlayer.table.currentBet - pokerPlayer.currentBet) + pokerPlayer.table.minRaise > pokerPlayer.money) pokerPlayer.allIn();
                                        else p.methodsError.cantAllIn(player);
                                    } else p.methodsError.playerIsAllIn(player);
                                } else p.methodsError.playerIsFolded(player);
                            } else p.methodsError.notYourTurn(player);
                        } else p.methodsError.tableIsAtShowdown(player);
                    } else p.methodsError.noPotsOnTable(player);
                } else p.methodsError.tableIsStopped(player);
            } else p.methodsError.playerIsEliminated(player);
        } else p.methodsError.notAPokerPlayer(player);
    }

    // Bets the specified amount in the name of the specified player.
    public void bet(Player player, String amount) throws Exception
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.eliminated == false)
            {
                if (pokerPlayer.table.stopped == false)
                {
                    if (pokerPlayer.table.inProgress == true)
                    {
                        if (pokerPlayer.table.currentPhase != 4)
                        {
                            if (pokerPlayer.action == true)
                            {
                                if (pokerPlayer.folded == false)
                                {
                                    if (pokerPlayer.allIn == 0) // Check if the player is not all in
                                    {
                                        if (p.methodsCheck.isDouble(amount))
                                        {
                                            if (pokerPlayer.money >= Double.parseDouble(amount) - pokerPlayer.currentBet) // Check if the player has enough money to perform the raise.
                                            {
                                                List<PokerPlayer> canCall = new ArrayList<PokerPlayer>();
                                                // Go through all players on the table, and add them to "haveLessThanRaise" if they cannot call this bet/raise.
                                                for (PokerPlayer temp : pokerPlayer.table.getNonFoldedPlayers())
                                                    if (temp.money >= Double.parseDouble(amount) - temp.currentBet && temp != pokerPlayer) canCall.add(temp);

                                                if (canCall.size() >= 1) // If there is at least one person that can call the bet
                                                {
                                                    if (Double.parseDouble(amount) >= pokerPlayer.table.currentBet + pokerPlayer.table.minRaise) // Check if raise is at least the minraise.
                                                    {
                                                        pokerPlayer.bet(Double.parseDouble(amount));
                                                    } else p.methodsError.cantRaise(player, pokerPlayer.table.minRaise, pokerPlayer.table.currentBet);
                                                } else p.methodsError.tableNoCallers(player, amount, pokerPlayer.table.getHighestCallingAmount(pokerPlayer));
                                            } else p.methodsError.notEnoughMoney(player, amount, pokerPlayer.money);
                                        } else p.methodsError.notANumber(player, amount);
                                    } else p.methodsError.playerIsAllIn(player);
                                } else p.methodsError.playerIsFolded(player);
                            } else p.methodsError.notYourTurn(player);
                        } else p.methodsError.tableIsAtShowdown(player);
                    } else p.methodsError.tableIsNotInProgress(player);
                } else p.methodsError.tableIsStopped(player);
            } else p.methodsError.playerIsEliminated(player);
        } else p.methodsError.notAPokerPlayer(player);
    }

    // Calls the latest bet in the name of the player.
    public void call(Player player) throws Exception
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.eliminated == false)
            {
                if (pokerPlayer.table.stopped == false)
                {
                    if (pokerPlayer.table.inProgress == true)
                    {
                        if (pokerPlayer.table.currentPhase != 4)
                        {
                            if (pokerPlayer.action == true)
                            {
                                if (pokerPlayer.folded == false)
                                {
                                    if (pokerPlayer.allIn == 0) // Check if the player is not all in
                                    {
                                        if (pokerPlayer.currentBet < pokerPlayer.table.currentBet) // Check if the player hasn't already called
                                        {
                                            if (pokerPlayer.money >= pokerPlayer.table.currentBet - pokerPlayer.currentBet) // Check if the player has enough money to call.
                                            pokerPlayer.call();
                                            else p.methodsError.notEnoughMoney(player, Double.toString(pokerPlayer.table.currentBet), pokerPlayer.money);
                                        } else p.methodsError.cantCall(player);
                                    } else p.methodsError.playerIsAllIn(player);
                                } else p.methodsError.playerIsFolded(player);
                            } else p.methodsError.notYourTurn(player);
                        } else p.methodsError.tableIsAtShowdown(player);
                    } else p.methodsError.tableIsNotInProgress(player);
                } else p.methodsError.tableIsStopped(player);
            } else p.methodsError.playerIsEliminated(player);
        } else p.methodsError.notAPokerPlayer(player);
    }

    // Checks the turn of the specified player
    public void check(Player player) throws Exception
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.eliminated == false)
            {
                if (pokerPlayer.table.stopped == false)
                {
                    if (pokerPlayer.table.inProgress == true)
                    {
                        if (pokerPlayer.table.currentPhase != 4)
                        {
                            if (pokerPlayer.action == true)
                            {
                                if (pokerPlayer.folded == false)
                                {
                                    if (pokerPlayer.allIn == 0) // Check if the player is not all in
                                    {
                                        if (pokerPlayer.currentBet == pokerPlayer.table.currentBet) // Check if the player already contributed enough (and therefore can check)
                                        pokerPlayer.check();
                                        else p.methodsError.cantCheck(pokerPlayer);
                                    } else p.methodsError.playerIsAllIn(player);
                                } else p.methodsError.playerIsFolded(player);
                            } else p.methodsError.notYourTurn(player);
                        } else p.methodsError.tableIsAtShowdown(player);
                    } else p.methodsError.tableIsNotInProgress(player);
                } else p.methodsError.tableIsStopped(player);
            } else p.methodsError.playerIsEliminated(player);
        } else p.methodsError.notAPokerPlayer(player);
    }

    // Displays the board to the specified player
    public void displayBoard(Player player) throws Exception
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            pokerPlayer.table.displayBoard(player);
        } else p.methodsError.notAPokerPlayer(player);
    }

    // Displays the hand to the specified player
    public void displayHand(Player player) throws Exception
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.cards.size() > 0) // Check if the player has a hand
            pokerPlayer.displayHand();
            else p.methodsError.playerHasNoHand(player);
        } else p.methodsError.notAPokerPlayer(player);
    }

    // Displays the player's money just to the player
    public void displayMoney(Player player) throws Exception
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null) pokerPlayer.displayMoney();
        else p.methodsError.notAPokerPlayer(player);
    }

    // Displays the table's pots to the player
    public void displayPot(Player player) throws Exception
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.table.pots.size() > 0) // Check if there are any pots at the table
            pokerPlayer.displayPot();
            else p.methodsError.noPotsOnTable(player);
        } else p.methodsError.notAPokerPlayer(player);
    }

    // Folds the hand of the specified player
    public void fold(Player player) throws Exception
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.eliminated == false)
            {
                if (pokerPlayer.table.stopped == false)
                {
                    if (pokerPlayer.table.inProgress == true)
                    {
                        if (pokerPlayer.action == true)
                        {
                            if (pokerPlayer.folded == false)
                            {
                                if (pokerPlayer.allIn == 0 || pokerPlayer.table.currentPhase == 4) // Check if the player is not all in
                                pokerPlayer.fold();
                                else p.methodsError.playerIsAllIn(player);
                            } else p.methodsError.playerIsFolded(player);
                        } else p.methodsError.notYourTurn(player);
                    } else p.methodsError.tableIsNotInProgress(player);
                } else p.methodsError.tableIsStopped(player);
            } else p.methodsError.playerIsEliminated(player);
        } else p.methodsError.notAPokerPlayer(player);
    }

    // Adds money to the speicifed player
    public void reBuy(Player player, String amount) throws Exception
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.table.elimination == false) // Check if the table is in elimination mode (if it is, they cant rebuy)
            {
                if (pokerPlayer.table.inProgress == false)
                {
                    if (p.methodsCheck.isDouble(amount))
                    {
                        if (p.ECONOMY.has(player.getName(), Double.parseDouble(amount))) // Check if the player has that amount
                        pokerPlayer.reBuy(Double.parseDouble(amount));
                        else p.methodsError.notEnoughMoney(player, amount, p.ECONOMY.getBalance(player.getName()));
                    } else p.methodsError.notANumber(player, amount);
                } else p.methodsError.tableIsInProgress(player);
            } else p.methodsError.tableIsInEliminationMode(player);
        } else p.methodsError.notAPokerPlayer(player);
    }

    public void reveal(Player player) throws Exception
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.eliminated == false)
            {
                if (pokerPlayer.table.currentPhase == 4) // If it is showdown
                {
                    if (pokerPlayer.action == true)
                    {
                        pokerPlayer.revealHand();
                    } else p.methodsError.notYourTurn(player);
                } else p.methodsError.cantReveal(player);
            } else p.methodsError.playerIsEliminated(player);
        } else p.methodsError.notAPokerPlayer(player);
    }

    public void withdraw(Player player, String amt)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.table.elimination == false) // Check if the table is in elimination mode (if it is, they can't withdraw)
            {
                if (pokerPlayer.table.inProgress == false)
                {
                    if (p.methodsCheck.isDouble(amt))
                    {
                        double amount = Double.parseDouble(amt);
                        if (amount <= pokerPlayer.money)
                        {
                            pokerPlayer.money = pokerPlayer.money - amount;
                            p.ECONOMY.depositPlayer(player.getName(), amount);
                            p.methodsMisc.addToLog(p.getDate() + " [ECONOMY] Depositing " + amount + " to " + player.getName());
                            p.methodsMisc.sendToAllWithinRange(pokerPlayer.table.location, p.PLUGIN_TAG + p.gold + player.getName() + p.white + " withdraws " + p.gold + p.methodsMisc.formatMoney(amount) + p.white + " New balance: " + p.gold + p.methodsMisc.formatMoney(pokerPlayer.money));
                        } else p.methodsError.notEnoughChips(player, pokerPlayer.money, amount);
                    } else p.methodsError.notANumber(player, amt);
                } else p.methodsError.tableIsInProgress(player);
            } else p.methodsError.tableIsInEliminationMode(player);
        } else p.methodsError.notAPokerPlayer(player);
    }
}
