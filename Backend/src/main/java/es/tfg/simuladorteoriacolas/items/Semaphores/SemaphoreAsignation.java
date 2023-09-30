package es.tfg.simuladorteoriacolas.items.Semaphores;

import org.w3c.dom.ls.LSException;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

public class SemaphoreAsignation {

    private Integer idOriginItem;

    private List<SemaphoresTypes> semaphoresTypes;

    private List<SmallestQueueDecision> smallestQueueDecisions;

    public SemaphoreAsignation() {
    }

    public Integer getIdOriginItem() {
        return idOriginItem;
    }


    public void setIdOriginItem(Integer idOriginItem) {
        this.idOriginItem = idOriginItem;
    }

    public List<SemaphoresTypes> getSemaphoresTypes() {
        return semaphoresTypes;
    }

    public void setSemaphoresTypes(List<SemaphoresTypes> semaphoresTypes) {
        this.semaphoresTypes = semaphoresTypes;
    }

    public List<SmallestQueueDecision> getSmallestQueueDecisions() {
        return smallestQueueDecisions;
    }

    public void setSmallestQueueDecisions(List<SmallestQueueDecision> smallestQueueDecisions) {
        this.smallestQueueDecisions = smallestQueueDecisions;
    }
}
