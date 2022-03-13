package com.tilebased.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.tilebased.game.Animation;
import com.tilebased.game.BattleAnimation;
import com.tilebased.game.ContextMenu;
import com.tilebased.game.GameMap;
import com.tilebased.game.MoveAnimation;
import com.tilebased.game.Path;
import com.tilebased.game.RNG;
import com.tilebased.game.SettingsManager;
import com.tilebased.game.player.Player;
import com.tilebased.game.player.aiplayer.AIPlayer;
import com.tilebased.game.player.aiplayer.strategy.plan.AttackStep;
import com.tilebased.game.player.aiplayer.strategy.plan.MoveStep;
import com.tilebased.game.player.aiplayer.strategy.plan.Plan;
import com.tilebased.game.resource.ResourceManager;
import com.tilebased.game.resource.TextureResource;
import com.tilebased.game.units.Selector;
import com.tilebased.game.units.Unit;
import com.tilebased.game.util.ClickLocation;
import com.tilebased.game.util.Point;
import com.tilebased.game.util.Range;

import java.util.*;

public class GameHandler {
    private final SettingsManager settings = SettingsManager.getInstance();
    private RNG rng;
    private Range range;
    private Path path;
    private Color color;
    private Selector selector;
    private Unit primarySelection;
    private Unit secondarySelection;
    private Point targetPoint;
    private GameMap currentMap;
    private final Map<Point, Unit> unitPositions;
    private final BitmapFont font;
    private GlyphLayout layout;
    private ShapeRenderer shapeRenderer;

    private Texture undo;
    private Texture next;
    private Texture attack;
    private Texture close;
    private TextureResource arrows;
    private TextureResource continueRes;
    private TextureResource exit;

    private String sentence;

    private final Stack<UndoableCommand> commands;

    HierarchicalStateMachine inMap;

    private ContextMenu contextMenu;

    private Animation animation;

    int turnPlayer = 0;
    int turn = 1;

    long pauseStartTime;
    long pausedMillis;

    private ClickLocation loc;

    boolean nextStep = true;
    Plan plan = null;
    AsyncResult<Plan> planContainer = null;
    AsyncExecutor Planner = new AsyncExecutor(4);
    private final HierarchicalStateMachine gameState;

    public GameHandler(HierarchicalStateMachine gameState){
        this.gameState = gameState;
        // The basic state of the game logic, calculates unit positions
        commands = new Stack<>();

        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();

        undo = new Texture(Gdx.files.internal("textures/Undo.png"));
        next = new Texture(Gdx.files.internal("textures/Next.png"));
        attack = new Texture(Gdx.files.internal("textures/Attack.png"));
        close = new Texture(Gdx.files.internal("textures/Close.png"));
        // load in Gl context to make it usable outside
        arrows = ResourceManager.getInstance().getTexture("textures/Arrows.png");
        exit = ResourceManager.getInstance().getTexture("textures/Exit.png");
        continueRes = ResourceManager.getInstance().getTexture("textures/Continue.png");

        inMap = gameState.getStateByName("game").getStateByName("inMap");

        currentMap = GameMap.loadMap(Gdx.files.internal("maps/test.map").path());
        unitPositions = new HashMap<>();
        layout = new GlyphLayout();

        rng = new RNG();
        loc = new ClickLocation();
    }

