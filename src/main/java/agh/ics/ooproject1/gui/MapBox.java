package agh.ics.ooproject1.gui;

import agh.ics.ooproject1.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.util.*;

public class MapBox {
    private final AbstractWorldMap map;
    private final SimulationEngine engine;
    private final Thread thread;
    private final int mapWidth = 420;
    private final VBox wrapper;
    private final Charts charts;
    private final HBox mapAndCharts;
    private final VBox statistics;
    private final VBox chartBox;
    private final VBox mapAndButton;
    private final Button startStop;
    private final GridPane grid;
    private final Label mostCommonGenome;
    private final VBox oneAnimalStatistics;
    private final Label magicWarning = new Label();
    private final Map<Vector2d, Pane> placedGrass = new LinkedHashMap<>();
    private final List<Pane> placedAnimals = new ArrayList<>();

    public MapBox(SimulationEngine engine, AbstractWorldMap map, Thread thread) {
        this.engine = engine;
        this.map = map;
        this.thread = thread;
        wrapper = new VBox(15);
        charts = new Charts();
        mapAndCharts = new HBox(15);
        statistics = new VBox(5);
        chartBox = new VBox(10);
        mapAndButton = new VBox(10);
        startStop = new Button("Start/stop");
        grid = new GridPane();
        oneAnimalStatistics = new VBox(10);
        mostCommonGenome = new Label("Most common genome: " + this.map.mostCommonGenome);
        statistics.getChildren().add(mostCommonGenome);
        makeGrid(new ArrayList<>(), new ArrayList<>());
        mapAndButton.getChildren().addAll(grid, startStop, oneAnimalStatistics);
        chartBox.getChildren().addAll(charts.getAllCharts());
        mapAndCharts.getChildren().addAll(chartBox, mapAndButton);
        wrapper.getChildren().addAll(mapAndCharts, statistics);
        startStop.setOnAction((event) -> {
            this.engine.flag = !this.engine.flag;
            if (this.engine.flag) {
                this.thread.resume();
            }
            else {
                this.thread.suspend();
            }
        });
    }

    public VBox getWrapper() {
        return wrapper;
    }

    public void refresh(List<Grass> toPlace, List<Grass> toDeletion) {
        makeGrid(toPlace, toDeletion);
        charts.addDataToCharts(map.animalCount, map.grassCount, map.energyAvg, map.lifeSpanAvg, map.childrenAvg);
    }

    public void makeGrid(List<Grass> newGrass, List<Grass> toDeletion) {
        mostCommonGenome.setText("Most common genome: " + map.mostCommonGenome);
        grid.getChildren().removeAll(placedAnimals);
        placedAnimals.clear();
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
        double cellSize = Math.min(mapWidth/map.getWidth(), mapWidth/map.getHeight());
        grid.setPrefSize(map.getWidth()*cellSize, map.getHeight()*cellSize);

        for (int i=0; i<map.getWidth(); i++) {
            grid.getColumnConstraints().add(new ColumnConstraints(cellSize));
        }

        for (int i=0; i<map.getHeight(); i++) {
            grid.getRowConstraints().add(new RowConstraints(cellSize));
        }


        for (Grass grass : toDeletion) {
            Pane toRemove = placedGrass.get(grass.getPosition());
            grid.getChildren().remove(toRemove);
            placedGrass.remove(grass.getPosition());
        }

        grid.setStyle("-fx-background-color: rgb(255, 153, 0)");

        for (Grass grass : newGrass) {
            Pane pane = new StackPane();
            pane.setStyle("-fx-background-color: darkgreen");
            pane.setPrefSize(cellSize, cellSize);
            grid.add(pane, grass.getPosition().x, map.getHeight() - grass.getPosition().y - 1);
            placedGrass.put(grass.getPosition(), pane);
        }

        List<Animal> toPlace = map.getToPlace();
        for (Animal animal : toPlace) {
            Pane pane = new StackPane();
            pane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                showStatistics(animal);
            });
            pane.setPrefSize(cellSize, cellSize);
            Rectangle shape = animal.getImage();
            shape.setWidth(cellSize);
            shape.setHeight(cellSize);
            pane.getChildren().add(shape);
            grid.add(pane, animal.getPosition().x, map.getHeight() - animal.getPosition().y - 1);
            placedAnimals.add(pane);
        }
    }

    public void showStatistics(Animal animal) {
        oneAnimalStatistics.getChildren().clear();
        Label genome = new Label("Chosen animal genome: " + animal.getGenome());
        oneAnimalStatistics.getChildren().add(genome);
    }
}
