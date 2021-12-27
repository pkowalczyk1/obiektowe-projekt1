package agh.ics.ooproject1;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Animal implements IWorldMapElement {
    //animal parameters
    private Vector2d position;
    private MapDirection orientation;
    private final Genome genome;
    private int energy;
    private final AbstractWorldMap map;
    private final List<IPositionChangeObserver> observers = new ArrayList<>();
    private final int epochBorn;

    //variables for statistics
    private int children = 0;
    private int descendants = 0;

    //variables for tracking
    public boolean isSelectedDescendant = false;
    private int epochDied = -1;
    private int trackedChildren = 0;

    public Animal(Vector2d position, AbstractWorldMap map, Genome genome, int energy, int epoch) {
        this.position = position;
        int orientationIndex = (int) (Math.random() * 8);
        orientation = switch (orientationIndex) {
            case 0 -> MapDirection.NORTH;
            case 1 -> MapDirection.NORTHEAST;
            case 2 -> MapDirection.EAST;
            case 3 -> MapDirection.SOUTHEAST;
            case 4 -> MapDirection.SOUTH;
            case 5 -> MapDirection.SOUTHWEST;
            case 6 -> MapDirection.WEST;
            case 7 -> MapDirection.NORTHWEST;
            default -> throw new IllegalStateException("Unexpected value: " + orientationIndex);
        };
        this.genome = genome;
        this.energy = energy;
        this.map = map;
        epochBorn = epoch;
    }

    public void move(int moveIndex) {
        switch (moveIndex) {
            case 0 -> {
                Vector2d oldPosition = position;
                position = position.add(orientation.toVector());
                if (map.canMoveTo(position)) {
                    int x = position.x;
                    int y = position.y;
                    //change position in unbounded map
                    if (position.x < 0) {
                        x += map.getWidth();
                    } else if (position.x >= map.getWidth()) {
                        x -= map.getWidth();
                    }
                    if (position.y < 0) {
                        y += map.getHeight();
                    } else if (position.y >= map.getHeight()) {
                        y -= map.getHeight();
                    }
                    position = new Vector2d(x, y);
                }
                else {
                    position = oldPosition;
                }
                decreaseEnergy(map.moveEnergy);
                positionChanged(oldPosition, position);
            }
            case 1 -> {
                orientation = orientation.next();
                decreaseEnergy(map.moveEnergy);
                map.energyChanged(this);
            }
            case 2 -> {
                orientation = orientation.next().next();
                decreaseEnergy(map.moveEnergy);
                map.energyChanged(this);
            }
            case 3 -> {
                orientation = orientation.next().next().next();
                decreaseEnergy(map.moveEnergy);
                map.energyChanged(this);
            }
            case 4 -> {
                Vector2d oldPosition = position;
                position = position.subtract(orientation.toVector());
                if (map.canMoveTo(position)) {
                    int x = position.x;
                    int y = position.y;
                    //change position in unbounded map
                    if (position.x < 0) {
                        x += map.getWidth();
                    } else if (position.x >= map.getWidth()) {
                        x -= map.getWidth();
                    }
                    if (position.y < 0) {
                        y += map.getHeight();
                    } else if (position.y >= map.getHeight()) {
                        y -= map.getHeight();
                    }
                    position = new Vector2d(x, y);
                }
                else {
                    position = oldPosition;
                }
                decreaseEnergy(map.moveEnergy);
                positionChanged(oldPosition, position);
            }
            case 5 -> {
                orientation = orientation.previous().previous().previous();
                decreaseEnergy(map.moveEnergy);
                map.energyChanged(this);
            }
            case 6 -> {
                orientation = orientation.previous().previous();
                decreaseEnergy(map.moveEnergy);
                map.energyChanged(this);
            }
            case 7 -> {
                orientation = orientation.previous();
                decreaseEnergy(map.moveEnergy);
                map.energyChanged(this);
            }
        }
    }

    //getters
    public Vector2d getPosition() {
        return position;
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    public int getEnergy() {
        return energy;
    }

    public Genome getGenome() {
        return genome;
    }

    public int getEpochBorn() {
        return epochBorn;
    }

    public int getEpochDied() {
        return epochDied;
    }

    public int getChildren() {
        return children;
    }

    public int getDescendants() {
        return descendants;
    }

    public int getTrackedChildren() {
        return trackedChildren;
    }

    //other methods
    public void decreaseEnergy(int change) {
        energy -= change;
    }

    public void increaseEnergy(int change) {
        energy += change;
    }

    public void addObserver(IPositionChangeObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer) {
        observers.remove(observer);
    }

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition) {
        for (IPositionChangeObserver observer : observers) {
            observer.positionChanged(oldPosition, newPosition, this);
        }
    }

    public Rectangle getImage() {
        double multiplier = Math.min((double)energy / (double)map.startEnergy, 1);
        Rectangle shape = new Rectangle();
        if (energy > 0) {
            shape.setFill(Color.rgb((int) (255 * (1 - multiplier)), (int) (255 * multiplier), 0));
        }
        else {
            shape.setFill(Color.BLACK);
        }

        return shape;
    }

    public void incrementChildren() {
        children++;
    }

    public void setEpochDied(int epoch) {
        epochDied = epoch;
    }

    public void incrementDescendants() {
        descendants++;
    }

    public void incrementTrackedChildren() {
        trackedChildren++;
    }

    public void resetDescendants() {
        descendants = 0;
    }

    public void resetTrackedChildren() {
        trackedChildren = 0;
    }

    @Override
    public String toString() {
        return position.toString();
    }
}
