package com.tilebased.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tilebased.game.logic.GameHandler;
import com.tilebased.game.logic.HierarchicalStateMachine;
import com.tilebased.game.logic.State;
import com.tilebased.game.util.ClickLocation;

public class TileBased extends ApplicationAdapter implements InputProcessor {
	private static final Logger LOGGER = new Logger(TileBased.class.getName());

	private final SettingsManager settings = SettingsManager.getInstance();

	private ClickLocation loc;

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

	OrthographicCamera camera;
	private Viewport viewport;

	int[] lastClick;
	int[] mousePos;
	private Queue<Integer> lastKeys;

	public TileBased(){
		gameState = new HierarchicalStateMachine("gameState");
		loading = new State("loading");
		mainMenu = new State("mainMenu");
		game = new HierarchicalStateMachine(false, "game");
		State gameLoading = new State("loading");
		HierarchicalStateMachine inMap = new HierarchicalStateMachine(false, "inMap");
		State inMapDefault = new State("default");
		State inMapSelected = new State("selected");
		State inMapMove = new State("move");
		State inMapContextMenu = new State("contextMenu");
		State inMapChooseTarget = new State("chooseTarget");
		State inMapBattle = new State("battle");
		State inMapTurnEnd = new State("turnEnd");
		State inMapTurnStart = new State("turnStart");
		State inMapMenu = new State("menu");
		editor = new HierarchicalStateMachine(false, "editor");
		exit = new State("exit");
		gameState.addTransition(loading, "loaded", mainMenu);
		gameState.addTransition(mainMenu, "start", game);
		gameState.addTransition(mainMenu, "edit", editor);
		gameState.addTransition(game, "toMainMenu", this::call, mainMenu);
		game.addTransition(gameLoading, "loaded", inMap);
		game.addTransition(inMap,"toMenu", inMapMenu);
		game.addTransition(inMapMenu,"back", inMap);
		game.setStart(gameLoading);
		inMap.addTransition(inMapDefault, "click", inMapSelected);
		inMap.addTransition(inMapDefault, "endTurn", inMapTurnEnd);
		inMap.addTransition(inMapDefault, "context", inMapContextMenu);
		inMap.addTransition(inMapSelected, "click", inMapMove);
		inMap.addTransition(inMapSelected, "cancel", inMapDefault);
		inMap.addTransition(inMapMove, "finished", inMapDefault);
		inMap.addTransition(inMapContextMenu, "close", inMapDefault);
		inMap.addTransition(inMapContextMenu, "endTurn", inMapTurnEnd);
		inMap.addTransition(inMapContextMenu, "target", inMapChooseTarget);
		inMap.addTransition(inMapChooseTarget, "close", inMapDefault);
		inMap.addTransition(inMapChooseTarget, "battle", inMapBattle);
		inMap.addTransition(inMapBattle, "finished", inMapDefault);
		inMap.addTransition(inMapTurnEnd, "nextTurn", inMapTurnStart);
		inMap.addTransition(inMapTurnStart, "start", inMapDefault);
		inMap.setStart(inMapTurnStart);
		gameState.addTransition(editor, "toMainMenu", mainMenu);
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

		lastKeys = new Queue<>();
		loc = new ClickLocation();

		camera = new OrthographicCamera();
		viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
		viewport.setScreenBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		viewport.apply();
		camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		if (loading.equals(gameState.getCurrent())) {
			//TODO load anything here
			gameState.transition("loaded");
		} else if (mainMenu.equals(gameState.getCurrent())) {
			if (lastClick != null && lastClick[3] == Input.Buttons.LEFT) {
				if (loc.clickInBoundingBox(lastClick, 660, 1260, 900, 800)) {
					gameState.transition("start");
				}
				else if (loc.clickInBoundingBox(lastClick, 660, 1260, 750, 650)) {
					//gameState.transition("edit");
				}
				else if (loc.clickInBoundingBox(lastClick, 660, 1260, 600, 500)) {
					//gameState.transition("config");
				}
				else if (loc.clickInBoundingBox(lastClick, 660, 1260, 450, 350)) {
					gameState.transition("exit");
				}
			}
			lastClick = null;
			renderMenu();
		} else if (game.equals(gameState.getCurrent())) {
			if (gameHandler == null) {
				gameHandler = new GameHandler(gameState);
			}
			gameHandler.handleGame(batch, lastClick, mousePos, lastKeys);
		} else if (exit.equals(gameState.getCurrent())) {
			Gdx.app.exit();
		}
		lastClick = null;
		batch.end();
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
		lastKeys.addLast(keycode);
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector3 vec = camera.unproject(new Vector3(screenX, screenY, 0));
		System.out.println(vec + " " + screenY);
		lastClick = new int[]{(int) vec.x, (int) vec.y, pointer, button};
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
		Vector3 vec = camera.unproject(new Vector3(screenX, screenY, 0));
		mousePos = new int[]{(int) vec.x, (int) vec.y};
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}

	private void call() {
		gameHandler.destroy();
		gameHandler = null;
	}
}
