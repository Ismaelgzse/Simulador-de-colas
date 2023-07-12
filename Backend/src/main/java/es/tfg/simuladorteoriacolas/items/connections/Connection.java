package es.tfg.simuladorteoriacolas.items.connections;

import es.tfg.simuladorteoriacolas.items.Item;
import jakarta.persistence.*;

@Entity
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idConnection;

    private Integer percentage;

    @ManyToOne
    private Item originItem;

    @ManyToOne
    private Item destinationItem;

    public Connection(){

    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
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
}
