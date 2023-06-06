package es.tfg.simuladorteoriacolas.simulation;

import es.tfg.simuladorteoriacolas.folder.FolderService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<Simulation>> getSimulations(@PathVariable Integer idFolder){
        var folder= folderService.findById(idFolder);
        if (folder.isPresent()){
            var simulations=simulationService.findAllSimulations(folder.get());
            return ResponseEntity.ok(simulations);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/folders/{idFolder}/simulations")
    public ResponseEntity<Simulation> newSimulation(@PathVariable Integer idFolder, @RequestBody SimulationDTO simulation, HttpServletRequest request){
        if (folderService.findById(idFolder).isPresent()){
            var savedSimulation=simulationService.save(idFolder,simulation,request);
            return ResponseEntity.created(fromCurrentRequest().path("/{id}").buildAndExpand(savedSimulation.getIdSimulation()).toUri()).body(savedSimulation);
        }
        return ResponseEntity.badRequest().build();
    }
}
