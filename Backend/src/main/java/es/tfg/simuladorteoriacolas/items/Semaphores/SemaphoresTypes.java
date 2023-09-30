package es.tfg.simuladorteoriacolas.items.Semaphores;

import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;

public class SemaphoresTypes {

    private List<Integer> idDestinationItem;
    private String type;
    private List<Semaphore> semaphores;
    private List<Exchanger> exchangers;

    public List<Exchanger> getExchangers() {
        return exchangers;
    }

    public void setExchangers(List<Exchanger> exchangers) {
        this.exchangers = exchangers;
    }

    public String getType() {
        return type;
    }

    public List<Semaphore> getSemaphores() {
        return semaphores;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSemaphores(List<Semaphore> semaphores) {
        this.semaphores = semaphores;
    }

    public List<Integer> getIdDestinationItem() {
        return idDestinationItem;
    }

    public void setIdDestinationItem(List<Integer> idDestinationItem) {
        this.idDestinationItem = idDestinationItem;
    }

}
