package es.tfg.simuladorteoriacolas.items.types;

import es.tfg.simuladorteoriacolas.items.Item;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;

@Entity
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idQueue;

    @OneToOne
    private Item item;

    private String capacity;

    private String discipline;

    private Integer in;

    private Integer out;

    public Queue(){

    }

    public Integer getOut() {
        return out;
    }

    public void setOut(Integer out) {
        this.out = out;
    }

    public Integer getIn() {
        return in;
    }

    public void setIn(Integer in) {
        this.in = in;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }
}
