package es.tfg.simuladorteoriacolas.simulation;

import es.tfg.simuladorteoriacolas.folder.FolderService;
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

    @GetMapping("/simulations")
    public ResponseEntity<List<Simulation>> getSimulations(@RequestParam Integer idFolder){
        var folder= folderService.findById(idFolder);
        if (folder.isPresent()){
            var simulations=simulationService.findAllSimulations(folder.get());
            return ResponseEntity.ok(simulations);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/simulations")
    public ResponseEntity<Simulation> newSimulation(@RequestBody SimulationDTO simulation,HttpServletRequest request){
        var savedSimulation=simulationService.save(simulation,request);
        return ResponseEntity.created(fromCurrentRequest().path("/{id}").buildAndExpand(savedSimulation.getIdSimulation()).toUri()).body(savedSimulation);
    }
}
