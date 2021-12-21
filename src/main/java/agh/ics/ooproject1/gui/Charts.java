package agh.ics.ooproject1.gui;

import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

public class Charts {
    private LineChart<Number, Number> allAnimalsChart;
    private LineChart<Number, Number> allGrassChart;
    private XYChart.Series<Number, Number> animalSeries;
    private XYChart.Series<Number, Number> grassSeries;
    private List<LineChart> allCharts = new ArrayList<>();
    private int day = 0;

    public Charts() {
        allAnimalsChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        allGrassChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        allCharts.add(allAnimalsChart);
        allCharts.add(allGrassChart);
        animalSeries = new XYChart.Series<>();
        grassSeries = new XYChart.Series<>();
        allAnimalsChart.getData().add(animalSeries);
        allGrassChart.getData().add(grassSeries);
        allAnimalsChart.setCreateSymbols(false);
        allGrassChart.setCreateSymbols(false);
    }

    public void addDataToCharts(int newAnimalCount, int newGrassCount){
        day++;
        animalSeries.getData().add(new XYChart.Data<>(day, newAnimalCount));
        grassSeries.getData().add(new XYChart.Data<>(day, newGrassCount));
    }

    public List<LineChart> getAllCharts() {
        return allCharts;
    }
}
