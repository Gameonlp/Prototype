package com.tilebased.game.logic;

public interface UndoableCommand extends Command{
    void undo();
}
