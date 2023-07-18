package es.tfg.simuladorteoriacolas.items;

import es.tfg.simuladorteoriacolas.items.connections.Connection;
import es.tfg.simuladorteoriacolas.items.connections.ConnectionService;
import es.tfg.simuladorteoriacolas.items.types.*;
import es.tfg.simuladorteoriacolas.items.types.Queue;
import es.tfg.simuladorteoriacolas.simulation.SimulationService;
import es.tfg.simuladorteoriacolas.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ItemAPIController {

    @Autowired
    private SimulationService simulationService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemTypesService itemTypesService;

    @Autowired
    private ConnectionService connectionService;

    @GetMapping("/simulations/{idSimulation}/item/{idItem}")
    public ResponseEntity<ItemDTO> getItem(@PathVariable Integer idSimulation,
                                           @PathVariable Integer idItem,
                                           HttpServletRequest request) {
        var userName = request.getUserPrincipal().getName();
        var simulation = simulationService.findById(idSimulation).orElseThrow();
        if (userName.equals(simulation.getUserCreator().getNickname())) {
            var item = itemService.findById(idItem).orElseThrow();
            ItemDTO itemDTO = new ItemDTO();
            itemDTO.setItem(item);
            switch (item.getDescription()) {
                case "Queue" -> itemDTO.setQueue(itemTypesService.findQueueByItem(item));
                case "Server" -> itemDTO.setServer(itemTypesService.findServerByItem(item));
                case "Sink" -> itemDTO.setSink(itemTypesService.findSinkByItem(item));
                case "Source" -> itemDTO.setSource(itemTypesService.findSourceByItem(item));
            }
            return ResponseEntity.ok(itemDTO);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/simulations/{idSimulation}/items")
    public ResponseEntity<List<ItemDTO>> getItems(@PathVariable Integer idSimulation,
                                                  HttpServletRequest request) {
        var userName = request.getUserPrincipal().getName();
        var simulation = simulationService.findById(idSimulation).orElseThrow();
        List<ItemDTO> itemDTOList = new ArrayList<>();
        if (userName.equals(simulation.getUserCreator().getNickname())) {
            var itemList = itemService.findAllBySimulation(simulation);
            for (var i = 0; i < itemList.size(); i++) {
                var itemDTO = new ItemDTO();
                switch (itemList.get(i).getDescription()) {
                    case "Queue" -> itemDTO.setQueue(itemTypesService.findQueueByItem(itemList.get(i)));
                    case "Server" -> itemDTO.setServer(itemTypesService.findServerByItem(itemList.get(i)));
                    case "Sink" -> itemDTO.setSink(itemTypesService.findSinkByItem(itemList.get(i)));
                    case "Source" -> itemDTO.setSource(itemTypesService.findSourceByItem(itemList.get(i)));
                }
                //
                List<Connection> connections= connectionService.findAllByOriginItem(itemList.get(i));
                itemDTO.setConnections(connections);
                itemDTO.setItem(itemList.get(i));
                itemDTOList.add(itemDTO);
            }
            return ResponseEntity.ok(itemDTOList);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/simulations/{idSimulation}/item/all")
    public ResponseEntity<List<ItemDTO>> updateAll(@PathVariable Integer idSimulation,
                                                   HttpServletRequest request,
                                                   @RequestBody List<ItemDTO> listItemDTO) {
        var userName = request.getUserPrincipal().getName();
        var simulation = simulationService.findById(idSimulation).orElseThrow();
        if (userName.equals(simulation.getUserCreator().getNickname())) {
            List<ItemDTO> savedListItems = new ArrayList<>();
            for (ItemDTO itemDTO : listItemDTO) {
                ItemDTO savedItemDTO = new ItemDTO();
                var itemFromRequest = itemDTO.getItem();
                var savedItem = itemService.save(itemFromRequest.getIdItem(),
                        itemFromRequest.getName(),
                        itemFromRequest.getPositionX(),
                        itemFromRequest.getPositionY(),
                        itemFromRequest.getDescription(),
                        simulation,
                        itemFromRequest.getSendToStrategy());
                savedItemDTO.setItem(savedItem);
                List<Connection> connections= connectionService.findAllByOriginItem(savedItem);
                savedItemDTO.setConnections(connections);
                switch (savedItem.getDescription()) {
                    case "Queue":
                        var queueFromRequest = itemDTO.getQueue();
                        Queue savedQueue = itemTypesService.save(savedItem,
                                queueFromRequest.getIdQueue(),
                                queueFromRequest);
                        savedItemDTO.setQueue(savedQueue);
                        break;
                    case "Server":
                        var serverFromRequest = itemDTO.getServer();
                        Server savedServer = itemTypesService.save(savedItem,
                                serverFromRequest.getIdServer(),
                                serverFromRequest);
                        savedItemDTO.setServer(savedServer);
                        break;
                    case "Sink":
                        var sinkFromRequest = itemDTO.getSink();
                        Sink savedSink = itemTypesService.save(savedItem,
                                sinkFromRequest.getIdSink(),
                                sinkFromRequest);
                        savedItemDTO.setSink(savedSink);
                        break;
                    case "Source":
                        var sourceFromRequest = itemDTO.getSource();
                        Source savedSource = itemTypesService.save(savedItem,
                                sourceFromRequest.getIdSource(),
                                sourceFromRequest);
                        savedItemDTO.setSource(savedSource);
                        break;
                }
                savedListItems.add(savedItemDTO);
            }
            Collections.reverse(savedListItems);
            return ResponseEntity.ok(savedListItems);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/simulations/{idSimulation}/item/{idItem}")
    public ResponseEntity<ItemDTO> update(@PathVariable Integer idSimulation,
                                          @PathVariable Integer idItem,
                                          HttpServletRequest request,
                                          @RequestBody ItemDTO itemDTO) {
        var userName = request.getUserPrincipal().getName();
        var simulation = simulationService.findById(idSimulation).orElseThrow();
        if (userName.equals(simulation.getUserCreator().getNickname())) {
            if (itemService.findById(idItem).isPresent()) {
                ItemDTO savedItemDTO = new ItemDTO();
                var itemFromRequest = itemDTO.getItem();
                var savedItem = itemService.save(idItem,
                        itemFromRequest.getName(),
                        itemFromRequest.getPositionX(),
                        itemFromRequest.getPositionY(),
                        itemFromRequest.getDescription(),
                        simulation,
                        itemFromRequest.getSendToStrategy());
                savedItemDTO.setItem(savedItem);
                switch (savedItem.getDescription()) {
                    case "Queue":
                        var queueFromRequest = itemDTO.getQueue();
                        Queue savedQueue = itemTypesService.save(savedItem,
                                queueFromRequest.getIdQueue(),
                                queueFromRequest);
                        savedItemDTO.setQueue(savedQueue);
                        break;
                    case "Server":
                        var serverFromRequest = itemDTO.getServer();
                        Server savedServer = itemTypesService.save(savedItem,
                                serverFromRequest.getIdServer(),
                                serverFromRequest);
                        savedItemDTO.setServer(savedServer);
                        break;
                    case "Sink":
                        var sinkFromRequest = itemDTO.getSink();
                        Sink savedSink = itemTypesService.save(savedItem,
                                sinkFromRequest.getIdSink(),
                                sinkFromRequest);
                        savedItemDTO.setSink(savedSink);
                        break;
                    case "Source":
                        var sourceFromRequest = itemDTO.getSource();
                        Source savedSource = itemTypesService.save(savedItem,
                                sourceFromRequest.getIdSource(),
                                sourceFromRequest);
                        savedItemDTO.setSource(savedSource);
                        break;
                }
                return ResponseEntity.ok(savedItemDTO);
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/simulations/{idSimulation}/item/{idItem}")
    public ResponseEntity<ItemDTO> deleteItem(@PathVariable Integer idSimulation,
                                              @PathVariable Integer idItem,
                                              HttpServletRequest request) {
        var userName = request.getUserPrincipal().getName();
        var simulation = simulationService.findById(idSimulation).orElseThrow();
        if (userName.equals(simulation.getUserCreator().getNickname())) {
            Optional<Item> item= itemService.findById(idItem);
            if (item.isPresent()){
                ItemDTO itemDTO=new ItemDTO();
                itemDTO.setItem(item.get());
                switch (item.get().getDescription()) {
                    case "Queue":
                        itemDTO.setQueue(itemTypesService.findQueueByItem(item.get()));
                        break;
                    case "Server":
                        itemDTO.setServer(itemTypesService.findServerByItem(item.get()));
                        break;
                    case "Sink":
                        itemDTO.setSink(itemTypesService.findSinkByItem(item.get()));
                        break;
                    case "Source":
                        itemDTO.setSource(itemTypesService.findSourceByItem(item.get()));
                        break;
                }
                connectionService.deleteAllConnectionsRelatedToAnItem(item.get());
                itemService.deleteById(idItem);
                return ResponseEntity.ok(itemDTO);
            }
            else {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/simulations/{idSimulation}/item")
    public ResponseEntity<ItemDTO> newItem(@PathVariable Integer idSimulation,
                                           @RequestBody ItemDTO itemDTO,
                                           HttpServletRequest request) {
        var userName = request.getUserPrincipal().getName();
        var simulation = simulationService.findById(idSimulation).orElseThrow();
        if (userName.equals(simulation.getUserCreator().getNickname())) {
            var itemFromRequest = itemDTO.getItem();
            Item item = new Item();
            item.setName(itemFromRequest.getName());
            item.setPositionX(itemFromRequest.getPositionX());
            item.setPositionY(itemFromRequest.getPositionY());
            item.setDescription(itemFromRequest.getDescription());
            item.setSendToStrategy(itemFromRequest.getSendToStrategy());
            item.setIdSimulation(simulation);
            var savedItem = itemService.save(item);
            if (savedItem.getName().equals("")) {
                savedItem.setName(savedItem.getDescription() + " " + savedItem.getIdItem().toString());
                savedItem = itemService.save(savedItem);
            }
            var savedItemDTO = new ItemDTO();
            savedItemDTO.setItem(savedItem);
            switch (itemFromRequest.getDescription()) {
                case "Queue":
                    var queueFromRequest = itemDTO.getQueue();
                    Queue queue = new Queue();
                    queue.setItem(savedItem);
                    queue.setCapacityQueue(queueFromRequest.getCapacityQueue());
                    queue.setDisciplineQueue(queueFromRequest.getDisciplineQueue());
                    queue.setInQueue(0);
                    queue.setOutQueue(0);
                    var savedQueue = itemTypesService.save(queue);
                    savedItemDTO.setQueue(savedQueue);
                    break;
                case "Server":
                    var serverFromRequest = itemDTO.getServer();
                    Server server = new Server();
                    server.setSetupTime(serverFromRequest.getSetupTime());
                    server.setCicleTime(serverFromRequest.getCicleTime());
                    server.setOutServer(serverFromRequest.getOutServer());
                    server.setItem(savedItem);
                    var savedServer = itemTypesService.save(server);
                    savedItemDTO.setServer(savedServer);
                    break;
                case "Sink":
                    var sinkFormRequest = itemDTO.getSink();
                    Sink sink = new Sink();
                    sink.setItem(savedItem);
                    sink.setInSink(sinkFormRequest.getInSink());
                    var savedSink = itemTypesService.save(sink);
                    savedItemDTO.setSink(savedSink);
                    break;
                case "Source":
                    var sourceFromRequest = itemDTO.getSource();
                    Source source = new Source();
                    source.setItem(savedItem);
                    source.setInterArrivalTime(sourceFromRequest.getInterArrivalTime());
                    source.setOutSource(sourceFromRequest.getOutSource());
                    source.setNumberProducts(sourceFromRequest.getNumberProducts());
                    var savedSource = itemTypesService.save(source);
                    savedItemDTO.setSource(savedSource);
                    break;
            }
            return ResponseEntity.ok(savedItemDTO);
        }
        return ResponseEntity.badRequest().build();
    }


}
