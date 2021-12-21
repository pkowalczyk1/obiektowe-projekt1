package agh.ics.ooproject1.gui;

import agh.ics.ooproject1.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Map;

public class MapBox {
    private AbstractWorldMap map;
    private SimulationEngine engine;
    private Thread thread;
    private final int mapWidth = 500;
    private VBox wrapper;
    private Charts charts;
    private HBox mapAndCharts;
    private VBox statistics;
    private VBox chartBox;
    private VBox mapAndButton;
    private Button startStop;
    private GridPane grid;

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
        makeGrid();
        mapAndButton.getChildren().addAll(grid, startStop);
        chartBox.getChildren().addAll(charts.getAllCharts());
        mapAndCharts.getChildren().addAll(chartBox, mapAndButton);
        statistics.getChildren().add(new Label("Current animals: " + this.map.animalCount));
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

    public void refresh() {
        makeGrid();
        charts.addDataToCharts(map.animalCount, map.grassCount);
    }

    public void makeGrid() {
        grid.getChildren().clear();
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

//        grid.add(new Label("test"), 0, 0);

        grid.setStyle("-fx-background-color: rgb(255, 153, 0)");

        Map<Vector2d, Grass> grassMap = map.getGrassFields();
        for (Grass grass : grassMap.values()) {
            Pane pane = new StackPane();
            pane.setStyle("-fx-background-color: darkgreen");
            pane.setPrefSize(cellSize, cellSize);
            grid.add(pane, grass.getPosition().x, map.getHeight() - grass.getPosition().y - 1);
        }

        List<Animal> toPlace = map.getToPlace();
        for (Animal animal : toPlace) {
            Pane pane = new StackPane();
            pane.setPrefSize(cellSize, cellSize);
            Rectangle shape = animal.getImage();
            shape.setWidth(cellSize);
            shape.setHeight(cellSize);
            pane.getChildren().add(shape);
            grid.add(pane, animal.getPosition().x, map.getHeight() - animal.getPosition().y - 1);
        }

//        grid.getRowConstraints().add(new RowConstraints(cellSize));
//
//        for (int i=0; i<map.getWidth(); i++) {
//            Pane pane = new StackPane();
//            pane.setStyle("-fx-background-color: rgb(255, 153, 0)");
//            if (map.animalsAt(new Vector2d(i, 0)) != null) {
//                List<Animal> list = map.animalsAt(new Vector2d(i, 0));
//                list.sort(Comparator.comparingInt(Animal::getEnergy));
//                Animal toShow = list.get(list.size() - 1);
//                Rectangle image = toShow.getImage();
//                image.setHeight(cellSize);
//                image.setWidth(cellSize);
//                pane.getChildren().add(image);
//            }
//            if (map.grassAt(new Vector2d(i, 0)) != null) {
//                pane.setStyle("-fx-background-color: darkgreen");
//            }
//            pane.setPrefSize(cellSize, cellSize);
//            grid.add(pane, i, 0);
//            grid.getColumnConstraints().add(new ColumnConstraints(cellSize));
//        }
//
//        for (int i=1; i<map.getHeight(); i++) {
//            Pane pane = new StackPane();
//            pane.setStyle("-fx-background-color: rgb(255, 153, 0)");
//            if (map.animalsAt(new Vector2d(0, i)) != null) {
//                List<Animal> list = map.animalsAt(new Vector2d(0, i));
//                list.sort(Comparator.comparingInt(Animal::getEnergy));
//                Animal toShow = list.get(list.size() - 1);
//                Rectangle image = toShow.getImage();
//                image.setHeight(cellSize);
//                image.setWidth(cellSize);
//                pane.getChildren().add(image);
//            }
//            if (map.grassAt(new Vector2d(0, i)) != null) {
//                pane.setStyle("-fx-background-color: darkgreen");
//            }
//            pane.setPrefSize(cellSize, cellSize);
//            grid.add(pane, 0, map.getHeight() - i);
//            grid.getRowConstraints().add(new RowConstraints(cellSize));
//        }
//
//        for (int i=1; i<map.getWidth(); i++) {
//            for (int j=1; j<map.getHeight(); j++) {
//                Pane pane = new StackPane();
//                pane.setStyle("-fx-background-color: rgb(255, 153, 0)");
//                if (map.animalsAt(new Vector2d(i, j)) != null) {
//                    List<Animal> list = map.animalsAt(new Vector2d(i, j));
//                    list.sort(Comparator.comparingInt(Animal::getEnergy));
//                    Animal toShow = list.get(list.size() - 1);
//                    Rectangle image = toShow.getImage();
//                    image.setHeight(cellSize);
//                    image.setWidth(cellSize);
//                    pane.getChildren().add(image);
//                }
//                if (map.grassAt(new Vector2d(i, j)) != null) {
//                    pane.setStyle("-fx-background-color: darkgreen");
//                }
//                pane.setPrefSize(cellSize, cellSize);
//                grid.add(pane, i, map.getHeight() - j);
//            }
//        }
    }
}
