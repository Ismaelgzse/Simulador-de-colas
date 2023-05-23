package es.tfg.simuladorteoriacolas.items;

import es.tfg.simuladorteoriacolas.simulation.Simulation;
import jakarta.persistence.*;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idItem;

    private String name;

    private Integer positionX;

    private Integer positionY;

    private String description;

    @ManyToOne
    private Simulation idSimulation;

    @ManyToOne
    private Item connectedItem;

    private Integer connectedPossitionX;

    private Integer connectedPossitionY;

    public Item(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getConnectedPossitionX() {
        return connectedPossitionX;
    }

    public Integer getConnectedPossitionY() {
        return connectedPossitionY;
    }

    public void setConnectedPossitionX(Integer connectedPossitionX) {
        this.connectedPossitionX = connectedPossitionX;
    }

    public void setConnectedPossitionY(Integer connectedPossitionY) {
        this.connectedPossitionY = connectedPossitionY;
    }

    public Integer getPositionX() {
        return positionX;
    }

    public Integer getPositionY() {
        return positionY;
    }

    public void setPositionX(Integer positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(Integer positionY) {
        this.positionY = positionY;
    }
}
