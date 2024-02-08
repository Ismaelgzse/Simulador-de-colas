package es.tfg.simuladorteoriacolas.items.connections;

import es.tfg.simuladorteoriacolas.items.Item;
import es.tfg.simuladorteoriacolas.items.ItemService;
import es.tfg.simuladorteoriacolas.simulation.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ConnectionAPIController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ConnectionService connectionService;


    /**
     * Creates a new connection between two items.
     *
     * @param connection A connection.
     * @param request    Http servlet information.
     * @return {@code True} Creates a new connection. {@code False} Bad request.
     */
    @Operation(summary = "Creates a new connection between two items.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connection created.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Connection.class))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred while creating a new connection",
                    content = @Content)
    })
    @PostMapping("/connection")
    public ResponseEntity<Connection> newConnection(@Parameter(description = "Connection object") @RequestBody Connection connection,
                                                    @Parameter(description = "Http servlet information") HttpServletRequest request) {
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            Item originItem = itemService.findById(connection.getOriginItem().getIdItem()).orElseThrow();
            Item destinationItem = itemService.findById(connection.getDestinationItem().getIdItem()).orElseThrow();
            Connection savedConnection = connectionService.save(null,
                    originItem,
                    destinationItem,
                    connection.getPercentage());
            if (savedConnection != null) {
                return ResponseEntity.ok(savedConnection);
            }
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Deletes a connection.
     *
     * @param idConnection Id of the connection to be deleted.
     * @param request      Http servlet information.
     * @return {@code True} The connection deleted. {@code False} Bad request.
     */
    @Operation(summary = "Deletes a connection.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connection deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Connection.class))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred while creating a new connection",
                    content = @Content)
    })
    @DeleteMapping("/connection/{idConnection}")
    public ResponseEntity<Connection> deleteConnection(@Parameter(description = "Id of the connection") @PathVariable Integer idConnection,
                                                       @Parameter(description = "Http servlet information") HttpServletRequest request) {
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            Connection connection = connectionService.findById(idConnection);
            if (connection != null) {
                connectionService.delete(connection);
                return ResponseEntity.ok(connection);
            }
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
