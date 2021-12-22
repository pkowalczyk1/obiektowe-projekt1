package agh.ics.ooproject1.gui;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

public class Charts {
    private LineChart<Number, Number> allAnimalsChart;
    private LineChart<Number, Number> allGrassChart;
    private LineChart<Number, Number> energyAvgChart;
    private LineChart<Number, Number> lifespanAvgChart;
    private XYChart.Series<Number, Number> animalSeries;
    private XYChart.Series<Number, Number> grassSeries;
    private XYChart.Series<Number, Number> energyAvgSeries;
    private XYChart.Series<Number, Number> lifespanAvgSeries;
    private List<LineChart> allCharts = new ArrayList<>();
    private int day = 0;

    public Charts() {
        allAnimalsChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        allGrassChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        energyAvgChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        lifespanAvgChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        allCharts.add(allAnimalsChart);
        allCharts.add(allGrassChart);
        allCharts.add(energyAvgChart);
        allCharts.add(lifespanAvgChart);
        animalSeries = new XYChart.Series<>();
        grassSeries = new XYChart.Series<>();
        energyAvgSeries = new XYChart.Series<>();
        lifespanAvgSeries = new XYChart.Series<>();
        allAnimalsChart.getData().add(animalSeries);
        allGrassChart.getData().add(grassSeries);
        energyAvgChart.getData().add(energyAvgSeries);
        lifespanAvgChart.getData().add(lifespanAvgSeries);
        allAnimalsChart.setCreateSymbols(false);
        allGrassChart.setCreateSymbols(false);
        energyAvgChart.setCreateSymbols(false);
        lifespanAvgChart.setCreateSymbols(false);
    }

    public void addDataToCharts(int newAnimalCount, int newGrassCount, double newEnergyAvg, double newLifespanAvg){
        day++;
        animalSeries.getData().add(new XYChart.Data<>(day, newAnimalCount));
        grassSeries.getData().add(new XYChart.Data<>(day, newGrassCount));
        energyAvgSeries.getData().add(new XYChart.Data<>(day, newEnergyAvg));
        lifespanAvgSeries.getData().add(new XYChart.Data<>(day, newLifespanAvg));
    }

    public List<LineChart> getAllCharts() {
        return allCharts;
    }
}
