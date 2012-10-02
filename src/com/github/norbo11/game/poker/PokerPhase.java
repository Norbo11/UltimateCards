package com.github.norbo11.game.poker;

public enum PokerPhase
{
    PREFLOP(0), FLOP(1), TURN(2), RIVER(3), SHOWDOWN(4), HAND_END(5);

    private int number;

    PokerPhase(int number)
    {
        this.number = number;
    }

    public int getNumber()
    {
        return number;
    }
}
