package es.tfg.simuladorteoriacolas.folder;

import es.tfg.simuladorteoriacolas.simulation.SimulationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/folder")
    public ResponseEntity<Integer> getFolderId(@RequestParam String name){
        var folder= folderService.findByName(name);
        if (folder!=null){
            return ResponseEntity.ok(folder.getIdFolder());
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/foldersAlt")
    public ResponseEntity<List<FolderWithPagedSimulationsDTO>> getFolderWithSimulationsInPages(HttpServletRequest request){
        var folders= folderService.findByUser(request);
        List<FolderWithPagedSimulationsDTO> list= new ArrayList<>();
        for (var i=0;i<folders.size();i++){
            var simulationsPaged=simulationService.getSimulationsInPages(folders.get(i),0);
            var content=simulationsPaged.getContent();
            var isLast=simulationsPaged.isLast();
            var folderDTO= new FolderWithPagedSimulationsDTO(folders.get(i).getIdFolder(),folders.get(i).getNameFolder(),content,isLast);
            list.add(folderDTO);
        }
        Collections.reverse(list);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/folders")
    public ResponseEntity<List<Folder>> getFolders(HttpServletRequest request){
        var folders= folderService.findByUser(request);
        return ResponseEntity.ok(folders);
    }

    @PostMapping("/folders")
    public ResponseEntity<Folder> newFolder(@RequestBody FolderDTO folderDTO,HttpServletRequest request){
        Folder folder= folderService.save(null,folderDTO.getNameFolder(),request);
        return ResponseEntity.created(fromCurrentRequest().path("/{id}").buildAndExpand(folder.getIdFolder()).toUri()).body(folder);
    }

    @PutMapping("/folders/{idFolder}")
    public ResponseEntity<Folder> modifyFolder(@RequestBody FolderDTO folderDTO,
                                               HttpServletRequest request,
                                               @PathVariable Integer idFolder){
        if (folderService.findById(idFolder).isPresent()){
            var folder= folderService.save(idFolder,folderDTO.getNameFolder(),request);
            if (folder!=null){
                return ResponseEntity.ok(folder);
            }
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/folders/{idFolder}")
    public ResponseEntity<Folder> deleteFolder(@PathVariable Integer idFolder,
                                               HttpServletRequest request){
        var creator= request.getUserPrincipal().getName();
        var folder= folderService.findById(idFolder);
        if (folder.isPresent()){
            if (creator.equals(folder.get().getUserCreator().getNickname())){
                folderService.deleteById(idFolder);
                return ResponseEntity.ok(folder.get());
            }
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.notFound().build();
    }
}
