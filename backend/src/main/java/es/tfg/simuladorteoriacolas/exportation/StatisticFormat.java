package es.tfg.simuladorteoriacolas.exportation;

import es.tfg.simuladorteoriacolas.items.ItemDTO;
import org.w3c.dom.ls.LSException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class StatisticFormat {
    private StatisticFormat(){

    }

    //Returns the statistics of the simulations results
    public static List<ItemStatistics> formatListSimulations(List<List<ItemDTO>> simulations){
        List<ItemStatistics> itemStatisticsList = new ArrayList<>();
        for (var i = 0; i < simulations.get(0).size(); i++) {
            String typeOfItem = simulations.get(0).get(i).getItem().getDescription();
            String nameOfItem = simulations.get(0).get(i).getItem().getName();
            ItemStatistics itemStatistics = new ItemStatistics();
            itemStatistics.setNameItem(nameOfItem);
            List<HashMap<String, Double>> statistics = new ArrayList<>();
            HashMap<String, Double> statistic = new HashMap<>();
            Double sumSquaresDifference;
            Double mean;
            Double max;
            Double min;
            Double deviation;
            switch (typeOfItem) {
                case "Source":
                    List<String> nameStatisticList = new ArrayList<>();
                    List<Double> outProducts = new ArrayList<>();
                    for (var simulationIndex = 0; simulationIndex < simulations.size(); simulationIndex++) {
                        ItemDTO currentItem = simulations.get(simulationIndex).get(i);
                        outProducts.add((double) currentItem.getSource().getOutSource());
                    }
                    mean = (double) (outProducts.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
                    statistic.put("Media", mean);

                    Double meanAux = mean;
                    sumSquaresDifference = outProducts.stream()
                            .mapToDouble(numero -> Math.pow(numero - meanAux, 2))
                            .sum();
                    deviation = Math.sqrt(sumSquaresDifference / outProducts.size());
                    statistic.put("Desviación típica", deviation);

                    max = (double) (outProducts.stream().mapToDouble(Double::doubleValue).max().orElse(0.0));
                    statistic.put("Max.", max);

                    min = (double) (outProducts.stream().mapToDouble(Double::doubleValue).min().orElse(0.0));
                    statistic.put("Min.", min);
                    statistics.add(statistic);

                    nameStatisticList.add("Nº productos salen (Output)");
                    itemStatistics.setNameStatistic(nameStatisticList);

                    itemStatistics.setStatistics(statistics);
                    break;
                case "Queue":
                    nameStatisticList = List.of("Nº productos salen (Output)", "Nº productos entran (Input)", "Contenido medio de la cola", "Contenido máximo de la cola", "Estancia máxima de producto (seg)", "Estancia media de productos (seg)");

                    for (String name : nameStatisticList) {
                        List<Double> numbers = new ArrayList<>();
                        statistic = new HashMap<>();
                        for (var simulationIndex = 0; simulationIndex < simulations.size(); simulationIndex++) {
                            ItemDTO currentItem = simulations.get(simulationIndex).get(i);
                            switch (name) {
                                case "Nº productos salen (Output)":
                                    numbers.add((double) currentItem.getQueue().getOutQueue());
                                    break;
                                case "Nº productos entran (Input)":
                                    numbers.add((double) currentItem.getQueue().getTotalInQueue());
                                    break;
                                case "Contenido medio de la cola":
                                    numbers.add((double) currentItem.getQueue().getAvgContent());
                                    break;
                                case "Contenido máximo de la cola":
                                    numbers.add((double) currentItem.getQueue().getMaxContent());
                                    break;
                                case "Estancia máxima de producto (seg)":
                                    numbers.add((double) currentItem.getQueue().getMaxStays());
                                    break;
                                case "Estancia media de productos (seg)":
                                    numbers.add((double) currentItem.getQueue().getAvgStayTime());
                                    break;
                            }
                        }
                        mean = (double) (numbers.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
                        statistic.put("Media", mean);

                        Double meanAux1 = mean;
                        sumSquaresDifference = numbers.stream()
                                .mapToDouble(numero -> Math.pow(numero - meanAux1, 2))
                                .sum();
                        deviation = Math.sqrt(sumSquaresDifference / numbers.size());
                        statistic.put("Desviación típica", deviation);

                        max = (double) (numbers.stream().mapToDouble(Double::doubleValue).max().orElse(0.0));
                        statistic.put("Max.", max);

                        min = (double) (numbers.stream().mapToDouble(Double::doubleValue).min().orElse(0.0));
                        statistic.put("Min.", min);
                        statistics.add(statistic);

                    }
                    itemStatistics.setStatistics(statistics);
                    itemStatistics.setNameStatistic(nameStatisticList);

                    break;
                case "Server":
                    nameStatisticList = List.of("Nº productos salen (Output)", "Estancia máxima de producto (seg)", "Estancia media de productos (seg)", "Pct. uso de utilización");

                    for (String name : nameStatisticList) {
                        List<Double> numbers = new ArrayList<>();
                        statistic = new HashMap<>();
                        for (var simulationIndex = 0; simulationIndex < simulations.size(); simulationIndex++) {
                            ItemDTO currentItem = simulations.get(simulationIndex).get(i);
                            switch (name) {
                                case "Nº productos salen (Output)":
                                    numbers.add((double) currentItem.getServer().getOutServer());
                                    break;
                                case "Estancia máxima de producto (seg)":
                                    numbers.add((double) currentItem.getServer().getMaxStays());
                                    break;
                                case "Estancia media de productos (seg)":
                                    numbers.add((double) currentItem.getServer().getAvgStayTime());
                                    break;
                                case "Pct. uso de utilización":
                                    numbers.add((double) currentItem.getServer().getPctBusyTime());
                                    break;
                            }
                        }
                        mean = (double) (numbers.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
                        statistic.put("Media", mean);

                        Double meanAux1 = mean;
                        sumSquaresDifference = numbers.stream()
                                .mapToDouble(numero -> Math.pow(numero - meanAux1, 2))
                                .sum();
                        deviation = Math.sqrt(sumSquaresDifference / numbers.size());
                        statistic.put("Desviación típica", deviation);

                        max = (double) (numbers.stream().mapToDouble(Double::doubleValue).max().orElse(0.0));
                        statistic.put("Max.", max);

                        min = (double) (numbers.stream().mapToDouble(Double::doubleValue).min().orElse(0.0));
                        statistic.put("Min.", min);
                        statistics.add(statistic);

                    }
                    itemStatistics.setStatistics(statistics);
                    itemStatistics.setNameStatistic(nameStatisticList);

                    break;
                case "Sink":
                    nameStatisticList = new ArrayList<>();
                    List<Integer> inProducts = new ArrayList<>();
                    for (var simulationIndex = 0; simulationIndex < simulations.size(); simulationIndex++) {
                        ItemDTO currentItem = simulations.get(simulationIndex).get(i);
                        inProducts.add(currentItem.getSink().getInSink());
                    }
                    mean = (double) (inProducts.stream().mapToInt(Integer::intValue).average().orElse(0.0));
                    statistic.put("Media", mean);

                    Double meanAux2 = mean;
                    sumSquaresDifference = inProducts.stream()
                            .mapToDouble(numero -> Math.pow(numero - meanAux2, 2))
                            .sum();
                    deviation = Math.sqrt(sumSquaresDifference / inProducts.size());
                    statistic.put("Desviación típica", deviation);

                    max = (double) (inProducts.stream().mapToInt(Integer::intValue).max().orElse(0));
                    statistic.put("Max.", max);

                    min = (double) (inProducts.stream().mapToInt(Integer::intValue).min().orElse(0));
                    statistic.put("Min.", min);
                    statistics.add(statistic);

                    nameStatisticList.add("Nº productos entran (Input)");
                    itemStatistics.setNameStatistic(nameStatisticList);

                    itemStatistics.setStatistics(statistics);
                    break;
            }
            itemStatisticsList.add(itemStatistics);
        }
        return itemStatisticsList;
    }

    //Returns the raw data from the simulations
    public static List<RawData> formatRawData(List<List<ItemDTO>> simulations){

        List<RawData> itemsRawDataList = new ArrayList<>();
        for (var i = 0; i < simulations.get(0).size(); i++) {
            String typeOfItem = simulations.get(0).get(i).getItem().getDescription();
            String nameOfItem = simulations.get(0).get(i).getItem().getName();
            RawData rawData = new RawData();
            rawData.setNameItem(nameOfItem);
            List<String> nameStatisticList= new ArrayList<>();
            List<List<String>> rawDataFromSimulations = new ArrayList<>();
            switch (typeOfItem) {
                case "Source":
                    List<String> outProducts= new ArrayList<>();
                    nameStatisticList.add("Nº productos salen (Output)");
                    for (var simulationIndex = 0; simulationIndex < simulations.size(); simulationIndex++) {
                        ItemDTO currentItem = simulations.get(simulationIndex).get(i);
                        outProducts.add(String.valueOf(Math.round(currentItem.getSource().getOutSource() * 100d) / 100d));
                    }
                    rawDataFromSimulations.add(outProducts);

                    rawData.setNameItem(nameOfItem);
                    rawData.setRawDataFromSimulations(rawDataFromSimulations);
                    rawData.setNameStatisticList(nameStatisticList);
                    break;
                case "Queue":
                    nameStatisticList = List.of("Nº productos salen (Output)", "Nº productos entran (Input)", "Contenido medio de la cola", "Contenido máximo de la cola", "Estancia máxima de producto (seg)", "Estancia media de productos (seg)");
                    outProducts= new ArrayList<>();
                    List<String> inProducts= new ArrayList<>();
                    List<String> avgContent= new ArrayList<>();
                    List<String> maxContent= new ArrayList<>();
                    List<String> maxStays= new ArrayList<>();
                    List<String> avgStays = new ArrayList<>();

                    for (var simulationIndex = 0; simulationIndex < simulations.size(); simulationIndex++) {
                        ItemDTO currentItem = simulations.get(simulationIndex).get(i);
                        outProducts.add(String.valueOf(Math.round(currentItem.getQueue().getOutQueue() * 100d) / 100d));
                        inProducts.add(String.valueOf(Math.round(currentItem.getQueue().getTotalInQueue() * 100d) / 100d));
                        avgContent.add(String.valueOf(Math.round(currentItem.getQueue().getAvgContent() * 100d) / 100d));
                        maxContent.add(String.valueOf(Math.round(currentItem.getQueue().getMaxContent() * 100d) / 100d));
                        maxStays.add(String.valueOf(Math.round(currentItem.getQueue().getMaxStays() * 100d) / 100d));
                        avgStays.add(String.valueOf(Math.round(currentItem.getQueue().getAvgStayTime() * 100d) / 100d));
                    }

                    rawDataFromSimulations.add(outProducts);
                    rawDataFromSimulations.add(inProducts);
                    rawDataFromSimulations.add(avgContent);
                    rawDataFromSimulations.add(maxContent);
                    rawDataFromSimulations.add(maxStays);
                    rawDataFromSimulations.add(avgStays);

                    rawData.setNameStatisticList(nameStatisticList);
                    rawData.setRawDataFromSimulations(rawDataFromSimulations);
                    rawData.setNameItem(nameOfItem);
                    break;

                case "Server":
                    nameStatisticList = List.of("Nº productos salen (Output)", "Estancia máxima de producto (seg)", "Estancia media de productos (seg)", "Pct. uso de utilización");

                    outProducts= new ArrayList<>();
                    maxStays= new ArrayList<>();
                    avgStays = new ArrayList<>();
                    List<String> utilPct= new ArrayList<>();

                    for (var simulationIndex = 0; simulationIndex < simulations.size(); simulationIndex++) {
                        ItemDTO currentItem = simulations.get(simulationIndex).get(i);
                        outProducts.add(String.valueOf(Math.round(currentItem.getServer().getOutServer() * 100d) / 100d));
                        maxStays.add(String.valueOf(Math.round(currentItem.getServer().getMaxStays() * 100d) / 100d));
                        avgStays.add(String.valueOf(Math.round(currentItem.getServer().getAvgStayTime() * 100d) / 100d));
                        utilPct.add(String.valueOf(Math.round(currentItem.getServer().getPctBusyTime() * 100d) / 100d));
                    }

                    rawDataFromSimulations.add(outProducts);
                    rawDataFromSimulations.add(maxStays);
                    rawDataFromSimulations.add(avgStays);
                    rawDataFromSimulations.add(utilPct);

                    rawData.setNameStatisticList(nameStatisticList);
                    rawData.setRawDataFromSimulations(rawDataFromSimulations);
                    rawData.setNameItem(nameOfItem);
                    break;

                case "Sink":
                    nameStatisticList.add("Nº productos entran (Input)");
                    inProducts = new ArrayList<>();

                    for (var simulationIndex = 0; simulationIndex < simulations.size(); simulationIndex++) {
                        ItemDTO currentItem = simulations.get(simulationIndex).get(i);
                        inProducts.add(String.valueOf(Math.round(currentItem.getSink().getInSink() * 100d) / 100d));
                    }
                    rawDataFromSimulations.add(inProducts);
                    rawData.setNameItem(nameOfItem);
                    rawData.setRawDataFromSimulations(rawDataFromSimulations);
                    rawData.setNameStatisticList(nameStatisticList);

                    break;
            }
            itemsRawDataList.add(rawData);
        }
        return itemsRawDataList;

    }

}
