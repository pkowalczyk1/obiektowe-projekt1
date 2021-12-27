package agh.ics.ooproject1.gui;

import agh.ics.ooproject1.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.util.*;
import java.util.List;

import static javafx.scene.paint.Color.rgb;

//class that contains boxes with map and all statistics
public class MapBox {
    private final AbstractWorldMap map;
    private final SimulationEngine engine;

    //box variables and buttons
    private final int mapWidth = 410;
    private final VBox wrapper;
    private final Charts charts;
    private final HBox mapAndCharts;
    private final VBox statistics;
    private final VBox chartBox;
    private final HBox buttons;
    private final VBox mapAndButton;
    private final Button startStop;
    private final Button saveLogs;
    private final Button showMostCommonGenome;
    private final GridPane grid;
    private final Label mostCommonGenome;
    private final VBox oneAnimalClicked;
    private final VBox oneAnimalStatistics;

    //structures to keep animals and grass placed on map in gui
    private final Map<Vector2d, Pane> placedGrass = new LinkedHashMap<>();
    private final List<Pane> placedAnimals = new ArrayList<>();
    private final Map<Vector2d, Rectangle> shapes = new LinkedHashMap<>();

    public MapBox(SimulationEngine engine, AbstractWorldMap map) {
        this.engine = engine;
        this.map = map;

        //initialize all boxes and buttons
        wrapper = new VBox(15);
        charts = new Charts();
        mapAndCharts = new HBox(15);
        statistics = new VBox(5);
        chartBox = new VBox(10);
        buttons = new HBox(10);
        mapAndButton = new VBox(10);
        startStop = new Button("Start/stop");
        saveLogs = new Button("Save statistics");
        showMostCommonGenome = new Button("Highlight most common genome");
        grid = new GridPane();
        oneAnimalClicked = new VBox(10);
        oneAnimalStatistics = new VBox(10);
        mostCommonGenome = new Label("Most common genome: " + this.map.mostCommonGenome);
        statistics.getChildren().add(mostCommonGenome);
        makeGrid(new ArrayList<>(), new ArrayList<>());
        buttons.getChildren().addAll(startStop);
        mapAndButton.getChildren().addAll(grid, buttons, oneAnimalClicked, oneAnimalStatistics);
        chartBox.getChildren().addAll(charts.getAllCharts());
        mapAndCharts.getChildren().addAll(chartBox, mapAndButton);
        wrapper.getChildren().addAll(mapAndCharts, statistics);

        startStop.setOnAction((event) -> {
            oneAnimalClicked.getChildren().clear();
            this.engine.flag = !this.engine.flag;
            if (this.engine.flag) {
                buttons.getChildren().removeAll(showMostCommonGenome, saveLogs);
            }
            else {
                buttons.getChildren().addAll(saveLogs, showMostCommonGenome);
            }
        });

        saveLogs.setOnAction((event) -> {
            if (map instanceof UnboundedMap) {
                map.saveLogs("src\\main\\resources\\unbounded_map_statistics");
            }
            else {
                map.saveLogs("src\\main\\resources\\bounded_map_statistics");
            }
        });

        showMostCommonGenome.setOnAction((event) -> highlightAnimals());
    }

    //getter for entire map box
    public VBox getWrapper() {
        return wrapper;
    }

    public void refresh(List<Grass> toPlace, List<Grass> toDeletion) {
        makeGrid(toPlace, toDeletion);
        charts.addDataToCharts(map.animalCount, map.grassCount, map.energyAvg, map.lifeSpanAvg, map.childrenAvg);
    }

    //method to update grid
    public void makeGrid(List<Grass> newGrass, List<Grass> toDeletion) {
        mostCommonGenome.setText("Most common genome: " + map.mostCommonGenome);
        grid.getChildren().removeAll(placedAnimals);   //remove all placed animals
        placedAnimals.clear();
        shapes.clear();
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
        double cellSize = Math.min(mapWidth/map.getWidth(), mapWidth/map.getHeight());
        grid.setPrefSize(map.getWidth()*cellSize, map.getHeight()*cellSize);

        //add constraints to grid
        for (int i=0; i<map.getWidth(); i++) {
            grid.getColumnConstraints().add(new ColumnConstraints(cellSize));
        }

        for (int i=0; i<map.getHeight(); i++) {
            grid.getRowConstraints().add(new RowConstraints(cellSize));
        }

        //delete grass that was eaten previous day
        for (Grass grass : toDeletion) {
            Pane toRemove = placedGrass.get(grass.getPosition());
            grid.getChildren().remove(toRemove);
            placedGrass.remove(grass.getPosition());
        }

        grid.setStyle("-fx-background-color: rgb(255, 153, 0)");

        //place new grass
        for (Grass grass : newGrass) {
            Pane pane = new StackPane();
            pane.setStyle("-fx-background-color: darkgreen");
            pane.setPrefSize(cellSize, cellSize);
            grid.add(pane, grass.getPosition().x, map.getHeight() - grass.getPosition().y - 1);
            placedGrass.put(grass.getPosition(), pane);
        }

        //place animals
        List<Animal> toPlace = map.getToPlace();
        for (Animal animal : toPlace) {
            Pane pane = new StackPane();
            //click event on animal
            pane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                showGenome(animal);
                oneAnimalStatistics.getChildren().clear();
                map.selectedAnimal = null;
            });
            pane.setPrefSize(cellSize, cellSize);
            Rectangle shape = animal.getImage();
            shape.setWidth(cellSize);
            shape.setHeight(cellSize);
            pane.getChildren().add(shape);
            shapes.put(animal.getPosition(), shape);
            grid.add(pane, animal.getPosition().x, map.getHeight() - animal.getPosition().y - 1);
            placedAnimals.add(pane);
        }
    }

    public void showGenome(Animal animal) {
        oneAnimalClicked.getChildren().clear();
        Label genome = new Label("Chosen animal genome: " + animal.getGenome());
        //button to toggle animal tracking
        Button track = new Button("Toggle track");
        track.setOnAction((event) -> {
            if (map.selectedAnimal == null) {
                showStatistics(animal);
                map.selectedAnimal = animal;
                map.trackStartTime = map.epoch;
            }
            else {
                map.selectedAnimal.resetDescendants();
                map.selectedAnimal.resetTrackedChildren();
                map.selectedAnimal = null;
                map.trackStartTime = -1;
                oneAnimalStatistics.getChildren().clear();
            }
        });
        oneAnimalClicked.getChildren().addAll(genome, track);
    }

    //method to show statistics of tracked animal
    public void showStatistics(Animal animal) {
        oneAnimalStatistics.getChildren().clear();
        Label childrenCount = new Label("Tracked animal children count: " + animal.getTrackedChildren());
        Label descendantsCount = new Label("Tracked animal descendants count: " + animal.getDescendants());
        Label epochDied;
        if (animal.getEpochDied() == -1) {
            epochDied = new Label("Tracked animal is still alive!");
        }
        else {
            epochDied = new Label("Tracked animal died in epoch " + animal.getEpochDied());
        }
        oneAnimalStatistics.getChildren().addAll(childrenCount, descendantsCount, epochDied);
    }

    public void highlightAnimals() {
        List<Animal> toPlace = map.getToPlace();
        for (Animal animal : toPlace) {
            if (animal.getGenome().equals(map.mostCommonGenome)) {
                shapes.get(animal.getPosition()).setFill(rgb(102, 102, 255));
            }
        }
    }
}
