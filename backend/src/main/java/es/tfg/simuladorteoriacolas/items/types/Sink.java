package es.tfg.simuladorteoriacolas.items.types;

import es.tfg.simuladorteoriacolas.items.Item;
import jakarta.persistence.*;

@Entity
public class Sink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idSink;

    private Integer inSink;

    @OneToOne
    private Item item;

    public Sink(){

    }

    public Integer getInSink() {
        return inSink;
    }

    public void setInSink(Integer inSink) {
        this.inSink = inSink;
    }

    public void setIdSink(Integer idSink) {
        this.idSink = idSink;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getIdSink() {
        return idSink;
    }
}
