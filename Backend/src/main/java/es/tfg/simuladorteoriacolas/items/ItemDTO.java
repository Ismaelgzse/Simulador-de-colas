package es.tfg.simuladorteoriacolas.items;

import es.tfg.simuladorteoriacolas.items.types.Queue;
import es.tfg.simuladorteoriacolas.items.types.Server;
import es.tfg.simuladorteoriacolas.items.types.Sink;
import es.tfg.simuladorteoriacolas.items.types.Source;
import es.tfg.simuladorteoriacolas.simulation.Simulation;

import java.util.List;

public class ItemDTO {
    private Item item;

    /*
    private Integer idItem;

    private String name;

    private Integer positionX;

    private Integer positionY;

    private String description;

    private Simulation idSimulation;

    private List<Item> connectedItem;

    private Integer connectedPossitionX;

    private Integer connectedPossitionY;

     */

    private Queue queue;

    private Server server;

    private Sink sink;

    private Source source;
/*
    public String getName() {
        return name;
    }

    public Integer getPositionY() {
        return positionY;
    }

    public Integer getPositionX() {
        return positionX;
    }

    public Integer getConnectedPositionY() {
        return connectedPossitionY;
    }

    public Integer getConnectedPositionX() {
        return connectedPossitionX;
    }

    public String getDescription() {
        return description;
    }

    public Integer getIdItem() {
        return idItem;
    }

    public List<Item> getConnectedItem() {
        return connectedItem;
    }

    public Simulation getIdSimulation() {
        return idSimulation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPositionY(Integer positionY) {
        this.positionY = positionY;
    }

    public void setPositionX(Integer positionX) {
        this.positionX = positionX;
    }

    public void setConnectedPositionY(Integer connectedPossitionY) {
        this.connectedPossitionY = connectedPossitionY;
    }

    public void setConnectedPositionX(Integer connectedPossitionX) {
        this.connectedPossitionX = connectedPossitionX;
    }

    public void setConnectedItem(List<Item> connectedItem) {
        this.connectedItem = connectedItem;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIdItem(Integer idItem) {
        this.idItem = idItem;
    }

    public void setIdSimulation(Simulation idSimulation) {
        this.idSimulation = idSimulation;
    }

 */
    public Item getItem(){
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Sink getSink() {
        return sink;
    }

    public void setSink(Sink sink) {
        this.sink = sink;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }
}
