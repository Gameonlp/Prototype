package com.tilebased.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tilebased.game.player.Player;
import com.tilebased.game.resource.ResourceManager;
import com.tilebased.game.resource.TextureResource;
import com.tilebased.game.units.Unit;
import com.tilebased.game.util.Point;
import com.tilebased.game.util.PointDistanceTuple;
import com.tilebased.game.util.Range;

import java.util.*;

public class Path {
    private final GameMap map;
    private final Map<Point, Unit> unitPositions;
    private final Point[] path;
    private Range range;
    private Player owner;
    private final TextureResource arrowsResource;

    public Path(GameMap map, Map<Point, Unit> unitPositions, int length, Range range, Player owner, Point startPoint) {
        this(map, unitPositions, length, range, owner, startPoint, null);
    }

    public Path(GameMap map, Map<Point, Unit> unitPositions, int length, Range range, Player owner, Point startPoint, Point endPoint){
        this.map = map;
        this.unitPositions = unitPositions;
        this.path = new Point[length + 1];
        path[0] = startPoint;
        this.range = range;
        this.owner = owner;
        this.arrowsResource = ResourceManager.getInstance().getTexture("textures/Arrows.png");
        if (endPoint != null){
            calculatePath(endPoint);
        }
    }

    public int getConcreteLength(){
        int i = 0;
        while (i < path.length) {
            if (path[i] == null)
                break;
            i++;
        }
        return i;
    }

    public void addPoint(Point mousePoint){
        if (mousePoint == null){
            return;
        }
        int distance = range.getDistance(mousePoint);
        for (int i = 0; path != null && i <= path.length; i++) {
            if (!mousePoint.equals(path[0]) && !(distance >= 0 && (distance < Integer.MAX_VALUE || unitPositions.containsKey(mousePoint) && unitPositions.get(mousePoint).getOwner().equals(owner)))) {
                break;
            } else if (i == path.length) {
                Arrays.fill(path, 1, path.length, null);
                calculatePath(mousePoint);
                break;
            } else if (path[i] == null && Math.abs(path[i - 1].x - mousePoint.x) + Math.abs(path[i - 1].y - mousePoint.y) == 1) {
                path[i] = mousePoint;
                break;
            } else if (path[i] == null) {
                Arrays.fill(path, 1, path.length, null);
                calculatePath(mousePoint);
                break;
            } else if (path[i].equals(mousePoint)) {
                for (int j = i + 1; j < path.length; j++){
                    path[j] = null;
                }
                break;
            }
        }
    }

    private void calculatePath(Point point){
        PriorityQueue<List<PointDistanceTuple>> toCheck = new PriorityQueue<>((o1, o2) -> {
            int dist1 = o1.get(0).getDistance() + Math.abs(o1.get(0).getPoint().x - path[0].x) + Math.abs(o1.get(0).getPoint().y - path[0].y);
            int dist2 = o2.get(0).getDistance() + Math.abs(o2.get(0).getPoint().x - path[0].x) + Math.abs(o2.get(0).getPoint().y - path[0].y);
            return dist1 - dist2;
        });
        List<PointDistanceTuple> current = new LinkedList<>();
        current.add(new PointDistanceTuple(point, 0));
        toCheck.offer(current);
        PointDistanceTuple currentPoint;
        while (!toCheck.isEmpty()){
            current = toCheck.poll();
            currentPoint = current.get(0);
            if (currentPoint.getPoint().equals(path[0])){
                for (int i = 0; i < current.size(); i++) {
                    path[i] = current.get(i).getPoint();
                }
                return;
            }
            if (currentPoint.getDistance() < path.length - 1 && (range.getDistance(currentPoint.getPoint()) != Integer.MAX_VALUE
                    || (unitPositions.get(currentPoint.getPoint()) != null
                    && unitPositions.get(currentPoint.getPoint()).getOwner().equals(owner)))){
                Point y_less_point = new Point(currentPoint.getPoint().x, currentPoint.getPoint().y - 1);
                checkAndAdd(y_less_point, currentPoint, current, toCheck);
                Point y_more_point = new Point(currentPoint.getPoint().x, currentPoint.getPoint().y + 1);
                checkAndAdd(y_more_point, currentPoint, current, toCheck);
                Point x_less_point = new Point(currentPoint.getPoint().x - 1, currentPoint.getPoint().y);
                checkAndAdd(x_less_point, currentPoint, current, toCheck);
                Point x_more_point = new Point(currentPoint.getPoint().x + 1, currentPoint.getPoint().y);
                checkAndAdd(x_more_point, currentPoint, current, toCheck);
            }
        }
    }

