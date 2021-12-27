package agh.ics.ooproject1.gui;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

//class that contains all charts displayed in gui
public class Charts {
    private final LineChart<Number, Number> animalsAndGrassChart;
    private final LineChart<Number, Number> energyAvgChart;
    private final LineChart<Number, Number> lifespanAvgChart;
    private final XYChart.Series<Number, Number> animalSeries;
    private final XYChart.Series<Number, Number> grassSeries;
    private final XYChart.Series<Number, Number> energyAvgSeries;
    private final XYChart.Series<Number, Number> lifespanAvgSeries;
    private final XYChart.Series<Number, Number> childrenAvgSeries;
    private final List<LineChart> allCharts = new ArrayList<>();
    private int day = 0;

    public Charts() {
        //create charts and series
        animalsAndGrassChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        energyAvgChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        lifespanAvgChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        allCharts.add(animalsAndGrassChart);
        allCharts.add(energyAvgChart);
        allCharts.add(lifespanAvgChart);
        animalSeries = new XYChart.Series<>();
        grassSeries = new XYChart.Series<>();
        energyAvgSeries = new XYChart.Series<>();
        lifespanAvgSeries = new XYChart.Series<>();
        childrenAvgSeries = new XYChart.Series<>();
        animalSeries.setName("Animal count");
        grassSeries.setName("Grass count");
        energyAvgSeries.setName("Average energy");
        lifespanAvgSeries.setName("Lifespan average");
        childrenAvgSeries.setName("Children count average");
        animalsAndGrassChart.getData().add(animalSeries);
        animalsAndGrassChart.getData().add(grassSeries);
        animalsAndGrassChart.getData().add(childrenAvgSeries);
        energyAvgChart.getData().add(energyAvgSeries);
        lifespanAvgChart.getData().add(lifespanAvgSeries);
        animalsAndGrassChart.setCreateSymbols(false);
        energyAvgChart.setCreateSymbols(false);
        lifespanAvgChart.setCreateSymbols(false);
    }

    //method to add new points to charts
    public void addDataToCharts(int newAnimalCount, int newGrassCount, double newEnergyAvg, double newLifespanAvg, double newChildrenAvg){
        day++;
        animalSeries.getData().add(new XYChart.Data<>(day, newAnimalCount));
        grassSeries.getData().add(new XYChart.Data<>(day, newGrassCount));
        energyAvgSeries.getData().add(new XYChart.Data<>(day, newEnergyAvg));
        lifespanAvgSeries.getData().add(new XYChart.Data<>(day, newLifespanAvg));
        childrenAvgSeries.getData().add(new XYChart.Data<>(day, newChildrenAvg));
    }

    //getter for list with all charts
    public List<LineChart> getAllCharts() {
        return allCharts;
    }
}
