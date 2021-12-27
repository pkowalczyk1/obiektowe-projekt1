package agh.ics.ooproject1;

import agh.ics.ooproject1.gui.IGuiObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimulationEngine implements Runnable {
    private final AbstractWorldMap map;
    private final IGuiObserver gui;
    public boolean isGoing = true;
    public boolean flag = true;
    public int magicCount = 0;
    private boolean spawnedMagic = false;

    public SimulationEngine(AbstractWorldMap map, int animalCount, IGuiObserver gui) {
        this.map = map;
        int placed = 0;
        while (placed != animalCount) {
            boolean check = false;
            while (!check) {
                int x = (int) (Math.random() * map.getWidth());
                int y = (int) (Math.random() * map.getHeight());
                Vector2d newPosition = new Vector2d(x, y);
                if (map.animalsAt(newPosition) == null) {
                    check = true;
                    map.placeAnimal(new Animal(newPosition, map, new Genome(), map.startEnergy, map.epoch));
                    placed++;
                }
            }
        }
        this.gui = gui;
    }

    @Override
    public void run() {
        while (isGoing) {
            if (flag) {
                spawnedMagic = false;
                //simulation methods
                map.removeDeadAnimals();
                map.moveAllAnimals();
                List<Grass> toDeletion = map.eat();
                map.spawn();
                List<Grass> toPlace = map.growGrass();

                //update statistics
                if (map.animalCount == 0) {
                    map.energyAvg = 0;
                    map.childrenAvg = 0;
                }
                else {
                    map.energyAvg = map.getAnimalsList().stream()
                            .filter(o -> o.getEnergy() > 0)
                            .mapToDouble(Animal::getEnergy).sum() / map.animalCount;
                    map.childrenAvg = map.getAnimalsList().stream()
                            .filter(o -> o.getEnergy() > 0)
                            .mapToDouble(Animal::getChildren).sum() / map.animalCount;
                }

                int maxCount = -1;
                Genome maxGenome = null;
                for (Map.Entry<Genome, Integer> entry : map.genomesCount.entrySet()) {
                    if (entry.getValue() > maxCount) {
                        maxCount = entry.getValue();
                        maxGenome = entry.getKey();
                    }
                }
                map.mostCommonGenome = maxGenome;

                //check if 5 new animals should be spawned in magic strategy
                if (map.animalCount == 5 && map.isMagic && magicCount < 3) {
                    place5New();
                    magicCount++;
                    spawnedMagic = true;
                }

                map.updateStatisticsHist();
                map.epoch++;

                //warn gui about new day
                gui.newDay(map, toPlace, toDeletion, spawnedMagic);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void place5New() {
        List<Animal> toPlace = new ArrayList<>();
        for (Animal animal : map.getAnimalsList()) {
            boolean placed = false;
            while (!placed) {
                int x = (int) (Math.random() * map.getWidth());
                int y = (int) (Math.random() * map.getHeight());
                if (map.isOccupied(new Vector2d(x, y))) {
                    placed = true;
                    toPlace.add(new Animal(new Vector2d(x, y), map, animal.getGenome(), map.startEnergy, map.epoch));
                }
            }
        }

        for (Animal animal : toPlace) {
            map.placeAnimal(animal);
        }
    }
}
