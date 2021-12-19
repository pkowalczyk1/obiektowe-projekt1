package agh.ics.ooproject1;

import agh.ics.ooproject1.gui.IGuiObserver;

public class SimulationEngine implements Runnable {
    private AbstractWorldMap map;
    private IGuiObserver gui;
    private int[] genome = {0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7};

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
                    map.place(new Animal(newPosition, map, new Genome(), map.startEnergy));
                    placed++;
                }
            }
        }
        this.gui = gui;
    }

    @Override
    public void run() {
        while (true) {
            this.map.removeDeadAnimals();
            this.map.moveAllAnimals();
            this.map.eat();
            this.map.spawn();
            this.map.growGrass();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.gui.newDay();
        }
    }

    private void placeGrass() {

    }
}
