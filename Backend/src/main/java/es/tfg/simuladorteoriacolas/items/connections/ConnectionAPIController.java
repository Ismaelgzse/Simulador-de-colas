package es.tfg.simuladorteoriacolas.items.connections;

import es.tfg.simuladorteoriacolas.items.Item;
import es.tfg.simuladorteoriacolas.items.ItemService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ConnectionAPIController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ConnectionService connectionService;

    @PostMapping("/connection")
    public ResponseEntity<Connection> newConnection(@RequestBody Connection connection,
                                                    HttpServletRequest request){
        Item originItem= itemService.findById(connection.getOriginItem().getIdItem()).orElseThrow();
        Item destinationItem= itemService.findById(connection.getDestinationItem().getIdItem()).orElseThrow();
        Connection savedConnection= connectionService.save(null,
                originItem,
                destinationItem,
                connection.getPercentage());
        if (savedConnection!=null){
            return ResponseEntity.ok(savedConnection);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/connection/{idConnection}")
    public ResponseEntity<Connection> deleteConnection(@PathVariable Integer idConnection,
                                                       HttpServletRequest request){
        Connection connection= connectionService.findById(idConnection);
        if (connection!=null){
            connectionService.delete(connection);
            return ResponseEntity.ok(connection);
        }
        return ResponseEntity.badRequest().build();
    }
}
