package es.tfg.simuladorteoriacolas.pdf;

import es.tfg.simuladorteoriacolas.items.ItemDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PdfGeneratorService {

    public PDDocument generatePdf(List<List<ItemDTO>> simulations, String nameFile) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        float margin = 50;
        float yStart = page.getMediaBox().getHeight() - margin;
        float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
        float yPosition = yStart;
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

        int rows;
        int cols = 5;
        for (ItemStatistics itemStatistics : itemStatisticsList) {
            rows = itemStatistics.getNameStatistic().size()+1;

            float rowHeight = 20f;
            float tableHeight = rowHeight * (float) rows;
            float tableYBottom = yStart - tableHeight;

            float y = yStart;
            for (int i = 0; i <= rows; i++) {
                contentStream.moveTo(margin, y);
                contentStream.lineTo(margin + tableWidth, y);
                contentStream.stroke();
                y -= rowHeight;
            }

            float x = margin;
            for (int i = 0; i <= cols; i++) {
                contentStream.moveTo(x, yStart);
                contentStream.lineTo(x, tableYBottom);
                contentStream.stroke();
                x += tableWidth / (float) cols;
            }

            float textx = margin + 4;
            float texty = yStart - 15;
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 9);

            contentStream.beginText();
            contentStream.newLineAtOffset(textx, texty);
            contentStream.showText("");
            contentStream.endText();
            textx += tableWidth / (float) cols;
            for (String header : itemStatistics.getStatistics().get(0).keySet()) {
                contentStream.beginText();
                contentStream.newLineAtOffset(textx, texty);
                contentStream.showText(header);
                contentStream.endText();
                textx += tableWidth / (float) cols;
            }

            float dataY = yStart - 13;

            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin+4, dataY);
            contentStream.showText(itemStatistics.getNameItem());
            contentStream.endText();

            List<List<String>> data= getDataFromHashMap(itemStatistics);
            dataY = yStart - 32.5f;
            for (List<String> row : data) {
                float dataX = margin + 4;
                for (String cell : row) {
                    contentStream.setFont(PDType1Font.HELVETICA, 6);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(dataX, dataY);
                    contentStream.showText(cell);
                    contentStream.endText();
                    dataX += tableWidth / (float) cols;
                }
                dataY -= rowHeight;
            }

            yStart= tableYBottom - 40f;

        }
/*
        int rows = data.size();
        int cols = headers.size();
        float rowHeight = 20f;
        float tableHeight = rowHeight * (float) rows;
        float tableYBottom = yStart - tableHeight;

        // Dibujar las celdas de la tabla
        float y = yStart;
        for (int i = 0; i <= rows; i++) {
            contentStream.moveTo(margin, y);
            contentStream.lineTo(margin + tableWidth, y);
            contentStream.stroke();
            y -= rowHeight;
        }

        float x = margin;
        for (int i = 0; i <= cols; i++) {
            contentStream.moveTo(x, yStart);
            contentStream.lineTo(x, tableYBottom);
            contentStream.stroke();
            x += tableWidth / (float) cols;
        }

        // Escribir encabezados
        float textx = margin + 4;
        float texty = yStart - 15;
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        for (String header : headers) {
            contentStream.beginText();
            contentStream.newLineAtOffset(textx, texty);
            contentStream.showText(header);
            contentStream.endText();
            textx += tableWidth / (float) cols;
        }

        // Escribir datos
        float dataY = yStart - 20;
        for (List<String> row : data) {
            float dataX = margin + 4;
            for (String cell : row) {
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(dataX, dataY);
                contentStream.showText(cell);
                contentStream.endText();
                dataX += tableWidth / (float) cols;
            }
            dataY -= rowHeight;
        }
*/
        contentStream.close();

        // Guardar el documento PDF
        //File file = new File(nameFile);
        return document;
        //Desktop.getDesktop().open(new File(nameFile));
    }

    private List<List<String>> getDataFromHashMap(ItemStatistics statistics) {
        var statisticsHashMap= statistics.getStatistics();
        var listNames= statistics.getNameStatistic();
        List<List<String>> result=new ArrayList<>();
        for (var i=0;i<statisticsHashMap.size();i++) {
            List<String> listFormattedStatistics= new ArrayList<>();
            listFormattedStatistics.add(listNames.get(i));
            for (Map.Entry<String, Double> numberStatistic : statisticsHashMap.get(i).entrySet()) {
                var number=(double) Math.round(numberStatistic.getValue() * 100d) / 100d;
                listFormattedStatistics.add(String.valueOf(number));
            }
            result.add(listFormattedStatistics);
        }
        return result;
    }
}
