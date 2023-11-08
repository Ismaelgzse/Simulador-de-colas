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

    public PDDocument generatePdf(List<List<ItemDTO>> simulations) throws IOException {
        //Creates a pdf document and a page
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        //Sets the styles and margins of the document
        float margin = 50;
        float yStart = page.getMediaBox().getHeight() - margin;
        float tableWidth = page.getMediaBox().getWidth() - 2 * margin;

        //Gets the statistics of the simulation formatted
        List<ItemStatistics> itemStatisticsList = StatisticFormat.formatListSimulations(simulations);

        int rows;
        int cols = 5;
        for (ItemStatistics itemStatistics : itemStatisticsList) {
            //If there is no space left on the page, create another page and continue writing on it
            if (yStart<200){
                contentStream.close();
                page = new PDPage();
                document.addPage(page);

                contentStream = new PDPageContentStream(document, page);
                yStart = page.getMediaBox().getHeight() - margin;
            }

            rows = itemStatistics.getNameStatistic().size()+1;

            float rowHeight = 20f;
            float tableHeight = rowHeight * (float) rows;
            float tableBottom = yStart - tableHeight;

            float y = yStart;

            float columnWidth = tableWidth / (float) cols;
            float cellHeight = rowHeight;

            //Sets the style of the name of the item
            contentStream.setNonStrokingColor(new Color(255, 165, 0));
            contentStream.addRect(margin, yStart - rowHeight, columnWidth, cellHeight);
            contentStream.fill();
            contentStream.setNonStrokingColor(Color.BLACK);

            //Ads the horizontal lines of the tables
            for (int i = 0; i <= rows; i++) {
                contentStream.moveTo(margin, y);
                contentStream.lineTo(margin + tableWidth, y);
                contentStream.stroke();
                y -= rowHeight;
            }

            float x = margin;
            //Ads the vertical lines of the tables
            for (int i = 0; i <= cols; i++) {
                contentStream.moveTo(x, yStart);
                contentStream.lineTo(x, tableBottom);
                contentStream.stroke();
                x += tableWidth / (float) cols;
            }

            //Sets the properties of the text inside the tables
            x = margin + 4;
            y = yStart - 15;

            //TODO Verificar si se deberia aprovechar a escribir aquí el nombre del item
            //Starts with the header of the table
            //The firts header corresponds to the name of the item
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(x, y);
            contentStream.showText(itemStatistics.getNameItem());
            contentStream.endText();
            x += tableWidth / (float) cols;
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 7);
            //The rest of the headers
            for (String header : itemStatistics.getStatistics().get(0).keySet()) {
                contentStream.beginText();
                contentStream.newLineAtOffset(x, y);
                contentStream.showText(header);
                contentStream.endText();
                x += tableWidth / (float) cols;
            }
            contentStream.setFont(PDType1Font.HELVETICA, 6);

            List<List<String>> data= getDataFromHashMap(itemStatistics);
            float dataY = yStart - 32.5f;
            //Writes the content of the table
            for (List<String> row : data) {
                float dataX = margin + 4;
                for (String cell : row) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(dataX, dataY);
                    contentStream.showText(cell);
                    contentStream.endText();
                    dataX += tableWidth / (float) cols;
                }
                dataY -= rowHeight;
            }

            yStart= tableBottom - 40f;

        }
        contentStream.close();

        return document;
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
