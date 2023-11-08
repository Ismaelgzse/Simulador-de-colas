package es.tfg.simuladorteoriacolas.simulation;

import es.tfg.simuladorteoriacolas.folder.FolderService;
import es.tfg.simuladorteoriacolas.items.ItemDTO;
import es.tfg.simuladorteoriacolas.items.ItemService;
import es.tfg.simuladorteoriacolas.simulation.algorithm.QuickSimulationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api")
public class SimulationAPIController {
    @Autowired
    private FolderService folderService;

    @Autowired
    private SimulationService simulationService;

    @Autowired
    private ItemService itemService;

    /**
     * Returns a page of simulations of a folder.
     *
     * @param idFolder Id of the folder.
     * @param page     Page of the simulation.
     * @return {@code True} A page of simulations of a folder. {@code False} Bad request.
     */
    @Operation(summary = "Returns a page of simulations of a folder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The page of simulations",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Simulation.class)))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred while getting the page of simulations.",
                    content = @Content)
    })
    @GetMapping("/folders/{idFolder}/simulations")
    public ResponseEntity<Page<Simulation>> getSimulations(@Parameter(description = "Id of the folder") @PathVariable Integer idFolder,
                                                           @Parameter(description = "Page of the simulation") @RequestParam(required = false) Integer page,
                                                           @Parameter(description = "Http servlet information") HttpServletRequest request) {
        var folder = folderService.findById(idFolder);
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            if (folder.get().getUserCreator().getNickname().equals(request.getUserPrincipal().getName())) {
                var numPage = (page == null) ? 0 : page;
                var simulations = simulationService.getSimulationsInPages(folder.get(), numPage);
                if (simulations.hasContent()) {
                    return ResponseEntity.ok(simulations);
                }
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Creates a new simulation on a folder.
     *
     * @param idFolder   Id of the folder.
     * @param simulation Simulation DTO.
     * @param request    Http servlet information.
     * @return {@code True} The simulation created. {@code False} Bad request.
     */
    @Operation(summary = "Creates a new simulation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The simulation created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Simulation.class))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred while creating the simulation.",
                    content = @Content)
    })
    @PostMapping("/folders/{idFolder}/simulations")
    public ResponseEntity<Simulation> newSimulation(@Parameter(description = "Id of the folder") @PathVariable Integer idFolder,
                                                    @Parameter(description = "Simulation DTO") @RequestBody SimulationDTO simulation,
                                                    @Parameter(description = "Http servlet information") HttpServletRequest request) {
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            if (folderService.findById(idFolder).isPresent()) {
                var savedSimulation = simulationService.save(idFolder, simulation, request);
                return ResponseEntity.created(fromCurrentRequest().path("/{id}").buildAndExpand(savedSimulation.getIdSimulation()).toUri()).body(savedSimulation);
            }
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Deletes a simulation.
     *
     * @param idFolder     Id of the folder.
     * @param idSimulation Id of the simulation.
     * @param request      Http servlet information
     * @return {@code True} The simulation deleted {@code False} Bad request.
     */
    @Operation(summary = "Deletes a simulation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The simulation deleted.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Simulation.class))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred while deleting the simulation.",
                    content = @Content)
    })
    @DeleteMapping("/folders/{idFolder}/simulations/{idSimulation}")
    public ResponseEntity<Simulation> deleteSimulation(@Parameter(description = "Id of the folder") @PathVariable Integer idFolder,
                                                       @Parameter(description = "Id of the simulation") @PathVariable Integer idSimulation,
                                                       @Parameter(description = "Http servlet information") HttpServletRequest request) {
        var folder = folderService.findById(idFolder).get();
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            if (folder.getUserCreator().getNickname().equals(request.getUserPrincipal().getName())) {
                var simulation = simulationService.findById(idSimulation).orElseThrow();
                if (simulation == null) {
                    return ResponseEntity.notFound().build();
                }
                if (simulation.getUserCreator().getNickname().equals(request.getUserPrincipal().getName())) {
                    itemService.deleteAllItemsBySimulation(simulation);
                    simulationService.delete(idSimulation);
                    return ResponseEntity.ok(simulation);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Updates a simulation.
     *
     * @param idFolder     Id of the folder.
     * @param idSimulation Id of the simulation.
     * @param simulation   Simulation DTO.
     * @param request      Http servlet information.
     * @return {@code True} The simulation updated {@code False} Bad request.
     */
    @Operation(summary = "Updates a simulation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The simulation updated.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Simulation.class))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred while updating the simulation.",
                    content = @Content)
    })
    @PutMapping("/folders/{idFolder}/simulations/{idSimulation}")
    public ResponseEntity<Simulation> modifySimulation(@Parameter(description = "Id of the folder") @PathVariable Integer idFolder,
                                                       @Parameter(description = "Id of the simulation") @PathVariable Integer idSimulation,
                                                       @Parameter(description = "Simulation DTO") @RequestBody SimulationDTO simulation,
                                                       @Parameter(description = "Http servlet information") HttpServletRequest request) {
        var simulationAux = simulationService.findById(idSimulation).get();
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            if (simulationAux.getUserCreator().getNickname().equals(request.getUserPrincipal().getName())) {
                if (simulationService.findById(idSimulation).isPresent()) {
                    var savedSimulation = simulationService.save(idFolder, simulation, request);
                    if (savedSimulation != null) {
                        return ResponseEntity.ok(savedSimulation);
                    }
                    return ResponseEntity.badRequest().build();
                }
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Saves the image of a simulation.
     *
     * @param idSimulation Id of the simulation.
     * @param file         Multipart file.
     * @param request      Http servlet information.
     * @return {@code True} The object saved. {@code False} Bad request.
     * @throws IOException
     */
    @Operation(summary = "Saves the image of a simulation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The image saved.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Object.class))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred while saving the image.",
                    content = @Content)
    })
    @PutMapping("/simulations/{idSimulation}/image")
    public ResponseEntity<Object> uploadSimulationImage(@Parameter(description = "Id of the simulation") @PathVariable Integer idSimulation,
                                                        @Parameter(description = "Multipart file") @RequestParam MultipartFile file,
                                                        @Parameter(description = "Http servlet information") HttpServletRequest request) throws IOException {
        var requestUser = request.getUserPrincipal().getName();
        var simulation = simulationService.findById(idSimulation).orElseThrow();
        if (requestUser.equals(simulation.getUserCreator().getNickname())) {
            try {
                simulation.setImageFile(BlobProxy.generateProxy(file.getInputStream(), file.getSize()))
                        .setMimeImage(file.getContentType());
                var savedSimulation = simulationService.save(simulation);
                if (savedSimulation != null) {
                    return ResponseEntity.ok(savedSimulation);
                }
            } catch (Exception e) {
                ResponseEntity.badRequest().build();
            }

        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

    }

    /**
     * Gets the image of a simulation.
     *
     * @param idSimulation Id of the simulation.
     * @param request      Http servlet information.
     * @return {@code True} The image of the simulation. {@code False} Bad request.
     * @throws SQLException
     */
    @Operation(summary = "Gets the image of a simulation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The image of a simulation.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Object.class))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred while getting the image.",
                    content = @Content)
    })
    @GetMapping("/simulations/{idSimulation}/image")
    public ResponseEntity<Object> getSimulationImage(@Parameter(description = "Id of the simulation") @PathVariable Integer idSimulation,
                                                     @Parameter(description = "Http servlet information") HttpServletRequest request) throws SQLException {
        var requestUser = request.getUserPrincipal().getName();
        var simulation = simulationService.findById(idSimulation).orElseThrow();
        if (requestUser.equals(simulation.getUserCreator().getNickname())) {
            if (simulation.getImageFile() != null) {
                InputStreamResource image = new InputStreamResource(simulation.getImageFile().getBinaryStream());
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, simulation.getMimeImage())
                        .contentLength(simulation.getImageFile().length()).body(image);
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

    }

    /**
     * Gets the simulation.
     *
     * @param idSimulation Id of a simulation.
     * @param request      Http servlet information.
     * @return {@code True} The simulation. {@code False} Bad request.
     */
    @Operation(summary = "Gets the simulation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The simulation.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Simulation.class))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred while getting the simulation.",
                    content = @Content)
    })
    @GetMapping("/simulation/{idSimulation}")
    public ResponseEntity<Simulation> getSimulation(@Parameter(description = "Id of the simulation") @PathVariable Integer idSimulation,
                                                    @Parameter(description = "Http servlet information") HttpServletRequest request) {
        var requestUser = request.getUserPrincipal().getName();
        var simulation = simulationService.findById(idSimulation).orElseThrow();
        if (requestUser.equals(simulation.getUserCreator().getNickname())) {
            return ResponseEntity.ok(simulation);
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Checks if the simulation is running.
     *
     * @param idSimulation Id of the simulation.
     * @param request      Http servlet information.
     * @return {@code} Boolean with the value of the check. {@code} Bad request.
     */
    @Operation(summary = "Checks if the simulation is running.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boolean with the value of the check.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred while checking the status of the simulation.",
                    content = @Content)
    })
    @GetMapping("simulation/{idSimulation}/isRunning")
    public ResponseEntity<Boolean> isRunning(@Parameter(description = "Id of the simulation") @PathVariable Integer idSimulation,
                                             @Parameter(description = "Http servlet information") HttpServletRequest request) {
        var simulation = simulationService.findById(idSimulation).get();
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            if (simulation.getUserCreator().getNickname().equals(request.getUserPrincipal().getName())) {
                if (simulation.getStatusSimulation().equals("1")) {
                    return ResponseEntity.ok(true);
                }
                return ResponseEntity.ok(false);
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Checks if there is an active quick simulation running.
     * @param idSimulation Id of the simulation.
     * @param request Http servlet information.
     * @return {@code} Boolean with the value of the check. {@code} Bad request.
     */
    @Operation(summary = "Checks if the quick simulation is running.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boolean with the value of the check.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred while checking the status of the simulation.",
                    content = @Content)
    })
    @GetMapping("simulation/{idSimulation}/isRunningQuickSimulation")
    public ResponseEntity<Boolean> isRunningQuickSimulation(@Parameter(description = "Id of the simulation") @PathVariable Integer idSimulation,
                                                            @Parameter(description = "Http servlet information") HttpServletRequest request) {
        var simulation = simulationService.findById(idSimulation).get();
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            if (simulation.getUserCreator().getNickname().equals(request.getUserPrincipal().getName())) {
                if (simulation.getStatusQuickSimulation().equals("1")) {
                    return ResponseEntity.ok(true);
                }
                return ResponseEntity.ok(false);
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Initialise and wait for the result of the quick simulation (List of ItemDTO).
     * @param idSimulation Id of the simulation.
     * @param quickSimulationDTO The DTO that stores the simulations and other options.
     * @param request Http servlet information.
     * @return {@code} The status of the simulations after they have been completed. {@code} Bad request.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Operation(summary = "Initialise and wait for the result of the quick simulation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of simulations.",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ItemDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content)
    })
    @PostMapping("simulation/{idSimulation}/quickSimulation")
    public ResponseEntity<List<List<ItemDTO>>> quickSimulation(@Parameter(description = "Id of the simulation") @PathVariable Integer idSimulation,
                                                               @Parameter(description = "Quick simulation DTO") @RequestBody QuickSimulationDTO quickSimulationDTO,
                                                               @Parameter(description = "Http servlet information") HttpServletRequest request) throws ExecutionException, InterruptedException {
        var simulation = simulationService.findById(idSimulation).get();
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            if (simulation.getUserCreator().getNickname().equals(request.getUserPrincipal().getName())) {
                if (!simulation.getStatusQuickSimulation().equals("1") && !simulation.getStatusSimulation().equals("1")) {

                    CompletableFuture<List<List<ItemDTO>>> future = simulationService.quickSimulationsFunc(idSimulation, quickSimulationDTO.getTimeSimulation(), quickSimulationDTO.getNumberSimulations(),quickSimulationDTO.getListSimulations());

                    List<List<ItemDTO>> simulationResult = future.get();

                    return ResponseEntity.ok(simulationResult);
                }
                return ResponseEntity.ok(null);
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

    }

    /**
     * Returns an array of bytes corresponding to the results of the simulation (List of ItemDTO) in pdf format.
     * @param idSimulation Id of the simulation.
     * @param simulations List of simulations.
     * @param request Http servlet information.
     * @return {@code} An array of bytes.
     * @throws IOException
     */
    @Operation(summary = "Returns an array of bytes corresponding to the results of the simulation (List of ItemDTO) in pdf format.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "An array of bytes.",
                    content = {@Content(mediaType = "application/octet-stream",
                            schema = @Schema(type = "string", format = "binary"))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content)
    })
    @PostMapping("simulation/{idSimulation}/quickSimulation/pdf")
    public ResponseEntity<byte[]> downloadPDF(@Parameter(description = "Id of the simulation") @PathVariable Integer idSimulation,
                                              @Parameter(description = "List of simulations (List of ItemDTO)") @RequestBody List<List<ItemDTO>> simulations,
                                              @Parameter(description = "Http servlet information") HttpServletRequest request) throws IOException {
        var simulation = simulationService.findById(idSimulation).get();
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            if (simulation.getUserCreator().getNickname().equals(request.getUserPrincipal().getName())) {
                PDDocument result= simulationService.generatePDF(simulations);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                result.save(byteArrayOutputStream);
                result.close();
                byte[] pdfBytes = byteArrayOutputStream.toByteArray();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+simulation.getTitle()+".pdf");

                return ResponseEntity.ok()
                        .headers(httpHeaders)
                        .contentType(MediaType.parseMediaType("application/pdf"))
                        .body(pdfBytes);
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Returns an array of bytes corresponding to the results of the simulation (List of ItemDTO) in excel format.
     * @param idSimulation Id of the simulation.
     * @param simulations List of simulations.
     * @param request Http servlet information.
     * @return {@code} An array of bytes.
     * @throws IOException
     */
    @Operation(summary = "Returns an array of bytes corresponding to the results of the simulation (List of ItemDTO) in excel format.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "An array of bytes.",
                    content = {@Content(mediaType = "application/octet-stream",
                            schema = @Schema(type = "string", format = "binary"))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content)
    })
    @PostMapping("simulation/{idSimulation}/quickSimulation/excel")
    public ResponseEntity<byte[]> downloadExcel(@Parameter(description = "Id of the simulation") @PathVariable Integer idSimulation,
                                                @Parameter(description = "List of simulations (List of ItemDTO)") @RequestBody List<List<ItemDTO>> simulations,
                                                @Parameter(description = "Http servlet information") HttpServletRequest request) throws IOException {
        var simulation = simulationService.findById(idSimulation).get();
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            if (simulation.getUserCreator().getNickname().equals(request.getUserPrincipal().getName())) {
                Workbook workbook= simulationService.generateExcel(simulations,simulation.getTitle());

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                workbook.write(byteArrayOutputStream);
                workbook.close();

                byte[] excelBytes = byteArrayOutputStream.toByteArray();

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+simulation.getTitle()+".xlsx");

                return ResponseEntity.ok()
                        .headers(httpHeaders)
                        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .body(excelBytes);
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
