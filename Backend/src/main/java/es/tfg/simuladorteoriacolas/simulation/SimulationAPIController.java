package es.tfg.simuladorteoriacolas.simulation;

import es.tfg.simuladorteoriacolas.folder.FolderService;
import es.tfg.simuladorteoriacolas.items.Item;
import es.tfg.simuladorteoriacolas.items.ItemService;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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

    @GetMapping("/folders/{idFolder}/simulations")
    public ResponseEntity<Page<Simulation>> getSimulations(@PathVariable Integer idFolder,@RequestParam(required = false) Integer page) {
        var folder = folderService.findById(idFolder);
        if (folder.isPresent()) {
            var numPage= (page==null) ? 0 : page;
            var simulations = simulationService.getSimulationsInPages(folder.get(),numPage);
            if (simulations.hasContent()){
                return ResponseEntity.ok(simulations);
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/folders/{idFolder}/simulations")
    public ResponseEntity<Simulation> newSimulation(@PathVariable Integer idFolder,
                                                    @RequestBody SimulationDTO simulation,
                                                    HttpServletRequest request) {
        if (folderService.findById(idFolder).isPresent()) {
            var savedSimulation = simulationService.save(idFolder, simulation, request);
            return ResponseEntity.created(fromCurrentRequest().path("/{id}").buildAndExpand(savedSimulation.getIdSimulation()).toUri()).body(savedSimulation);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/folders/{idFolder}/simulations/{idSimulation}")
    public ResponseEntity<Simulation> deleteSimulation(@PathVariable Integer idFolder,
                                                       @PathVariable Integer idSimulation) {
        var simulation = simulationService.findById(idSimulation).orElseThrow();
        if (simulation == null) {
            return ResponseEntity.notFound().build();
        }
        itemService.deleteAllItemsBySimulation(simulation);
        simulationService.delete(idSimulation);
        return ResponseEntity.ok(simulation);
    }

    @PutMapping("/folders/{idFolder}/simulations/{idSimulation}")
    public ResponseEntity<Simulation> modifySimulation(@PathVariable Integer idFolder,
                                                       @PathVariable Integer idSimulation,
                                                       @RequestBody SimulationDTO simulation,
                                                       HttpServletRequest request) {
        if (simulationService.findById(idSimulation).isPresent()) {
            var savedSimulation = simulationService.save(idFolder, simulation, request);
            if (savedSimulation != null) {
                return ResponseEntity.ok(savedSimulation);
            }
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/simulations/{idSimulation}/image")
    public ResponseEntity<Object> uploadSimulationImage(@PathVariable Integer idSimulation,
                                                        @RequestParam MultipartFile file,
                                                        HttpServletRequest request) throws IOException {
        var requestUser= request.getUserPrincipal().getName();
        var simulation=simulationService.findById(idSimulation).orElseThrow();
        if (requestUser.equals(simulation.getUserCreator().getNickname())){
            try {
                simulation.setImageFile(BlobProxy.generateProxy(file.getInputStream(),file.getSize()))
                        .setMimeImage(file.getContentType());
                var savedSimulation=simulationService.save(simulation);
                if (savedSimulation!=null){
                    return ResponseEntity.ok(savedSimulation);
                }
            }catch (Exception e){
                ResponseEntity.badRequest().build();
            }

        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

    }

    @GetMapping("/simulations/{idSimulation}/image")
    public ResponseEntity<Object> getSimulationImage(@PathVariable Integer idSimulation,
                                                     HttpServletRequest request) throws SQLException {
        var requestUser= request.getUserPrincipal().getName();
        var simulation=simulationService.findById(idSimulation).orElseThrow();
        if (requestUser.equals(simulation.getUserCreator().getNickname())){
            if (simulation.getImageFile()!=null){
                InputStreamResource image= new InputStreamResource(simulation.getImageFile().getBinaryStream());
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, simulation.getMimeImage())
                        .contentLength(simulation.getImageFile().length()).body(image);
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

    }

    @GetMapping("/simulation/{idSimulation}")
    public ResponseEntity<Simulation> getSimulation(@PathVariable Integer idSimulation,
                                                    HttpServletRequest request){
        var requestUser= request.getUserPrincipal().getName();
        var simulation=simulationService.findById(idSimulation).orElseThrow();
        if (requestUser.equals(simulation.getUserCreator().getNickname())){
            return ResponseEntity.ok(simulation);
        }
        return ResponseEntity.badRequest().build();
    }

}
