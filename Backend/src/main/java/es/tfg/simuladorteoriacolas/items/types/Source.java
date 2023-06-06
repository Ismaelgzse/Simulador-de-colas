package es.tfg.simuladorteoriacolas.items.types;

import es.tfg.simuladorteoriacolas.items.Item;
import jakarta.persistence.*;

@Entity
public class Source {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idSource;

    @OneToOne
    private Item item;

    private String interArrivalTime;

    private String numberProducts;

    private Integer outSource;

    public Source(){

    }

    public Integer getOutSource() {
        return outSource;
    }

    public void setOutSource(Integer outSource) {
        this.outSource = outSource;
    }

    public String getInterArrivalTime() {
        return interArrivalTime;
    }

    public void setInterArrivalTime(String interArrivalTime) {
        this.interArrivalTime = interArrivalTime;
    }

    public String getNumberProducts() {
        return numberProducts;
    }

    public void setNumberProducts(String numberProducts) {
        this.numberProducts = numberProducts;
    }
}
