package es.tfg.simuladorteoriacolas.exportation.excel;

import es.tfg.simuladorteoriacolas.exportation.ItemStatistics;
import es.tfg.simuladorteoriacolas.exportation.RawData;
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
import java.util.stream.Stream;

@Service
public class ExcelGeneratorService {
    public Workbook generateExcel(List<List<ItemDTO>> simulations, String nameFile) throws IOException {

        //Gets the statistics of the simulation formatted
        List<ItemStatistics> itemStatisticsList = StatisticFormat.formatListSimulations(simulations);

        //Creates a new workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet statisticsSimulationsSheet = workbook.createSheet("Estadísticas simulaciones");

        //Creates the style for the name of the item
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        headerStyle.setFont(font);

        //Sets some parameters of the sheet
        statisticsSimulationsSheet.setColumnWidth(0, 9000);
        statisticsSimulationsSheet.setColumnWidth(1, 4000);

        Integer contRows=0;
        for (ItemStatistics itemStatistics : itemStatisticsList) {
            Integer contCell=0;
            Row header = statisticsSimulationsSheet.createRow(contRows);

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
                Row row = statisticsSimulationsSheet.createRow(contRows);
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

        //Creates the sheet for the raw data of the simulations
        Sheet rawDataSheet = workbook.createSheet("Datos brutos (Raw data)");

        rawDataSheet.setColumnWidth(0, 9000);
        for (var i=0;i<simulations.size();i++){
            rawDataSheet.setColumnWidth(i+1, 4000);

        }

        List<RawData> rawDataList= StatisticFormat.formatRawData(simulations);

        contRows=0;
        for (var i=0;i<rawDataList.size();i++) {
            Integer contCell=0;
            Row header = rawDataSheet.createRow(contRows);

            //Sets the name of the item with the style
            Cell headerCell = header.createCell(contCell);
            headerCell.setCellValue(rawDataList.get(i).getNameItem());
            headerCell.setCellStyle(headerStyle);

            contCell++;
            //Sets the other headers of the statistics
            for (var j=0;j<simulations.size();j++) {
                headerCell = header.createCell(contCell);
                headerCell.setCellValue("Simulación "+ (j+1));

                contCell++;
            }

            contRows++;

            //Sets the data of the item
            List<List<String>> data= getDataFromLists(rawDataList.get(i).getNameStatisticList(),rawDataList.get(i).getRawDataFromSimulations());
            for (List<String> rowData : data) {
                Row row = rawDataSheet.createRow(contRows);
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

    private List<List<String>> getDataFromLists(List<String> nameStatisticsList, List<List<String>> rawDataLists){
        List<List<String>> formattedLists= new ArrayList<>();
        for (var i=0;i<nameStatisticsList.size();i++){
            List<String> rowOfRawData= new ArrayList<>();
            rowOfRawData.add(nameStatisticsList.get(i));
            rowOfRawData = Stream.concat(rowOfRawData.stream(), rawDataLists.get(i).stream()).toList();
            formattedLists.add(rowOfRawData);
        }
        return formattedLists;
    }

}
