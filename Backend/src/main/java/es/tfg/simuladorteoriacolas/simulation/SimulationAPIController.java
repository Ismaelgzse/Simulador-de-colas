package es.tfg.simuladorteoriacolas.simulation;

import es.tfg.simuladorteoriacolas.folder.FolderService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api")
public class SimulationAPIController {
    @Autowired
    private FolderService folderService;

    @Autowired
    private SimulationService simulationService;

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
        var simulation = simulationService.findById(idSimulation);
        if (simulation == null) {
            return ResponseEntity.notFound().build();
        }
        simulationService.delete(idSimulation);
        return ResponseEntity.ok(simulation.get());
    }

    @PutMapping("folders/{idFolder}/simulations/{idSimulation}")
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
}
