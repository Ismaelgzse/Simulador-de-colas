package es.tfg.simuladorteoriacolas.items.Semaphores;

import java.util.concurrent.Semaphore;

public class SmallestQueueDecision {
    private Semaphore controlDestinationSemaphore;

    private String typeItem;

    private Integer identifier;

    public Semaphore getControlDestinationSemaphore() {
        return controlDestinationSemaphore;
    }

    public void setControlDestinationSemaphore(Semaphore controlDestinationSemaphore) {
        this.controlDestinationSemaphore = controlDestinationSemaphore;
    }

    public String getTypeItem() {
        return typeItem;
    }

    public void setTypeItem(String typeItem) {
        this.typeItem = typeItem;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }
}
