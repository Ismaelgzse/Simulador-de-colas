package es.tfg.simuladorteoriacolas.items.types;

import es.tfg.simuladorteoriacolas.items.Item;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;

@Entity
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idServer;

    private String setupTime;

    private String cicleTime;

    private Integer out;

    @OneToOne
    private Item item;

    public Server(){

    }

    public String getCicleTime() {
        return cicleTime;
    }

    public void setCicleTime(String cicleTime) {
        this.cicleTime = cicleTime;
    }

    public String getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(String setupTime) {
        this.setupTime = setupTime;
    }

    public Integer getOut() {
        return out;
    }

    public void setOut(Integer out) {
        this.out = out;
    }
}