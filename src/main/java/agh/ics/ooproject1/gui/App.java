package agh.ics.ooproject1.gui;

import agh.ics.ooproject1.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application implements IGuiObserver {
    private AbstractWorldMap map1, map2;
    private SimulationEngine engine1, engine2;
    private final GridPane grid1 = new GridPane();
    private final GridPane grid2 = new GridPane();
    private MapBox mapBoxLeft;
    private MapBox mapBoxRight;
    private final double mapWidth = 500;
    private final int sceneWidth = 1000;
    private final int sceneHeight = 800;

    @Override
    public void start(Stage primaryStage) {
        // parametry startowe
        Label widthLabel = new Label("Szerokosc mapy");
        TextField widthText = new TextField("10");
        widthText.setMaxWidth(200);
        Label heightLabel = new Label("Wysokosc mapy");
        TextField heightText = new TextField("10");
        heightText.setMaxWidth(200);
        Label startEnergyLabel = new Label("Energia startowa");
        TextField startEnergyText = new TextField("50");
        startEnergyText.setMaxWidth(200);
        Label moveCostLabel = new Label("Koszt ruchu");
        TextField moveCostText = new TextField("6");
        moveCostText.setMaxWidth(200);
        Label plantEnergyLabel = new Label("Energia za zjedzenie rosliny");
        TextField plantEnergyText = new TextField("100");
        plantEnergyText.setMaxWidth(200);
        Label jungleRatioLabel = new Label("Stosunek dzungli do calej mapy");
        TextField jungleRatioText = new TextField("0.7");
        jungleRatioText.setMaxWidth(200);
        Label animalCountLabel = new Label("Ilosc zwierzat na poczatku");
        TextField animalCountText = new TextField("10");
        animalCountText.setMaxWidth(200);
        Button submit = new Button("Dalej");

        VBox main = new VBox(20, widthLabel, widthText,
                heightLabel, heightText,
                startEnergyLabel, startEnergyText,
                moveCostLabel, moveCostText,
                plantEnergyLabel, plantEnergyText,
                jungleRatioLabel, jungleRatioText,
                animalCountLabel, animalCountText,
                submit);

        submit.setOnAction(event -> {
            int width = Integer.parseInt(widthText.getText());
            int height = Integer.parseInt(heightText.getText());
            int startEnergy = Integer.parseInt(startEnergyText.getText());
            int moveCost = Integer.parseInt(moveCostText.getText());
            int plantEnergy = Integer.parseInt(plantEnergyText.getText());
            double jungleRatio = Double.parseDouble(jungleRatioText.getText());
            int animalCount = Integer.parseInt(animalCountText.getText());

            this.map1 = new UnboundedMap(width, height, jungleRatio, plantEnergy, moveCost, startEnergy);
            this.map2 = new BoundedMap(width, height, jungleRatio, plantEnergy, moveCost, startEnergy);
            this.engine1 = new SimulationEngine(map1, animalCount, this);
            this.engine2 = new SimulationEngine(map2, animalCount, this);
            main.getChildren().clear();
            main.setAlignment(Pos.CENTER);

            Thread thread1 = new Thread(engine1);
            Thread thread2 = new Thread(engine2);

//            makeGrid(map1);
//            makeGrid(map2);
//            VBox left = new VBox(10, grid1);
//            this.map1Count = new Label("Current animals: " + this.map1.count);
//            left.getChildren().add(this.map1Count);
//            VBox right = new VBox(10, grid2);
//            this.map2Count = new Label("Current animals: " + this.map2.count);
//            right.getChildren().add(this.map2Count);
//            HBox mapBox = new HBox(40, left, right);
            mapBoxLeft = new MapBox(engine1, map1, thread1);
            mapBoxRight = new MapBox(engine2, map2, thread2);
            HBox maps = new HBox(mapBoxLeft.getWrapper(), mapBoxRight.getWrapper());
            maps.setAlignment(Pos.CENTER);
            main.getChildren().addAll(maps);

            thread1.start();
            thread2.start();
        });

        main.setPrefWidth(800);
        main.setAlignment(Pos.CENTER);

        Scene scene = new Scene(main, sceneWidth, sceneHeight);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

//    private void makeGrid(AbstractWorldMap map) {
//        GridPane grid;
//        if (map instanceof UnboundedMap) {
//            grid = this.grid1;
//        }
//        else {
//            grid = this.grid2;
//        }
//        grid.getChildren().clear();
//        grid.getColumnConstraints().clear();
//        grid.getRowConstraints().clear();
//        double cellSize = Math.min(this.mapWidth/map.getWidth(), this.mapWidth/map.getHeight());
//        grid.setPrefSize(map.getWidth()*cellSize, map.getHeight()*cellSize);
//
//        for (int i=0; i<map.getWidth(); i++) {
//            grid.getColumnConstraints().add(new ColumnConstraints(cellSize));
//        }
//
//        for (int i=0; i<map.getHeight(); i++) {
//            grid.getRowConstraints().add(new RowConstraints(cellSize));
//        }

//        grid.add(new Label("test"), 0, 0);

//        grid.setStyle("-fx-background-color: rgb(255, 153, 0)");
//
//        Map<Vector2d, Grass> grassMap = map.getGrassFields();
//        for (Grass grass : grassMap.values()) {
//            Pane pane = new StackPane();
//            pane.setStyle("-fx-background-color: darkgreen");
//            pane.setPrefSize(cellSize, cellSize);
//            grid.add(pane, grass.getPosition().x, map.getHeight() - grass.getPosition().y - 1);
//        }
//
//        List<Animal> toPlace = map.getToPlace();
//        for (Animal animal : toPlace) {
//            Pane pane = new StackPane();
//            pane.setPrefSize(cellSize, cellSize);
//            Rectangle shape = animal.getImage();
//            shape.setWidth(cellSize);
//            shape.setHeight(cellSize);
//            pane.getChildren().add(shape);
//            grid.add(pane, animal.getPosition().x, map.getHeight() - animal.getPosition().y - 1);
//        }

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
//    }

    @Override
    public void newDay(AbstractWorldMap map) {
        Platform.runLater(() -> {
            if (map instanceof UnboundedMap) {
                mapBoxLeft.refresh();
            }
            else {
                mapBoxRight.refresh();
            }
        });
    }
}
