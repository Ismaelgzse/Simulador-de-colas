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

    private Double totalBusy;

    private Double pctBusyTime;

    private Double totalIdle;

    private Double lastTimeIdle;

    private Double lastTimeBusy;

    private Integer idleOrBusy;

    private Integer inServer;

    private Double maxStays;

    private Double totalStaysTime;

    private Double avgStayTime;

    public Integer getInServer() {
        return inServer;
    }

    public void setInServer(Integer inServer) {
        this.inServer = inServer;
    }

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

    public Double getTotalBusy() {
        return totalBusy;
    }

    public void setTotalBusy(Double totalBusy) {
        this.totalBusy = totalBusy;
    }

    public Double getPctBusyTime() {
        return pctBusyTime;
    }

    public void setPctBusyTime(Double pctBusyTime) {
        this.pctBusyTime = pctBusyTime;
    }

    public Double getTotalIdle() {
        return totalIdle;
    }

    public void setTotalIdle(Double totalIdle) {
        this.totalIdle = totalIdle;
    }

    public Double getLastTimeIdle() {
        return lastTimeIdle;
    }

    public void setLastTimeIdle(Double lastTimeIdle) {
        this.lastTimeIdle = lastTimeIdle;
    }

    public Double getLastTimeBusy() {
        return lastTimeBusy;
    }

    public void setLastTimeBusy(Double lastTimeBusy) {
        this.lastTimeBusy = lastTimeBusy;
    }

    public Integer getIdleOrBusy() {
        return idleOrBusy;
    }

    public void setIdleOrBusy(Integer idleOrBusy) {
        this.idleOrBusy = idleOrBusy;
    }

    public Double getMaxStays() {
        return maxStays;
    }

    public void setMaxStays(Double maxStays) {
        this.maxStays = maxStays;
    }

    public Double getTotalStaysTime() {
        return totalStaysTime;
    }

    public void setTotalStaysTime(Double totalStaysTime) {
        this.totalStaysTime = totalStaysTime;
    }

    public Double getAvgStayTime() {
        return avgStayTime;
    }

    public void setAvgStayTime(Double avgStayTime) {
        this.avgStayTime = avgStayTime;
    }
}
