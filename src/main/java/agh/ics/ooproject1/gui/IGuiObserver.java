package agh.ics.ooproject1.gui;

import agh.ics.ooproject1.AbstractWorldMap;
import agh.ics.ooproject1.Grass;

import java.util.List;

public interface IGuiObserver {
    void newDay(AbstractWorldMap map, List<Grass> toPlace, List<Grass> toDeletion, boolean magic);
}
