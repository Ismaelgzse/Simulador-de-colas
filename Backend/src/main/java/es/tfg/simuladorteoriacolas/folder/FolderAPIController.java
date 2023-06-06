package es.tfg.simuladorteoriacolas.folder;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;


@RestController
@RequestMapping("/api")
public class FolderAPIController {
    @Autowired
    private FolderService folderService;

    @GetMapping("/folders")
    public ResponseEntity<List<Folder>> getFolders(HttpServletRequest request){
        var folders= folderService.findByUser(request);
        return ResponseEntity.ok(folders);
    }

    @PostMapping("/folders")
    public ResponseEntity<Folder> newFolder(@RequestBody FolderDTO folderDTO,HttpServletRequest request){
        Folder folder= folderService.save(null,folderDTO.getName(),request);
        return ResponseEntity.created(fromCurrentRequest().path("/{id}").buildAndExpand(folder.getIdFolder()).toUri()).body(folder);
    }
}
