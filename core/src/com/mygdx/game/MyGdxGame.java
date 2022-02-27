package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.units.Archer;
import com.mygdx.game.units.Commander;
import com.mygdx.game.units.Unit;
import com.mygdx.game.weapons.Bow;
import com.mygdx.game.weapons.Sword;

import java.util.*;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
	private static final Logger LOGGER = new Logger(MyGdxGame.class.getName());

	private SettingsManager settings = SettingsManager.getInstance();

	SpriteBatch batch;
	BitmapFont font;
	Map<Point, Unit> unitPositions;

	Texture startButton;
	Texture editorButton;
	Texture configButton;
	Texture exitButton;

	GameMap current;

	//State Machine for Game States
	private final HierarchicalStateMachine gameState;
	private final State loading;
	private final State mainmenu;
	private final HierarchicalStateMachine game;
	private final State gameDefault;
	private final State gameSelected;
	private final State gameContextMenu;
	private final State gameChooseTarget;
	private final State gameBattle;
	private final HierarchicalStateMachine editor;
	private final State exit;

	private Stack<UndoableCommand> commands;

	private Range range;
	private Color color;
	private Selector selector;
	private Unit primarySelection;
	private Unit secondarySelection;
	private ContextMenu contextMenu;
	private Texture undo;
	private Texture next;
	private Texture attack;
	private Texture close;

	int turnPlayer = 0;

	int[] lastClick;

	public MyGdxGame(){
		gameState = new HierarchicalStateMachine("gameStates");
		loading = new State("loading");
		mainmenu = new State("mainmenu");
		game = new HierarchicalStateMachine("game", false);
		// The basic state of the game logic, calculates unit positions
		gameDefault = new State("default");
		gameSelected = new State("selected");
		gameContextMenu = new State("context");
		gameChooseTarget = new State("target");
		gameBattle = new State("battle");
		editor = new HierarchicalStateMachine("editor", false);
		exit = new State("exit");
		gameState.addTransition(loading, "loaded", mainmenu);
		gameState.addTransition(mainmenu, "start", game);
		gameState.addTransition(mainmenu, "edit", editor);
		gameState.addTransition(game, "toMenu", mainmenu);
		game.addTransition(gameDefault, "click", gameSelected);
		game.addTransition(gameSelected, "click", gameDefault);
		game.addTransition(gameDefault, "context", gameContextMenu);
		game.addTransition(gameContextMenu, "close", gameDefault);
		game.addTransition(gameContextMenu, "target", gameChooseTarget);
		game.addTransition(gameChooseTarget, "close", gameDefault);
		game.addTransition(gameChooseTarget, "battle", gameBattle);
		game.addTransition(gameBattle, "finished", gameDefault);
		game.setStart(gameDefault);
		gameState.addTransition(editor, "toMenu", mainmenu);
		gameState.addTransition(mainmenu, "exit", exit);
		gameState.setStart(loading);

		commands = new Stack<>();
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		current = GameMap.loadMap(Gdx.files.internal("maps/test.map").path());
		unitPositions = new HashMap<>();

		startButton = new Texture(Gdx.files.internal("textures/Start.png"));
		editorButton = new Texture(Gdx.files.internal("textures/Editor.png"));
		configButton = new Texture(Gdx.files.internal("textures/Config.png"));
		exitButton = new Texture(Gdx.files.internal("textures/Exit.png"));

		undo = new Texture(Gdx.files.internal("textures/Undo.png"));
		next = new Texture(Gdx.files.internal("textures/Next.png"));
		attack = new Texture(Gdx.files.internal("textures/Attack.png"));
		close = new Texture(Gdx.files.internal("textures/Close.png"));

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
		} else if (mainmenu.equals(gameState.getCurrent())) {
			if (lastClick != null && lastClick[3] == Input.Buttons.LEFT) {
				if (clickInBoundingBox(lastClick, 660, 1260, 900, 800)) {
					gameState.transition("start");
					System.out.println("start");
				}
				else if (clickInBoundingBox(lastClick, 660, 1260, 750, 650)) {
					gameState.transition("edit");
					System.out.println("edit");
				}
				else if (clickInBoundingBox(lastClick, 660, 1260, 600, 500)) {
					gameState.transition("config");
					System.out.println("config");
				}
				else if (clickInBoundingBox(lastClick, 660, 1260, 450, 350)) {
					gameState.transition("exit");
					System.out.println("exit");
				}
			}
			lastClick = null;
			renderMenu();
		} else if (game.equals(gameState.getCurrent())) {
			if (lastClick != null) {
				if (gameDefault.equals(gameState.getCurrent().getCurrent())) {
					unitPositions.clear();
					for (Unit unit : current.getUnits()){
						unitPositions.put(new Point(unit.getPositionX(), unit.getPositionY()), unit);
					}
					Unit unit = unitPositions.get(clickedTile(lastClick));
					if (unit != null
							&& lastClick[3] == Input.Buttons.LEFT && unit.getMovePoints() > 0) {
						range = new Range(current, unitPositions, unit.getMovePoints(), unit.getPositionX(), unit.getPositionY(), true, false, false, unit.getOwner());
						color = Color.GREEN;
						primarySelection = unit;
						gameState.transition("click");
						System.out.println(unit);
					} else if (unit != null
							&& lastClick[3] == Input.Buttons.RIGHT) {
						gameState.transition("context");
						if (!unit.hasAttacked()) {
							contextMenu = new ContextMenu(lastClick[0], lastClick[1], batch, attack, close) {
								@Override
								public void clickMenu(int x, int y) {
									switch (this.getClickedButton(x, y)) {
										case 0:
											primarySelection = unit;
											range = new Range(current, unitPositions, unit.getWeapon().getMinDistance(), unit.getWeapon().getMaxDistance(),
													unit.getPositionX(), unit.getPositionY(), unit.isWalking(), unit.isFlying(), unit.isSwimming(), true, unit.getOwner());
											color = Color.BLUE;
											selector = unit.getWeapon().target(turnPlayer);
											game.transition("target");
											break;
										case 1:
											gameState.transition("close");
											break;
									}
								}
							};
						} else {
							contextMenu = new ContextMenu(lastClick[0], lastClick[1], batch, close) {
								@Override
								public void clickMenu(int x, int y) {
									if (this.getClickedButton(x, y) == 0) {
										gameState.transition("close");
									}
								}
							};

						}
						lastClick = null;
					} else if (unit == null && lastClick[3] == Input.Buttons.RIGHT){
						gameState.transition("context");
						contextMenu = new ContextMenu(lastClick[0], lastClick[1], batch, undo, next, close) {
							@Override
							public void clickMenu(int x, int y) {
								switch (this.getClickedButton(x, y)){
									case 0:
										if (!commands.empty()) {
											UndoableCommand lastCommand = commands.pop();
											lastCommand.undo();
										}
										break;
									case 1:
										for (Unit unit : current.getUnits()) {
											if (unit.getOwner() == turnPlayer) {
												unit.endTurn();
											}
										}
										commands.clear();
										turnPlayer = (1 + turnPlayer) % 2;//TODO move numPlayers into map
										gameState.transition("close");
										break;
									case 2:
										gameState.transition("close");
										break;
								}
							}
						};
						lastClick = null;
					}
				} else if (gameSelected.equals(gameState.getCurrent().getCurrent())){
					if (primarySelection.getOwner() == turnPlayer && range.getDistance(lastClick[0] / 64, (1080 - lastClick[1]) / 64) >= 0
							&& range.getDistance(lastClick[0] / 64, (1080 - lastClick[1]) / 64) < Integer.MAX_VALUE
							&& lastClick[3] == Input.Buttons.LEFT) {
						range = null;
						gameState.transition("click");
						UndoableCommand moveCommand = primarySelection.move(lastClick[0] / 64, (1080 - lastClick[1]) / 64);
						moveCommand.execute();
						commands.push(moveCommand);
					} else if (range.getDistance(lastClick[0] / 64, (1080 - lastClick[1]) / 64) >= 0
							&& range.getDistance(lastClick[0] / 64, (1080 - lastClick[1]) / 64) < Integer.MAX_VALUE
							&& lastClick[3] == Input.Buttons.RIGHT){
						range = null;
						gameState.transition("click");
					}
				} else if (gameChooseTarget.equals(gameState.getCurrent().getCurrent())){
					Unit unit = unitPositions.get(new Point(lastClick[0] / 64, (1080 - lastClick[1]) / 64));
					if (unit != null && selector.select(unit) && range.getDistance(lastClick[0] / 64, (1080 - lastClick[1]) / 64) >= 0
							&& range.getDistance(lastClick[0] / 64, (1080 - lastClick[1]) / 64) < Integer.MAX_VALUE
							&& lastClick[3] == Input.Buttons.LEFT) {
						gameState.transition("battle");
						selector = null;
						secondarySelection = unit;
					}
					else if (unit == null && lastClick[3] == Input.Buttons.RIGHT){
						range = null;
						gameState.transition("close");
					}
				}
			}
			renderGame(range, color, selector);
			if (gameBattle.equals(gameState.getCurrent().getCurrent())){
				//TODO battle animation
				commands.clear();
				primarySelection.dealDamage(secondarySelection);

				List<Unit> toRemove = new LinkedList<>();
				for (Unit unit : current.getUnits()){
					if (unit.getHealth() <= 0){
						//TODO destroy animation
						toRemove.add(unit);
					}
				}
				current.getUnits().removeAll(toRemove);
				for (Unit unit : toRemove){
					unit.destroy();
				}
				range = null;
				gameState.transition("finished");
			}
			if (gameContextMenu.equals(gameState.getCurrent().getCurrent())){
				if(lastClick != null && lastClick[3] == Input.Buttons.LEFT){
					contextMenu.clickMenu(lastClick[0], lastClick[1]);
				} else if(lastClick != null && lastClick[3] == Input.Buttons.RIGHT){
					gameState.transition("close");
				}
				contextMenu.draw();
			}
			lastClick = null;
		} else if (exit.equals(gameState.getCurrent())) {
			Gdx.app.exit();
		}
		batch.end();
	}

	private boolean clickInBoundingBox(int[] lastClick, int left, int right, int top, int bottom){
		return lastClick[0] >= left && lastClick[0] <= right && lastClick[1] >= 1080 - top && lastClick[1] <= 1080 - bottom;
	}

	private Point clickedTile(int[] lastClick){
		return new Point(lastClick[0] / 64, current.getHeight() - lastClick[1] / 64 - 1);
	}

	private void renderMenu() {
		batch.draw(startButton, 660, 800);
		batch.draw(editorButton, 660, 650);
		batch.draw(configButton, 660, 500);
		batch.draw(exitButton, 660, 350);
	}

	public void renderGame(Range range, Color color, Selector selector){
		int height = current.getHeight();
		for(int y = 0; y < current.getHeight(); y++){
			for(int x = 0; x < current.getWidth(); x++){
				int distance = Integer.MAX_VALUE;
				if (range != null) {
					distance = range.getDistance(x, height - y - 1);
				}
				if (distance < Integer.MAX_VALUE && distance >= 0) {
					batch.setColor(color);
				}
				batch.draw(current.getTexture(x, y), 64 * x, 64 * (height - y - 1), 64, 64);
				batch.setColor(Color.WHITE);
				if (settings.getBooleanSetting("DebugShowDistances") && range != null) {
					font.setColor(Color.BLUE);
					font.getData().setScale(2f);
					String distString = "" + range.getDistance(x, height - y - 1);
					if (!distString.equals("" + Integer.MAX_VALUE)) {
						font.draw(batch, distString, 64 * x, 64 * (height - y));
					}
				}
			}
		}
		for (Unit unit : current.getUnits()) {
			int distance = Integer.MAX_VALUE;
			batch.setColor(current.getPlayers().get(unit.getOwner()).getPlayerColor());
			if (range != null) {
				distance = range.getDistance(unit.getPositionX(), height - unit.getPositionY() - 1);
			}
			if (selector != null && distance < Integer.MAX_VALUE && distance >= 0 && selector.select(unit)) {
				batch.setColor(color);
			}
			if (unit.hasAttacked()){
				batch.setColor(Color.GRAY);
			}
			batch.draw(unit.getTexture(), unit.getPositionX() * 64,unit.getPositionY() * 64, 64, 64);
			batch.setColor(Color.WHITE);
		}
		//TODO add round Counter
	}

	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
		startButton.dispose();
		editorButton.dispose();
		configButton.dispose();
		exitButton.dispose();
		current.destroyMap();

		undo.dispose();
		next.dispose();
		attack.dispose();
		close.dispose();
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
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
