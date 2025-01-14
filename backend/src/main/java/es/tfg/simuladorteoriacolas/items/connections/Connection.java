package es.tfg.simuladorteoriacolas.items.connections;

import es.tfg.simuladorteoriacolas.items.Item;
import jakarta.persistence.*;

@Entity
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idConnect;

    private Double percentage;

    @ManyToOne
    private Item originItem;

    @ManyToOne
    private Item destinationItem;

    public Connection(){

    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public void setOriginItem(Item originItem) {
        this.originItem = originItem;
    }

    public Item getOriginItem() {
        return originItem;
    }

    public Item getDestinationItem() {
        return destinationItem;
    }

    public void setDestinationItem(Item destinationItem) {
        this.destinationItem = destinationItem;
    }

    public Integer getIdConnect() {
        return idConnect;
    }
}
