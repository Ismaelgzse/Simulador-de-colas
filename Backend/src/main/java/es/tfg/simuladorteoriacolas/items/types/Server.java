package es.tfg.simuladorteoriacolas.items.types;

import es.tfg.simuladorteoriacolas.items.Item;
import jakarta.persistence.*;

@Entity
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idServer;

    private String setupTime;

    private String cicleTime;

    private Integer outServer;

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

    public Integer getOutServer() {
        return outServer;
    }

    public void setOutServer(Integer outServer) {
        this.outServer = outServer;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getIdServer() {
        return idServer;
    }
}
