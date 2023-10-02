package es.tfg.simuladorteoriacolas.webSocket.controller;

import es.tfg.simuladorteoriacolas.simulation.SimulationService;
import es.tfg.simuladorteoriacolas.webSocket.configuration.ServerStatusTask;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Controller
public class WebSocketController {

    @Autowired
    private SimulationService simulationService;

    private final ServerStatusTask serverStatusTask;

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    private final TaskScheduler taskScheduler;

    private final ConcurrentHashMap<String, ScheduledFuture<?>> currentScheduledTasks = new ConcurrentHashMap<>();


    public WebSocketController(ServerStatusTask serverStatusTask, SimpMessageSendingOperations simpMessageSendingOperations, TaskScheduler taskScheduler) {
        this.serverStatusTask = serverStatusTask;
        this.simpMessageSendingOperations = simpMessageSendingOperations;
        this.taskScheduler = taskScheduler;
    }

    @MessageMapping("/simulateMessage/{simulationId}")
    @SendTo("/simulationInfo/{simulatonId}")
    public void handler(@DestinationVariable String simulationId, String activateTask) {
        scheduleServerStatusMessage(simulationId);
    }


    private void scheduleServerStatusMessage(String simulationId) {
        var simulation = simulationService.findById(Integer.valueOf(simulationId)).get();
        ScheduledFuture<?> existingTask = currentScheduledTasks.get(simulationId);
        if (existingTask != null) {
            simulation.setStatusSimulation("0");
            simulationService.save(simulation);
            existingTask.cancel(true);
            currentScheduledTasks.remove(simulationId);
        } else {
            if (simulation.getStatusSimulation().equals("0")) {
                simulation.setStatusSimulation("1");
                simulationService.save(simulation);
                ScheduledFuture<?> newTask = taskScheduler.scheduleAtFixedRate(() -> serverStatusTask.scheduled(simulationId), 5000);
                currentScheduledTasks.put(simulationId, newTask);
            }
        }
    }

}
