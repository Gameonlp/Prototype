package com.mygdx.game;

public interface UndoableCommand {
    void execute();
    void undo();
}
