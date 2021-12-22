package agh.ics.ooproject1;

import agh.ics.ooproject1.gui.IGuiObserver;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SimulationEngine implements Runnable {
    private AbstractWorldMap map;
    private IGuiObserver gui;
    public boolean flag = true;

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
                    map.place(new Animal(newPosition, map, new Genome(), map.startEnergy, map.epoch));
                    placed++;
                }
            }
        }
        this.gui = gui;
    }

    @Override
    public void run() {
        while (true) {
            map.removeDeadAnimals();
            map.moveAllAnimals();
            map.eat();
            map.spawn();
            map.growGrass();
            map.epoch++;
            map.energyAvg = map.getAnimalsList().stream().mapToDouble(Animal::getEnergy).sum() / map.animalCount;
            int maxCount = -1;
            List<Integer> maxGenome = null;
            for (Map.Entry<List<Integer>, Integer> entry : map.genomesCount.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    maxGenome = entry.getKey();
                }
            }
            map.mostCommonGenome = maxGenome;
            gui.newDay(map);
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