    public void handleGame(SpriteBatch batch, int[] lastClick, int[] mousePos, Queue<Integer> lastKeys) {
        if (gameState.getStateByName("game").getStateByName("loading").equals(gameState.getCurrent().getCurrent())){
            if (sentence == null){
                List<String> wittySentences =settings.getStringListSetting("WittySentences");
                sentence = wittySentences.get(new Random().nextInt(wittySentences.size()));
            }
            if (lastClick != null){
                gameState.transition("loaded");
            }
            renderLoading(batch);
        } else if (inMap.equals(gameState.getCurrent().getCurrent())) {
            handleInMap(batch, lastClick, mousePos, lastKeys);
        } else if (gameState.getStateByName("game").getStateByName("menu").equals(gameState.getCurrent().getCurrent())) {
            renderGame(batch, range, color, selector, null);
            if (contextMenu != null) {
                contextMenu.draw();
            }
            if (path != null) {
                if (unitPositions.get(path.get(0)).getOwner().getPlayerType() == Player.PlayerType.HUMAN) {
                    path.draw(batch);
                }
            }
            batch.end();
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.35f, 0.35f, 0.35f, 0.5f);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
            batch.begin();
            renderMenu(batch);
            boolean menu = false;
            while (!lastKeys.isEmpty()) {
                if (lastKeys.removeFirst() == Input.Keys.ESCAPE) {
                    menu = true;
                }
            }
            Texture cont = continueRes.getResource();
            if (menu || lastClick != null && loc.clickInBoundingBox(lastClick, (int) (Gdx.graphics.getWidth() / 2f - cont.getWidth() / 2f),
                    (int) (Gdx.graphics.getWidth() / 2f + cont.getWidth() / 2f), 800 + cont.getHeight(), 800)) {

                pausedMillis += TimeUtils.millis() - pauseStartTime;
                gameState.transition("back");
            }
            Texture ex = exit.getResource();
            if (lastClick != null && loc.clickInBoundingBox(lastClick, (int) (Gdx.graphics.getWidth() / 2f - ex.getWidth() / 2f),
                    (int) (Gdx.graphics.getWidth() / 2f + ex.getWidth() / 2f), 750, 750 - ex.getHeight())) {
                gameState.transition("toMainMenu");
                gameState.getStateByName("game").reset();
            }
        }
    }

    private void renderMenu(SpriteBatch batch) {
        Texture cont = continueRes.getResource();
        batch.draw(cont, Gdx.graphics.getWidth() / 2f - cont.getWidth() / 2f, 800);
        Texture ex = exit.getResource();
        batch.draw(ex, Gdx.graphics.getWidth() / 2f - ex.getWidth() / 2f, 800 - 50 - ex.getHeight());
    }

    private void renderLoading(SpriteBatch batch) {
        font.setColor(Color.ORANGE);
        font.getData().setScale(3f);
        layout.setText(font, sentence);
        font.draw(batch, sentence, 10, layout.height + 10);
    }

    private void handleInMap(SpriteBatch batch, int[] lastClick, int[] mousePos, Queue<Integer> lastKeys) {
        Player currentPlayer = currentMap.getPlayers().get(turnPlayer);
        boolean menu = false;
        while (!lastKeys.isEmpty()) {
            if (lastKeys.removeFirst() == Input.Keys.ESCAPE) {
                menu = true;
            }
        }
        if (menu){
            pauseStartTime = TimeUtils.millis();
            gameState.transition("toMenu");
        }
        if (currentPlayer.getPlayerType() == Player.PlayerType.AI
                && inMap.getStateByName("default").equals(gameState.getCurrent().getCurrent().getCurrent())){
            boolean just_started = false;
            if (planContainer == null && plan == null) {
                planContainer = Planner.submit(() -> ((AIPlayer) currentPlayer).handleTurn(currentMap, unitPositions));
            }
            if (planContainer != null && planContainer.isDone()) {
                plan = planContainer.get();
                planContainer = null;
                just_started = true;
            }
            if (plan != null && nextStep){
                nextStep = false;
                if (!just_started){
                    plan.evaluateConditions();
                }
                if(!plan.executeNext(step -> {
                    if (step.getType().equals("move")){
                        MoveStep current = (MoveStep) step;
                        primarySelection = null;
                        range = null;
                        if (path != null) {
                            path.destroy();
                        }
                        primarySelection = current.toMove;
                        targetPoint = current.moveTo;
                        path = current.moveAlong;
                        gameState.transition("click");
                        gameState.transition("click");
                    }
                    else if (step.getType().equals("attack")){
                        AttackStep current = (AttackStep) step;
                        primarySelection = current.attacker;
                        secondarySelection = current.target;
                        range = null;
                        gameState.transition("context");
                        gameState.transition("target");
                        gameState.transition("battle");
                    }
                })){
                    plan = null;
                    gameState.transition("endTurn");
                }
                clearUnits();
                Timer.schedule(new Timer.Task() {@Override public void run() {nextStep = true;}}, 1.0f);
            }
        }
        if (lastClick != null) {
            if (inMap.getStateByName("default").equals(gameState.getCurrent().getCurrent().getCurrent())) {
                handleGameDefault(batch, lastClick, mousePos);
                lastClick = null;
            } else if (gameState.getStateByName("game").getStateByName("inMap").getStateByName("selected").equals(gameState.getCurrent().getCurrent().getCurrent())){
                if (primarySelection.getOwner() == currentPlayer && currentPlayer.getPlayerType() == Player.PlayerType.HUMAN && range.getDistance(lastClick[0] / 64, lastClick[1] / 64) >= 0
                        && range.getDistance(lastClick[0] / 64, lastClick[1] / 64) < Integer.MAX_VALUE
                        && lastClick[3] == Input.Buttons.LEFT) {
                    range = null;
                    targetPoint = new Point(lastClick[0] / 64, lastClick[1] / 64);
                    lastClick = null;
                    gameState.transition("click");
                } else if (range.getDistance(lastClick[0] / 64, lastClick[1] / 64) >= 0
                        && range.getDistance(lastClick[0] / 64, lastClick[1] / 64) < Integer.MAX_VALUE
                        && lastClick[3] == Input.Buttons.RIGHT){
                    range = null;
                    path.destroy();
                    path = null;
                    lastClick = null;
                    gameState.transition("cancel");
                }
            } else if (inMap.getStateByName("chooseTarget").equals(gameState.getCurrent().getCurrent().getCurrent())){
                Unit unit = unitPositions.get(new Point(lastClick[0] / 64, lastClick[1] / 64));
                if (primarySelection.getOwner() == currentPlayer && unit != null && selector.select(unit) && range.getDistance(lastClick[0] / 64, lastClick[1] / 64) >= 0
                        && range.getDistance(lastClick[0] / 64, lastClick[1] / 64) < Integer.MAX_VALUE
                        && lastClick[3] == Input.Buttons.LEFT) {
                    gameState.transition("battle");
                    selector = null;
                    lastClick = null;
                    secondarySelection = unit;
                }
                else if (unit == null && lastClick[3] == Input.Buttons.RIGHT){
                    range = null;
                    lastClick = null;
                    gameState.transition("close");
                }
            }
        }
        renderGame(batch, range, color, selector, mousePos);
        if (inMap.getStateByName("move").equals(gameState.getCurrent().getCurrent().getCurrent())){
            long time = TimeUtils.millis() - pausedMillis;
            if (animation == null){
                primarySelection.setDraw(false);
                animation = new MoveAnimation(time, primarySelection, path, batch);
                path.destroy();
                path = null;
            }
            animation.play(time);
            if (animation.isDone(time)) {
                UndoableCommand moveCommand = primarySelection.move(targetPoint);
                moveCommand.execute();
                commands.push(moveCommand);
                primarySelection.setDraw(true);
                animation = null;
                gameState.transition("finished");
            }
        }
        if (inMap.getStateByName("battle").equals(gameState.getCurrent().getCurrent().getCurrent())){
            long time = TimeUtils.millis() - pausedMillis;
            if (animation == null) {
                animation = new BattleAnimation(time, primarySelection, secondarySelection, batch);
                commands.clear();
                primarySelection.dealDamage(secondarySelection, rng).execute();
            }
            animation.play(time);
            if (((BattleAnimation) animation).firstDone(time) && !((BattleAnimation) animation).isRevenging()) {
                Range revengeRange = new Range(currentMap, unitPositions, secondarySelection, secondarySelection.getWeapon());
                int distance = revengeRange.getDistance(new Point(primarySelection));
                if (secondarySelection.getHealth() > 0 && distance >= 0 && distance < Integer.MAX_VALUE){
                    ((BattleAnimation) animation).revenge();
                } else {
                    ((BattleAnimation) animation).noRevenge();
                }
            }
            if (animation.isDone(time)) {
                if (((BattleAnimation) animation).isRevenging()){
                    secondarySelection.revenge(primarySelection, rng).execute();
                }
                clearUnits();
                range = null;
                animation = null;
                gameState.transition("finished");
            }
        }
        if (inMap.getStateByName("contextMenu").equals(gameState.getCurrent().getCurrent().getCurrent())){
            contextMenu.draw();
            if(lastClick != null && lastClick[3] == Input.Buttons.LEFT){
                contextMenu.clickMenu(lastClick[0], lastClick[1]);
            } else if(lastClick != null && lastClick[3] == Input.Buttons.RIGHT){
                contextMenu = null;
                gameState.transition("close");
            }
        }
        if (inMap.getStateByName("turnEnd").equals(gameState.getCurrent().getCurrent().getCurrent())){
            for (Unit unit : currentMap.getUnits()) {
                if (unit.getOwner() == currentMap.getPlayers().get(turnPlayer)) {
                    unit.endTurn();
                }
            }
            commands.clear();
            turnPlayer = (1 + turnPlayer) % currentMap.getPlayers().size();
            range = null;
            lastClick = null;
            if (path != null) {
                path.destroy();
            }
            path = null;
            turn += 1;
            gameState.transition("nextTurn");
        }
        if (inMap.getStateByName("turnStart").equals(gameState.getCurrent().getCurrent().getCurrent())){
            renderStartCard(batch);
            if(lastClick != null) {
                gameState.transition("start");
            }
        }
    }

    private void renderStartCard(SpriteBatch batch) {
        font.setColor(Color.WHITE);
        font.getData().setScale(10f);
        font.draw(batch, "Turn " + turn, 500, 600);
    }

    private void clearUnits() {
        List<Unit> toRemove = new LinkedList<>();
        for (Unit unit : currentMap.getUnits()){
            if (unit.getHealth() <= 0){
                //TODO destroy animation
                toRemove.add(unit);
            }
        }
        currentMap.getUnits().removeAll(toRemove);
        for (Unit unit : toRemove){
            unit.destroy();
        }
    }

    private void handleGameDefault(SpriteBatch batch, int[] lastClick, int[] mousePos) {
        unitPositions.clear();
        for (Unit unit : currentMap.getUnits()){
            unitPositions.put(new Point(unit.getPositionX(), unit.getPositionY()), unit);
        }
        Unit unit = unitPositions.get(clickedTile(lastClick));
        if (unit != null
                && lastClick[3] == Input.Buttons.LEFT && unit.getMovePoints() > 0) {
            range = new Range(currentMap, unitPositions, unit.getMovePoints(), unit.getPositionX(), unit.getPositionY(), true, false, false, unit.getOwner());
            path = new Path(currentMap, unitPositions, unit.getMovePoints(), range, currentMap.getPlayers().get(turnPlayer), new Point(unit));
            color = Color.GREEN;
            primarySelection = unit;
            if (currentMap.getPlayers().get(turnPlayer).getPlayerType() == Player.PlayerType.HUMAN) {
                gameState.transition("click");
            }
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
                                range = new Range(currentMap, unitPositions, unit, unit.getWeapon());
                                color = Color.BLUE;
                                selector = unit.getWeapon().target(currentMap.getPlayers().get(turnPlayer));
                                if (currentMap.getPlayers().get(turnPlayer).getPlayerType() == Player.PlayerType.HUMAN) {
                                    gameState.transition("target");
                                }
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
                            if (currentMap.getPlayers().get(turnPlayer).getPlayerType() == Player.PlayerType.HUMAN) {
                                if (!commands.empty()) {
                                    UndoableCommand lastCommand = commands.pop();
                                    lastCommand.undo();
                                }
                            }
                            break;
                        case 1:
                            if (currentMap.getPlayers().get(turnPlayer).getPlayerType() == Player.PlayerType.HUMAN) {
                                gameState.transition("endTurn");
                            }
                            break;
                        case 2:
                            gameState.transition("close");
                            break;
                    }
                }
            };
        }
    }

    private Point clickedTile(int[] lastClick){
        return new Point(lastClick[0] / 64, lastClick[1] / 64);
    }

    public void renderGame(SpriteBatch batch, Range range, Color color, Selector selector, int[] mousePos){
        int height = currentMap.getHeight();
        for(int y = 0; y < currentMap.getHeight(); y++){
            for(int x = 0; x < currentMap.getWidth(); x++){
                int distance = Integer.MAX_VALUE;
                if (range != null) {
                    distance = range.getDistance(x, height - y - 1);
                }
                if (distance < Integer.MAX_VALUE && distance >= 0) {
                    batch.setColor(color);
                }
                batch.draw(currentMap.getTexture(x, y), 64 * x, 64 * (height - y - 1), 64, 64);
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
        font.setColor(Color.RED);
        font.getData().setScale(1f);
        for (Unit unit : currentMap.getUnits()) {
            if (unit.isDraw()) {
                int distance = Integer.MAX_VALUE;
                batch.setColor(unit.getOwner().getPlayerColor());
                if (range != null) {
                    distance = range.getDistance(unit.getPositionX(), height - unit.getPositionY() - 1);
                }
                if (selector != null && distance < Integer.MAX_VALUE && distance >= 0 && selector.select(unit)) {
                    batch.setColor(color);
                }
                if (unit.hasAttacked()) {
                    batch.setColor(Color.GRAY);
                }
                batch.draw(unit.getTexture(), unit.getPositionX() * 64, unit.getPositionY() * 64, 64, 64);
                String life = unit.getHealth() + "/" + unit.getMaxHealthPoints();
                layout.setText(font, life);
                font.draw(batch, life, unit.getPositionX() * 64 + 64 - layout.width, unit.getPositionY() * 64 + layout.height);
                batch.setColor(Color.WHITE);
            }
        }

        if (range != null && path != null && mousePos != null) {
            Point mousePoint = new Point(mousePos[0] / 64, mousePos[1] / 64);
            path.addPoint(mousePoint);
            path.draw(batch);
        }
        //TODO add round Counter
    }

    public RNG getRng() {
        return rng;
    }

    public void destroy() {
        font.dispose();
        shapeRenderer.dispose();
        layout.reset();
        currentMap.destroyMap();

        undo.dispose();
        next.dispose();
        attack.dispose();
        close.dispose();

        arrows.dispose();
        continueRes.dispose();
        exit.dispose();
    }
}
