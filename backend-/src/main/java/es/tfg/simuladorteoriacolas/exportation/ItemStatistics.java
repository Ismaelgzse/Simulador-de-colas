package es.tfg.simuladorteoriacolas.exportation;

import es.tfg.simuladorteoriacolas.items.ItemDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemStatistics {

    private String nameItem;

    private List<String> nameStatisticList;

    private List<HashMap<String,Double>> statistics;

    public ItemStatistics(){

    }

    public List<String> getNameStatistic() {
        return nameStatisticList;
    }

    public void setNameStatistic(List<String> nameStatisticList) {
        this.nameStatisticList = nameStatisticList;
    }

    public String getNameItem() {
        return nameItem;
    }

    public void setNameItem(String nameItem) {
        this.nameItem = nameItem;
    }

    public List<HashMap<String, Double>> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<HashMap<String, Double>> statistics) {
        this.statistics = statistics;
    }
}
