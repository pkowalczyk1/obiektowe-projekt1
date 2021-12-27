package agh.ics.ooproject1;

import agh.ics.ooproject1.gui.App;
import javafx.application.Application;

public class World {
    public static void main(String[] args) {
        //run simulation and catch argument exceptions
        try {
            Application.launch(App.class, args);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex);
        }
    }
}
