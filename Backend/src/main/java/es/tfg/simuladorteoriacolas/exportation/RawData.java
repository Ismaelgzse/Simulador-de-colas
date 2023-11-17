package es.tfg.simuladorteoriacolas.exportation;

import java.util.List;

public class RawData {
    private String nameItem;

    private List<String> nameStatisticList;

    private List<List<String>> rawDataFromSimulations;

    public RawData(String nameItem, List<String> nameStatisticList, List<List<String>> rawDataFromSimulations){
        this.nameItem=nameItem;
        this.nameStatisticList=nameStatisticList;
        this.rawDataFromSimulations=rawDataFromSimulations;
    }

    public RawData(){

    }

    public String getNameItem() {
        return nameItem;
    }

    public void setNameItem(String nameItem) {
        this.nameItem = nameItem;
    }

    public List<String> getNameStatisticList() {
        return nameStatisticList;
    }

    public void setNameStatisticList(List<String> nameStatisticList) {
        this.nameStatisticList = nameStatisticList;
    }

    public List<List<String>> getRawDataFromSimulations() {
        return rawDataFromSimulations;
    }

    public void setRawDataFromSimulations(List<List<String>> rawDataFromSimulations) {
        this.rawDataFromSimulations = rawDataFromSimulations;
    }
}
