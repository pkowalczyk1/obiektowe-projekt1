package agh.ics.ooproject1;

import agh.ics.ooproject1.gui.App;
import javafx.application.Application;

public class World {
    public static void main(String[] args) {
//        Animal animal = new Animal(new Vector2d(5, 4), new UnboundedMap(10, 10, 0.1, 40, 20), new int[] {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 6, 7, 7, 7, 7}, 10);
//        System.out.println(animal.getOrientation());
//        System.out.println(animal.getPosition());
//        animal.move();
//        System.out.println(animal.getPosition());
//        System.out.println(animal.getOrientation());
        try {
            Application.launch(App.class, args);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex);
        }
    }
}