    private void checkAndAdd(Point point, PointDistanceTuple currentPoint, List<PointDistanceTuple> current, PriorityQueue<List<PointDistanceTuple>> toCheck) {
        PointDistanceTuple pointDistanceTuple = new PointDistanceTuple(point, currentPoint.getDistance() + 1);
        if (!current.contains(pointDistanceTuple)){
            List<PointDistanceTuple> toAdd = new LinkedList<>(current);
            toAdd.add(0, pointDistanceTuple);
            toCheck.offer(toAdd);
        }
    }

    public void draw(SpriteBatch batch) {
        Texture arrows = arrowsResource.getResource();
        for (int i = 1; i < path.length && path[i] != null; i++) {
            if (path[i].belowOf(path[i - 1])){
                if (i + 1 < path.length && path[i + 1] != null){
                   if (path[i].aboveOf(path[i + 1])){
                       batch.draw(arrows, path[i].x * 64, path[i].y * 64, 0, 0, 64, 64);
                   } else if (path[i].rightOf(path[i + 1])){
                       batch.draw(arrows, path[i].x * 64, path[i].y * 64, 3 * 64, 0, 64, 64);
                   } else if (path[i].leftOf(path[i + 1])){
                       batch.draw(arrows, path[i].x * 64, path[i].y * 64, 4 * 64, 0, 64, 64);
                   }
                } else {
                    batch.draw(arrows, path[i].x * 64, path[i].y * 64, 8 * 64, 0, 64, 64);
                }
            } else if (path[i].aboveOf(path[i - 1])){
                if (i + 1 < path.length && path[i + 1] != null){
                    if (path[i].belowOf(path[i + 1])){
                        batch.draw(arrows, path[i].x * 64, path[i].y * 64, 0, 0, 64, 64);
                    } else if (path[i].rightOf(path[i + 1])){
                        batch.draw(arrows, path[i].x * 64, path[i].y * 64, 2 * 64, 0, 64, 64);
                    } else if (path[i].leftOf(path[i + 1])){
                        batch.draw(arrows, path[i].x * 64, path[i].y * 64, 5 * 64, 0, 64, 64);
                    }
                } else {
                    batch.draw(arrows, path[i].x * 64, path[i].y * 64, 6 * 64, 0, 64, 64);
                }
            } else if (path[i].leftOf(path[i - 1])) {
                if (i + 1 < path.length && path[i + 1] != null){
                    if (path[i].rightOf(path[i + 1])){
                        batch.draw(arrows, path[i].x * 64, path[i].y * 64, 64, 0, 64, 64);
                    } else if (path[i].belowOf(path[i + 1])){
                        batch.draw(arrows, path[i].x * 64, path[i].y * 64, 4 * 64, 0, 64, 64);
                    } else if (path[i].aboveOf(path[i + 1])){
                        batch.draw(arrows, path[i].x * 64, path[i].y * 64, 5 * 64, 0, 64, 64);
                    }
                } else {
                    batch.draw(arrows, path[i].x * 64, path[i].y * 64, 9 * 64, 0, 64, 64);
                }
            } else if (path[i].rightOf(path[i - 1])) {
                if (i + 1 < path.length && path[i + 1] != null){
                    if (path[i].leftOf(path[i + 1])){
                        batch.draw(arrows, path[i].x * 64, path[i].y * 64, 64, 0, 64, 64);
                    } else if (path[i].aboveOf(path[i + 1])){
                        batch.draw(arrows, path[i].x * 64, path[i].y * 64, 2 * 64, 0, 64, 64);
                    } else if (path[i].belowOf(path[i + 1])){
                        batch.draw(arrows, path[i].x * 64, path[i].y * 64, 3 * 64, 0, 64, 64);
                    }
                } else {
                    batch.draw(arrows, path[i].x * 64, path[i].y * 64, 7 * 64, 0, 64, 64);
                }
            }
        }
    }

    public void destroy(){
        arrowsResource.dispose();
    }

    public Point get(int i) {
        return path[i];
    }
}
