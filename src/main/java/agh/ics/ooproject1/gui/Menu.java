package agh.ics.ooproject1.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class Menu {
    private final VBox wrapper;
    private final Label widthLabel;
    private final TextField widthText;
    private final Label heightLabel;
    private final TextField heightText;
    private final Label startEnergyLabel;
    private final TextField startEnergyText;
    private final Label moveCostLabel;
    private final TextField moveCostText;
    private final Label plantEnergyLabel;
    private final TextField plantEnergyText;
    private final Label jungleRatioLabel;
    private final TextField jungleRatioText;
    private final Label animalCountLabel;
    private final TextField animalCountText;
    private final CheckBox leftMapStrategy;
    private final CheckBox rightMapStrategy;
    public final Button submit;

    public Menu() {
        widthLabel = new Label("Map width");
        widthText = new TextField("10");
        widthText.setMaxWidth(200);
        heightLabel = new Label("Map height");
        heightText = new TextField("10");
        heightText.setMaxWidth(200);
        startEnergyLabel = new Label("Start energy");
        startEnergyText = new TextField("50");
        startEnergyText.setMaxWidth(200);
        moveCostLabel = new Label("Move cost");
        moveCostText = new TextField("6");
        moveCostText.setMaxWidth(200);
        plantEnergyLabel = new Label("Plant energy");
        plantEnergyText = new TextField("100");
        plantEnergyText.setMaxWidth(200);
        jungleRatioLabel = new Label("Jungle-to-map ratio");
        jungleRatioText = new TextField("0.7");
        jungleRatioText.setMaxWidth(200);
        animalCountLabel = new Label("Count of animals at the start");
        animalCountText = new TextField("10");
        animalCountText.setMaxWidth(200);
        leftMapStrategy = new CheckBox("Apply magic to left map?");
        rightMapStrategy = new CheckBox("Apply magic to right map?");
        submit = new Button("Go!");
        wrapper = new VBox(20, widthLabel, widthText,
                heightLabel, heightText,
                startEnergyLabel, startEnergyText,
                moveCostLabel, moveCostText,
                plantEnergyLabel, plantEnergyText,
                jungleRatioLabel, jungleRatioText,
                animalCountLabel, animalCountText,
                leftMapStrategy, rightMapStrategy,
                submit);
        wrapper.setAlignment(Pos.CENTER);
    }

    public int getWidth() throws IllegalArgumentException {
        if (!isNumeric(widthText.getText())) {
            throw new IllegalArgumentException("Start parameters should be numbers, not texts");
        }
        if (Integer.parseInt(widthText.getText()) > 200) {
            throw new IllegalArgumentException("Too wide map");
        }
        return Integer.parseInt(widthText.getText());
    }

    public int getHeight() throws IllegalArgumentException {
        if (!isNumeric(heightText.getText())) {
            throw new IllegalArgumentException("Start parameters should be numbers, not texts");
        }
        if (Integer.parseInt(heightText.getText()) > 200) {
            throw new IllegalArgumentException("Too high map");
        }
        return Integer.parseInt(heightText.getText());
    }

    public int getStartEnergy() throws IllegalArgumentException {
        if (!isNumeric(startEnergyText.getText())) {
            throw new IllegalArgumentException("Start parameters should be numbers, not texts");
        }
        return Integer.parseInt(startEnergyText.getText());
    }

    public int getMoveCost() throws IllegalArgumentException {
        if (!isNumeric(moveCostText.getText())) {
            throw new IllegalArgumentException("Start parameters should be numbers, not texts");
        }
        return Integer.parseInt(moveCostText.getText());
    }

    public int getPlantEnergy() throws IllegalArgumentException {
        if (!isNumeric(plantEnergyText.getText())) {
            throw new IllegalArgumentException("Start parameters should be numbers, not texts");
        }
        return Integer.parseInt(plantEnergyText.getText());
    }

    public double getJungleRatio() throws IllegalArgumentException {
        if (!isNumeric(jungleRatioText.getText())) {
            throw new IllegalArgumentException("Start parameters should be numbers, not texts");
        }
        if (Double.parseDouble(jungleRatioText.getText()) > 1) {
            throw new IllegalArgumentException("Jungle ratio cannot be greater than 1");
        }
        return Double.parseDouble(jungleRatioText.getText());
    }

    public int getAnimalCount() throws IllegalArgumentException {
        if (!isNumeric(animalCountText.getText())) {
            throw new IllegalArgumentException("Start parameters should be numbers, not texts");
        }
        if (Integer.parseInt(animalCountText.getText()) > Integer.parseInt(widthText.getText())*Integer.parseInt(heightText.getText())) {
            throw new IllegalArgumentException("Animal count cannot be greater than map size");
        }
        return Integer.parseInt(animalCountText.getText());
    }

    public boolean getLeftMapStrategy() {
        return leftMapStrategy.isSelected();
    }

    public boolean getRightMapStrategy() {
        return rightMapStrategy.isSelected();
    }

    public VBox getWrapper() {
        return wrapper;
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
