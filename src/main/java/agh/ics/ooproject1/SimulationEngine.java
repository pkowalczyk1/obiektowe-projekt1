package agh.ics.ooproject1;

import agh.ics.ooproject1.gui.IGuiObserver;

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
            map.removeDeadAnimals();
            map.moveAllAnimals();
            map.eat();
            map.spawn();
            map.growGrass();
            gui.newDay(map);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
