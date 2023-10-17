package es.tfg.simuladorteoriacolas.items.types;

import es.tfg.simuladorteoriacolas.items.Item;
import jakarta.persistence.*;

@Entity
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idQueue;

    @OneToOne
    private Item item;

    private String capacityQueue;

    private String disciplineQueue;

    private Integer inQueue;

    private Integer outQueue;

    private Double maxStays;

    private Double totalStaysTime;

    private Integer totalProducts;

    private Integer totalInQueue;

    private Integer maxContent;

    private Double avgStayTime;

    private Double avgContent;

    private Integer lastSizeContent;

    private Double lastTimeCheckedContent;

    private Double timeMultipliedByContent;

    public Queue(){

    }

    public Integer getOutQueue() {
        return outQueue;
    }

    public void setOutQueue(Integer outQueue) {
        this.outQueue = outQueue;
    }

    public Integer getInQueue() {
        return inQueue;
    }

    public void setInQueue(Integer inQueue) {
        this.inQueue = inQueue;
    }

    public String getCapacityQueue() {
        return capacityQueue;
    }

    public void setCapacityQueue(String capacityQueue) {
        this.capacityQueue = capacityQueue;
    }

    public String getDisciplineQueue() {
        return disciplineQueue;
    }

    public void setDisciplineQueue(String disciplineQueue) {
        this.disciplineQueue = disciplineQueue;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getIdQueue() {
        return idQueue;
    }

    public Double getMaxStays() {
        return maxStays;
    }

    public void setMaxStays(Double maxStays) {
        this.maxStays = maxStays;
    }

    public Double getTotalStaysTime() {
        return totalStaysTime;
    }

    public void setTotalStaysTime(Double totalStaysTime) {
        this.totalStaysTime = totalStaysTime;
    }

    public Integer getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Integer totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Integer getTotalInQueue() {
        return totalInQueue;
    }

    public void setTotalInQueue(Integer totalInQueue) {
        this.totalInQueue = totalInQueue;
    }

    public Integer getMaxContent() {
        return maxContent;
    }

    public void setMaxContent(Integer maxContent) {
        this.maxContent = maxContent;
    }

    public Double getAvgStayTime() {
        return avgStayTime;
    }

    public void setAvgStayTime(Double avgStayTime) {
        this.avgStayTime = avgStayTime;
    }

    public Double getAvgContent() {
        return avgContent;
    }

    public void setAvgContent(Double avgContent) {
        this.avgContent = avgContent;
    }

    public Integer getLastSizeContent() {
        return lastSizeContent;
    }

    public void setLastSizeContent(Integer lastSizeContent) {
        this.lastSizeContent = lastSizeContent;
    }

    public Double getLastTimeCheckedContent() {
        return lastTimeCheckedContent;
    }

    public void setLastTimeCheckedContent(Double lastTimeCheckedContent) {
        this.lastTimeCheckedContent = lastTimeCheckedContent;
    }

    public Double getTimeMultipliedByContent() {
        return timeMultipliedByContent;
    }

    public void setTimeMultipliedByContent(Double timeMultipliedByContent) {
        this.timeMultipliedByContent = timeMultipliedByContent;
    }
}
