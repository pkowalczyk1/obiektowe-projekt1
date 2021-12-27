package agh.ics.ooproject1.gui;

import agh.ics.ooproject1.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class App extends Application implements IGuiObserver {
    private AbstractWorldMap map1, map2;
    private SimulationEngine engine1, engine2;
    private MapBox mapBoxLeft;
    private MapBox mapBoxRight;
    private final int sceneWidth = 1000;
    private final int sceneHeight = 800;

    @Override
    public void start(Stage primaryStage) {
        Menu menu = new Menu();

        VBox main = new VBox(menu.getWrapper());

        menu.submit.setOnAction(event -> {
            int width = menu.getWidth();
            int height = menu.getHeight();
            int startEnergy = menu.getStartEnergy();
            int moveCost = menu.getMoveCost();
            int plantEnergy = menu.getPlantEnergy();
            double jungleRatio = menu.getJungleRatio();
            int animalCount = menu.getAnimalCount();

            boolean leftMapStrategy = menu.getLeftMapStrategy();
            boolean rightMapStrategy = menu.getRightMapStrategy();

            this.map1 = new UnboundedMap(width, height, jungleRatio, plantEnergy, moveCost, startEnergy, leftMapStrategy);
            this.map2 = new BoundedMap(width, height, jungleRatio, plantEnergy, moveCost, startEnergy, rightMapStrategy);

            this.engine1 = new SimulationEngine(map1, animalCount, this);
            this.engine2 = new SimulationEngine(map2, animalCount, this);

            main.getChildren().clear();
            main.setAlignment(Pos.CENTER);

            Thread thread1 = new Thread(engine1);
            Thread thread2 = new Thread(engine2);

            mapBoxLeft = new MapBox(engine1, map1, thread1);
            mapBoxRight = new MapBox(engine2, map2, thread2);
            HBox maps = new HBox(mapBoxLeft.getWrapper(), mapBoxRight.getWrapper());
            maps.setAlignment(Pos.CENTER);
            main.getChildren().addAll(maps);

            thread1.start();
            thread2.start();

            primaryStage.setOnCloseRequest((e) -> {
                engine1.isGoing = false;
                engine2.isGoing = false;
                thread1.resume();
                thread2.resume();
            });
        });

        main.setPrefWidth(800);
        main.setAlignment(Pos.CENTER);

        Scene scene = new Scene(main, sceneWidth, sceneHeight);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    @Override
    public void newDay(AbstractWorldMap map, List<Grass> toPlace, List<Grass> toDeletion, boolean magic) {
        Platform.runLater(() -> {
            if (map instanceof UnboundedMap) {
                mapBoxLeft.refresh(toPlace, toDeletion);
                if (map.selectedAnimal != null) {
                    mapBoxLeft.showStatistics(map.selectedAnimal);
                }

                if (magic) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Magic spawn!");
                    alert.setHeaderText("Left map");
                    alert.setContentText("Magic spawn " + engine1.magicCount + "/3");
                    alert.show();
                }

                if (map.selectedAnimal != null) {
                    mapBoxLeft.showStatistics(map.selectedAnimal);
                }
            }
            else {
                mapBoxRight.refresh(toPlace, toDeletion);
                if (map.selectedAnimal != null) {
                    mapBoxRight.showStatistics(map.selectedAnimal);
                }

                if (magic) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Magic spawn!");
                    alert.setHeaderText("Right map");
                    alert.setContentText("Magic spawn " + engine2.magicCount + "/3");
                    alert.show();
                }

                if (map.selectedAnimal != null) {
                    mapBoxRight.showStatistics(map.selectedAnimal);
                }
            }
        });
    }
}
