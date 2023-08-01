package es.tfg.simuladorteoriacolas.webSocket.controller;

import es.tfg.simuladorteoriacolas.webSocket.configuration.ServerStatusTask;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Controller
public class WebSocketController {

    private final ServerStatusTask serverStatusTask;

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    private final TaskScheduler taskScheduler;

    private final ConcurrentHashMap<String, ScheduledFuture<?>> currentScheduledTasks = new ConcurrentHashMap<>();


    public WebSocketController(ServerStatusTask serverStatusTask, SimpMessageSendingOperations simpMessageSendingOperations, TaskScheduler taskScheduler) {
        this.serverStatusTask = serverStatusTask;
        this.simpMessageSendingOperations = simpMessageSendingOperations;
        this.taskScheduler = taskScheduler;
    }

    @MessageMapping("/destination/{itemId}")
    @SendTo("/itemInfo/{itemId}")
    public void handler(@DestinationVariable String itemId, String activateTask) {
        scheduleServerStatusMessage(itemId);
    }

    private void scheduleServerStatusMessage(String simulationId) {
        ScheduledFuture<?> existingTask = currentScheduledTasks.get(simulationId);
        if (existingTask != null) {
            existingTask.cancel(true);
        }
        ScheduledFuture<?> newTask = taskScheduler.scheduleAtFixedRate(() -> serverStatusTask.scheduled(simulationId), 5000);
        currentScheduledTasks.put(simulationId, newTask);
    }

}
