package es.tfg.simuladorteoriacolas.webSocket.controller;

import es.tfg.simuladorteoriacolas.simulation.SimulationService;
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

    @MessageMapping("/simulateMessage/{simulationId}")
    @SendTo("/simulationInfo/{simulatonId}")
    public void handler(@DestinationVariable String simulationId, String activateTask) {

        //TODO provisional
        var simulation = simulationService.findById(Integer.valueOf(simulationId)).get();
        if (simulation.getStatusSimulation().equals("0")) {
            simulation.setStatusSimulation("1");
            simulationService.save(simulation);
            simulationService.simulate(Integer.valueOf(simulationId));
        }
        else {
            simulation.setStatusSimulation("0");
            simulationService.save(simulation);

        }
    }

}
