package com.github.norbo11.game.poker;

public enum PokerPhase {
    PREFLOP(0), FLOP(1), TURN(2), RIVER(3), SHOWDOWN(4), HAND_END(5);

    PokerPhase(int number) {
        this.number = number;
    }

    private int number;

    public int getNumber() {
        return number;
    }
}
