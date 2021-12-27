package agh.ics.ooproject1;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnimalSpawnTest {
    @Test
    public void spawnTest() {
        AbstractWorldMap map = new UnboundedMap(10, 10, 0.5, 10, 10, 100, false);
        List<Integer> genome1 = Arrays.asList(0, 0, 0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 5, 6, 6, 6, 6, 7, 7, 7, 7, 7);
        List<Integer> genome2 = Arrays.asList(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7);
        Animal animal1 = new Animal(new Vector2d(1, 1), map, new Genome(genome1), 140, 0);
        Animal animal2 = new Animal(new Vector2d(1, 1), map, new Genome(genome2), 60, 0);
        map.placeAnimal(animal1);
        map.placeAnimal(animal2);
        Animal newAnimal = map.spawnNewAnimal(animal1, animal2);
        List<Integer> expectedGenes1 = Arrays.asList(0, 0, 0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 7, 7, 7);
        List<Integer> expectedGenes2 = Arrays.asList(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 5, 6, 6, 6, 6, 7, 7, 7, 7, 7);
        Genome expectedGenome1 = new Genome(expectedGenes1);
        Genome expectedGenome2 = new Genome(expectedGenes2);
        assertTrue(newAnimal.getGenome().equals(expectedGenome1) || newAnimal.getGenome().equals(expectedGenome2));
        assertTrue(newAnimal.getGenome().getGenes().equals(expectedGenes1) || newAnimal.getGenome().getGenes().equals(expectedGenes2));
        assertEquals(50, newAnimal.getEnergy());
        assertEquals(105, animal1.getEnergy());
        assertEquals(45, animal2.getEnergy());
    }
}
