package es.tfg.simuladorteoriacolas.exportation.pdf;

import es.tfg.simuladorteoriacolas.exportation.ItemStatistics;
import es.tfg.simuladorteoriacolas.exportation.StatisticFormat;
import es.tfg.simuladorteoriacolas.items.ItemDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.awt.*;
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

        List<ItemStatistics> itemStatisticsList = StatisticFormat.formatListSimulations(simulations);

        int rows;
        int cols = 5;
        for (ItemStatistics itemStatistics : itemStatisticsList) {
            if (yStart<200){
                contentStream.close();
                page = new PDPage();
                document.addPage(page);

                contentStream = new PDPageContentStream(document, page);
                yStart = page.getMediaBox().getHeight() - margin;
                yPosition = yStart;
            }
            rows = itemStatistics.getNameStatistic().size()+1;

            float rowHeight = 20f;
            float tableHeight = rowHeight * (float) rows;
            float tableYBottom = yStart - tableHeight;

            float y = yStart;

            float cellWidth = tableWidth / (float) cols;
            float cellHeight = rowHeight;
            contentStream.setNonStrokingColor(new Color(255, 165, 0));
            contentStream.addRect(margin, yStart - rowHeight, cellWidth, cellHeight);
            contentStream.fill();
            contentStream.setNonStrokingColor(Color.BLACK);

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
