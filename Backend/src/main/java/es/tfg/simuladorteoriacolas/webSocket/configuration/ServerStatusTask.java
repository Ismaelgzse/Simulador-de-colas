package es.tfg.simuladorteoriacolas.webSocket.configuration;

import es.tfg.simuladorteoriacolas.items.ItemDTO;
import es.tfg.simuladorteoriacolas.items.ItemService;
import es.tfg.simuladorteoriacolas.items.connections.Connection;
import es.tfg.simuladorteoriacolas.items.types.ItemTypesService;
import es.tfg.simuladorteoriacolas.simulation.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class ServerStatusTask {

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @Autowired
    private SimulationService simulationService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemTypesService itemTypesService;

    @Autowired
    public ServerStatusTask(SimpMessageSendingOperations simpMessagingTemplate) {
        this.simpMessageSendingOperations = simpMessagingTemplate;
    }

    public void scheduled(String simulationId){
        var simulation=simulationService.findById(Integer.valueOf(simulationId)).orElseThrow();
        List<ItemDTO> itemDTOList= new ArrayList<>();
        var itemList = itemService.findAllBySimulation(simulation);
        for (var i = 0; i < itemList.size(); i++) {
            var itemDTO = new ItemDTO();
            switch (itemList.get(i).getDescription()) {
                case "Queue" -> itemDTO.setQueue(itemTypesService.findQueueByItem(itemList.get(i)));
                case "Server" -> itemDTO.setServer(itemTypesService.findServerByItem(itemList.get(i)));
                case "Sink" -> itemDTO.setSink(itemTypesService.findSinkByItem(itemList.get(i)));
                case "Source" -> itemDTO.setSource(itemTypesService.findSourceByItem(itemList.get(i)));
            }
            itemDTOList.add(itemDTO);
        }

        simpMessageSendingOperations.convertAndSend("/simulationInfo/"+simulationId,itemDTOList);
    }

}
