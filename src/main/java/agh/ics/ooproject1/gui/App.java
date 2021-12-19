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
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;

public class App extends Application implements IGuiObserver {
    private AbstractWorldMap map;
    private SimulationEngine engine;
    private final GridPane grid = new GridPane();
    private final int mapWidth = 100;

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
        Label jungleRatioLabel = new Label("Stosunek dzungli to calej mapy");
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

            this.map = new UnboundedMap(width, height, jungleRatio, plantEnergy, moveCost, startEnergy);
            this.engine = new SimulationEngine(map, animalCount, this);
            makeGrid();
            main.getChildren().clear();
            main.getChildren().add(this.grid);

            Thread thread = new Thread(engine);
            thread.start();
        });

        main.setMaxWidth(800);
        main.setAlignment(Pos.CENTER);

        Scene scene = new Scene(main, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    private void makeGrid() {
        this.grid.getChildren().clear();
        this.grid.getColumnConstraints().clear();
        this.grid.getRowConstraints().clear();

        this.grid.getRowConstraints().add(new RowConstraints(20));

        for (int i=0; i<this.map.getWidth(); i++) {
            Pane pane = new StackPane();
            pane.setStyle("-fx-background-color: rgb(255, 153, 0)");
            if (this.map.animalsAt(new Vector2d(i, 0)) != null) {
                List<Animal> list = this.map.animalsAt(new Vector2d(i, 0));
                list.sort(Comparator.comparingInt(Animal::getEnergy));
                Animal toShow = list.get(list.size() - 1);
                Circle image = toShow.getImage();
                image.setRadius(8);
                pane.getChildren().add(image);
            }
            if (this.map.grassAt(new Vector2d(i, 0)) != null) {
                pane.setStyle("-fx-background-color: darkgreen");
            }
            pane.setPrefSize(20, 20);
            this.grid.add(pane, i, 0);
            this.grid.getColumnConstraints().add(new ColumnConstraints(20));
        }

        for (int i=1; i<this.map.getHeight(); i++) {
            Pane pane = new StackPane();
            pane.setStyle("-fx-background-color: rgb(255, 153, 0)");
            if (this.map.animalsAt(new Vector2d(0, i)) != null) {
                List<Animal> list = this.map.animalsAt(new Vector2d(0, i));
                list.sort(Comparator.comparingInt(Animal::getEnergy));
                Animal toShow = list.get(list.size() - 1);
                Circle image = toShow.getImage();
                image.setRadius(8);
                pane.getChildren().add(image);
            }
            if (this.map.grassAt(new Vector2d(0, i)) != null) {
                pane.setStyle("-fx-background-color: darkgreen");
            }
            pane.setPrefSize(20, 20);
            this.grid.add(pane, 0, this.map.getHeight() - i);
            this.grid.getRowConstraints().add(new RowConstraints(20));
        }

        for (int i=1; i<this.map.getWidth(); i++) {
            for (int j=1; j<this.map.getHeight(); j++) {
                Pane pane = new StackPane();
                pane.setStyle("-fx-background-color: rgb(255, 153, 0)");
                if (this.map.animalsAt(new Vector2d(i, j)) != null) {
                    List<Animal> list = this.map.animalsAt(new Vector2d(i, j));
                    list.sort(Comparator.comparingInt(Animal::getEnergy));
                    Animal toShow = list.get(list.size() - 1);
                    Circle image = toShow.getImage();
                    image.setRadius(8);
                    pane.getChildren().add(image);
                }
                if (this.map.grassAt(new Vector2d(i, j)) != null) {
                    pane.setStyle("-fx-background-color: darkgreen");
                }
                pane.setPrefSize(20, 20);
                this.grid.add(pane, i, this.map.getHeight() - j);
            }
        }
    }

    @Override
    public void newDay() {
        Platform.runLater(this::makeGrid);
    }
}
