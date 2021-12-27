package agh.ics.ooproject1;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovementTest {
    private final UnboundedMap map = new UnboundedMap(20, 20, 0.5, 50, 2, 100, false);
    private final List<Integer> genes = Arrays.asList(0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 4, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 7);
    private Animal animal = new Animal(new Vector2d(2, 2), map, new Genome(genes), 20, 0);

    @Test
    public void RotationTest() {
        MapDirection initial = animal.getOrientation();
        animal.move(1);
        initial = initial.next();
        assertEquals(initial, animal.getOrientation());
        assertEquals(18, animal.getEnergy());
        animal.move(2);
        initial = initial.next().next();
        assertEquals(initial, animal.getOrientation());
        animal.move(3);
        initial = initial.next().next().next();
        assertEquals(initial, animal.getOrientation());
        animal.move(5);
        initial = initial.next().next().next().next().next();
        assertEquals(initial, animal.getOrientation());
        animal.move(6);
        initial = initial.next().next().next().next().next().next();
        assertEquals(initial, animal.getOrientation());
        animal.move(7);
        initial = initial.next().next().next().next().next().next().next();
        assertEquals(initial, animal.getOrientation());
    }

    @Test
    public void WalkingTest() {
        animal.move(0);
        Vector2d expected = switch(animal.getOrientation()) {
            case NORTH -> new Vector2d(2, 3);
            case NORTHEAST -> new Vector2d(3, 3);
            case EAST -> new Vector2d(3, 2);
            case SOUTHEAST -> new Vector2d(3, 1);
            case SOUTH -> new Vector2d(2, 1);
            case SOUTHWEST -> new Vector2d(1, 1);
            case WEST -> new Vector2d(1, 2);
            case NORTHWEST -> new Vector2d(1, 3);
        };
        assertEquals(expected, animal.getPosition());
        assertEquals(18 ,animal.getEnergy());

        animal = new Animal(new Vector2d(2, 2), map, new Genome(genes), 20, 0);
        animal.move(4);
        expected = switch(animal.getOrientation()) {
            case NORTH -> new Vector2d(2, 1);
            case NORTHEAST -> new Vector2d(1, 1);
            case EAST -> new Vector2d(1, 2);
            case SOUTHEAST -> new Vector2d(1, 3);
            case SOUTH -> new Vector2d(2, 3);
            case SOUTHWEST -> new Vector2d(3, 3);
            case WEST -> new Vector2d(3, 2);
            case NORTHWEST -> new Vector2d(3, 1);
        };
        assertEquals(expected, animal.getPosition());
        assertEquals(18, animal.getEnergy());
    }
}
