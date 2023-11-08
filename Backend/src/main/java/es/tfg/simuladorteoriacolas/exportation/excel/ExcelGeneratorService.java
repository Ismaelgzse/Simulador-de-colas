package es.tfg.simuladorteoriacolas.exportation.excel;

import es.tfg.simuladorteoriacolas.exportation.ItemStatistics;
import es.tfg.simuladorteoriacolas.exportation.StatisticFormat;
import es.tfg.simuladorteoriacolas.items.ItemDTO;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ExcelGeneratorService {
    public Workbook generateExcel(List<List<ItemDTO>> simulations, String nameFile) throws IOException {

        //Gets the statistics of the simulation formatted
        List<ItemStatistics> itemStatisticsList = StatisticFormat.formatListSimulations(simulations);

        //Creates a new workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(nameFile);

        //Creates the style for the name of the item
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        headerStyle.setFont(font);

        //Sets some parameters of the sheet
        sheet.setColumnWidth(0, 9000);
        sheet.setColumnWidth(1, 4000);

        Integer contRows=0;
        for (ItemStatistics itemStatistics : itemStatisticsList) {
            Integer contCell=0;
            Row header = sheet.createRow(contRows);

            //Sets the name of the item with the style
            Cell headerCell = header.createCell(contCell);
            headerCell.setCellValue(itemStatistics.getNameItem());
            headerCell.setCellStyle(headerStyle);

            contCell++;
            //Sets the other headers of the statistics
            for (String headerString:itemStatistics.getStatistics().get(0).keySet()) {
                headerCell = header.createCell(contCell);
                headerCell.setCellValue(headerString);

                contCell++;
            }

            contRows++;

            //Sets the data of the item
            List<List<String>> data= getDataFromHashMap(itemStatistics);
            for (List<String> rowData : data) {
                Row row = sheet.createRow(contRows);
                contCell=0;
                for (String cellData : rowData) {
                    Cell cell = row.createCell(contCell);
                    cell.setCellValue(cellData);
                    contCell++;
                }
                contRows++;
            }
            contRows++;

        }

        return workbook;
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
