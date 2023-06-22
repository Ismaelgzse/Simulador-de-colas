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
}
