package agh.ics.ooproject1;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class Animal implements IWorldMapElement {
    private Vector2d position;
    private MapDirection orientation;
    private Genome genome;
    private int energy;
    private AbstractWorldMap map;
    private List<IPositionChangeObserver> observers = new ArrayList<>();

    public Animal(Vector2d position, AbstractWorldMap map, Genome genome, int energy) {
        this.position = position;
        int orientationIndex = (int) (Math.random() * 8);
        this.orientation = switch (orientationIndex) {
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
    }

    public void move() {
        int moveIndex = this.genome.randomMove();
        switch (moveIndex) {
            case 0 -> {
                Vector2d oldPosition = this.position;
                this.position = this.position.add(this.orientation.toVector());
                int x = this.position.x;
                int y = this.position.y;
                if (this.position.x < 0) {
                    x += this.map.getWidth();
                }
                else if (this.position.x >= this.map.getWidth()) {
                    x -= this.map.getWidth();
                }
                if (this.position.y < 0) {
                    y += this.map.getHeight();
                }
                else if (this.position.y >= this.map.getHeight()) {
                    y -= this.map.getHeight();
                }
                this.position = new Vector2d(x, y);
                decreaseEnergy(this.map.moveEnergy);
                positionChanged(oldPosition, this.position);
            }
            case 1 -> {
                this.orientation = this.orientation.next();
                decreaseEnergy(this.map.moveEnergy);
            }
            case 2 -> {
                this.orientation = this.orientation.next().next();
                decreaseEnergy(this.map.moveEnergy);
            }
            case 3 -> {
                this.orientation = this.orientation.next().next().next();
                decreaseEnergy(this.map.moveEnergy);
            }
            case 4 -> {
                Vector2d oldPosition = this.position;
                this.position = this.position.subtract(this.orientation.toVector());
                int x = this.position.x;
                int y = this.position.y;
                if (this.position.x < 0) {
                    x += this.map.getWidth();
                }
                else if (this.position.x >= this.map.getWidth()) {
                    x -= this.map.getWidth();
                }
                if (this.position.y < 0) {
                    y += this.map.getHeight();
                }
                else if (this.position.y >= this.map.getHeight()) {
                    y -= this.map.getHeight();
                }
                this.position = new Vector2d(x, y);
                decreaseEnergy(this.map.moveEnergy);
                positionChanged(oldPosition, this.position);
            }
            case 5 -> {
                this.orientation = this.orientation.previous().previous().previous();
                decreaseEnergy(this.map.moveEnergy);
            }
            case 6 -> {
                this.orientation = this.orientation.previous().previous();
                decreaseEnergy(this.map.moveEnergy);
            }
            case 7 -> {
                this.orientation = this.orientation.previous();
                decreaseEnergy(this.map.moveEnergy);
            }
        }
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public int getEnergy() {
        return this.energy;
    }

    public List<Integer> getGenome() {
        return this.genome.getGenes();
    }

    public void decreaseEnergy(int change) {
        this.energy -= change;
    }

    public void increaseEnergy(int change) {
        this.energy += change;
    }

    public void addObserver(IPositionChangeObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer) {
        this.observers.remove(observer);
    }

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition) {
        for (IPositionChangeObserver observer : this.observers) {
            observer.positionChanged(oldPosition, newPosition, this);
        }
    }

    public Circle getImage() {
        double multiplier = Math.min((double)this.energy / (double)this.map.startEnergy, 1);
        System.out.println(multiplier);
        Circle shape = new Circle();
        if (this.energy > 0) {
            shape.setFill(Color.rgb((int) (255 * (1 - multiplier)), (int) (255 * multiplier), 0));
        }
        else {
            shape.setFill(Color.BLACK);
        }

        return shape;
    }
}
