package es.tfg.simuladorteoriacolas.items;

import es.tfg.simuladorteoriacolas.simulation.Simulation;
import jakarta.persistence.*;

import java.util.List;

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

    public void setIdSimulation(Simulation idSimulation) {
        this.idSimulation = idSimulation;
    }

    public void setIdItem(Integer idItem) {
        this.idItem = idItem;
    }


    public Integer getIdItem() {
        return idItem;
    }
}
