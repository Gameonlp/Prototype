package com.tilebased.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.tilebased.game.logic.GameHandler;
import com.tilebased.game.logic.HierarchicalStateMachine;
import com.tilebased.game.logic.State;

public class TileBased extends ApplicationAdapter implements InputProcessor {
	private static final Logger LOGGER = new Logger(TileBased.class.getName());

	private final SettingsManager settings = SettingsManager.getInstance();

	SpriteBatch batch;

	Texture startButton;
	Texture editorButton;
	Texture configButton;
	Texture exitButton;

	private GameHandler gameHandler;

	//State Machine for Game States
	private final HierarchicalStateMachine gameState;
	private final State loading;
	private final State mainMenu;
	private final HierarchicalStateMachine game;
	private final HierarchicalStateMachine editor;
	private final State exit;

	int[] lastClick;
	int[] mousePos;

	public TileBased(){
		gameState = new HierarchicalStateMachine("gameState");
		loading = new State("loading");
		mainMenu = new State("mainMenu");
		game = new HierarchicalStateMachine(false, "game");
		State gameDefault = new State("gameDefault");
		State gameSelected = new State("gameSelected");
		State gameMove = new State("gameMove");
		State gameContextMenu = new State("gameContextMenu");
		State gameChooseTarget = new State("gameChooseTarget");
		State gameBattle = new State("gameBattle");
		State gameTurnEnd = new State("gameTurnEnd");
		State gameTurnStart = new State("gameTurnStart");
		editor = new HierarchicalStateMachine(false, "editor");
		exit = new State("exit");
		gameState.addTransition(loading, "loaded", mainMenu);
		gameState.addTransition(mainMenu, "start", game);
		gameState.addTransition(mainMenu, "edit", editor);
		gameState.addTransition(game, "toMenu", mainMenu);
		game.addTransition(gameDefault, "click", gameSelected);
		game.addTransition(gameDefault, "endTurn", gameTurnEnd);
		game.addTransition(gameDefault, "context", gameContextMenu);
		game.addTransition(gameSelected, "click", gameMove);
		game.addTransition(gameSelected, "cancel", gameDefault);
		game.addTransition(gameMove, "finished", gameDefault);
		game.addTransition(gameContextMenu, "close", gameDefault);
		game.addTransition(gameContextMenu, "endTurn", gameTurnEnd);
		game.addTransition(gameContextMenu, "target", gameChooseTarget);
		game.addTransition(gameChooseTarget, "close", gameDefault);
		game.addTransition(gameChooseTarget, "battle", gameBattle);
		game.addTransition(gameBattle, "finished", gameDefault);
		game.addTransition(gameTurnEnd, "nextTurn", gameTurnStart);
		game.addTransition(gameTurnStart, "start", gameDefault);
		game.setStart(gameTurnStart);
		gameState.addTransition(editor, "toMenu", mainMenu);
		gameState.addTransition(mainMenu, "exit", exit);
		gameState.setStart(loading);
	}

	@Override
	public void create () {
		batch = new SpriteBatch();

		startButton = new Texture(Gdx.files.internal("textures/Start.png"));
		editorButton = new Texture(Gdx.files.internal("textures/Editor.png"));
		configButton = new Texture(Gdx.files.internal("textures/Config.png"));
		exitButton = new Texture(Gdx.files.internal("textures/Exit.png"));

		Gdx.input.setInputProcessor(this);
		LOGGER.setLevel(Logger.DEBUG);
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		batch.begin();
		if (loading.equals(gameState.getCurrent())) {
			//TODO load anything here
			gameState.transition("loaded");
		} else if (mainMenu.equals(gameState.getCurrent())) {
			if (lastClick != null && lastClick[3] == Input.Buttons.LEFT) {
				if (clickInBoundingBox(lastClick, 660, 1260, 900, 800)) {
					gameState.transition("start");
				}
				else if (clickInBoundingBox(lastClick, 660, 1260, 750, 650)) {
					//gameState.transition("edit");
				}
				else if (clickInBoundingBox(lastClick, 660, 1260, 600, 500)) {
					//gameState.transition("config");
				}
				else if (clickInBoundingBox(lastClick, 660, 1260, 450, 350)) {
					gameState.transition("exit");
				}
			}
			lastClick = null;
			renderMenu();
		} else if (game.equals(gameState.getCurrent())) {
			if (gameHandler == null) {
				gameHandler = new GameHandler(gameState);
			}
			gameHandler.handleGame(batch, lastClick, mousePos);
		} else if (exit.equals(gameState.getCurrent())) {
			Gdx.app.exit();
		}
		lastClick = null;
		batch.end();
	}

	private boolean clickInBoundingBox(int[] lastClick, int left, int right, int top, int bottom){
		return lastClick[0] >= left && lastClick[0] <= right && lastClick[1] >= 1080 - top && lastClick[1] <= 1080 - bottom;
	}

	private void renderMenu() {
		batch.draw(startButton, 660, 800);
		batch.draw(editorButton, 660, 650);
		batch.draw(configButton, 660, 500);
		batch.draw(exitButton, 660, 350);
	}

	@Override
	public void dispose () {
		batch.dispose();
		startButton.dispose();
		editorButton.dispose();
		configButton.dispose();
		exitButton.dispose();
		if (gameHandler != null){
			gameHandler.destroy();
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		lastClick = new int[]{screenX, screenY, pointer, button};
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		mousePos = new int[]{screenX, screenY};
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
