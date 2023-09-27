package es.tfg.simuladorteoriacolas.folder;

import es.tfg.simuladorteoriacolas.items.ItemService;
import es.tfg.simuladorteoriacolas.simulation.Simulation;
import es.tfg.simuladorteoriacolas.simulation.SimulationService;
import es.tfg.simuladorteoriacolas.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;


@RestController
@RequestMapping("/api")
public class FolderAPIController {

    @Autowired
    private FolderService folderService;

    @Autowired
    private SimulationService simulationService;

    @Autowired
    private ItemService itemService;

    /**
     * Gets the id of a folder by its name.
     *
     * @param name Name of the folder
     * @return {@code True} Response entity with the id of the folder. {@code False} Bad request.
     */
    @Operation(summary = "Gets the id of a folder by its name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Identifier of a folder",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Integer.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request because the name of the folder doesn't exist",
                    content = @Content)
    })
    @GetMapping("/folder")
    public ResponseEntity<Integer> getFolderId(@Parameter(description = "Name of the folder") @RequestParam String name) {
        var folder = folderService.findByName(name);
        if (folder != null) {
            return ResponseEntity.ok(folder.getIdFolder());
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Returns the list of folders with their respective paginated simulations.
     *
     * @param request Http servlet information
     * @return {@code True} A list of folders with their respective paginated simulations. {@code False} Resource not found.
     */
    @Operation(summary = "Returns the list of folders with their respective paginated simulations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A list of folders with their respective paginated simulations.",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FolderWithPagedSimulationsDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Resource not found.",
                    content = @Content)
    })
    @GetMapping("/foldersAlt")
    public ResponseEntity<List<FolderWithPagedSimulationsDTO>> getFolderWithSimulationsInPages(@Parameter(description = "Http servlet information") HttpServletRequest request) {
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            var folders = folderService.findByUser(request);
            List<FolderWithPagedSimulationsDTO> list = new ArrayList<>();
            for (var i = 0; i < folders.size(); i++) {
                var simulationsPaged = simulationService.getSimulationsInPages(folders.get(i), 0);
                var content = simulationsPaged.getContent();
                var isLast = simulationsPaged.isLast();
                var folderDTO = new FolderWithPagedSimulationsDTO(folders.get(i).getIdFolder(), folders.get(i).getNameFolder(), content, isLast);
                list.add(folderDTO);
            }
            Collections.reverse(list);
            return ResponseEntity.ok(list);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Returns the folder list of a user.
     *
     * @param request Http servlet information.
     * @return {@code True} A list of folders for a user. {@code False} Bad request.
     */
    @Operation(summary = "Returns the folder list of a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A list of folders.",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Folder.class)))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content)
    })
    @GetMapping("/folders")
    public ResponseEntity<List<Folder>> getFolders(@Parameter(description = "Http servlet information") HttpServletRequest request) {
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            var folders = folderService.findByUser(request);
            return ResponseEntity.ok(folders);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Creates a new folder.
     *
     * @param folderDTO Folder Data Transfer Object.
     * @param request   Http servlet information.
     * @return {@code True} The folder created. {@code False} Bad request.
     */
    @Operation(summary = "Creates a new folder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder created.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Folder.class))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred during the creation of the folder",
                    content = @Content)
    })
    @PostMapping("/folders")
    public ResponseEntity<Folder> newFolder(@Parameter(description = "Folder DTO") @RequestBody FolderDTO folderDTO,
                                            @Parameter(description = "Http servlet information") HttpServletRequest request) {
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            Folder folder = folderService.save(null, folderDTO.getNameFolder(), request);
            return ResponseEntity.created(fromCurrentRequest().path("/{id}").buildAndExpand(folder.getIdFolder()).toUri()).body(folder);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Updates a folder.
     *
     * @param folderDTO Folder Data Transfer Object.
     * @param request   Http servlet information.
     * @param idFolder  Id of the folder
     * @return {@code True} The folder updated. {@code False} if bad request.
     */
    @Operation(summary = "Updates a folder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder updated.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Folder.class))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred during the modification of the folder",
                    content = @Content)
    })
    @PutMapping("/folders/{idFolder}")
    public ResponseEntity<Folder> modifyFolder(@Parameter(description = "Folder DTO") @RequestBody FolderDTO folderDTO,
                                               @Parameter(description = "Http servlet information") HttpServletRequest request,
                                               @Parameter(description = "Id of the folder") @PathVariable Integer idFolder) {
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            if (folderService.findById(idFolder).isPresent()) {
                var folder = folderService.save(idFolder, folderDTO.getNameFolder(), request);
                if (folder != null) {
                    return ResponseEntity.ok(folder);
                }
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Deletes a folder.
     *
     * @param idFolder Id of the folder to be deleted.
     * @param request  Http servlet information..
     * @return {@code True} Folder deleted. {@code False} If bad request
     */
    @Operation(summary = "Deletes a folder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder deleted.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Folder.class))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred while deleting of the folder",
                    content = @Content)
    })
    @DeleteMapping("/folders/{idFolder}")
    public ResponseEntity<Folder> deleteFolder(@Parameter(description = "Id of the folder") @PathVariable Integer idFolder,
                                               @Parameter(description = "Http servlet information") HttpServletRequest request) {
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            var creator = request.getUserPrincipal().getName();
            var folder = folderService.findById(idFolder);
            if (folder.isPresent()) {
                if (creator.equals(folder.get().getUserCreator().getNickname())) {
                    List<Simulation> simulationList = simulationService.findAllSimulations(folder.get());
                    for (Simulation simulationAux : simulationList) {
                        itemService.deleteAllItemsBySimulation(simulationAux);
                    }
                    folderService.deleteById(idFolder);
                    return ResponseEntity.ok(folder.get());
                }
                ResponseEntity.badRequest().build();
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
