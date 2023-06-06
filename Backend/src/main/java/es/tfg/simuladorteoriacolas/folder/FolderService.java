package es.tfg.simuladorteoriacolas.folder;

import es.tfg.simuladorteoriacolas.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FolderService {
    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserService userService;

    public List<Folder> findByUser(HttpServletRequest request){
        var user=userService.findByNickname(request.getUserPrincipal().getName()).get();
        return folderRepository.findAllByUserCreator(user);
    }

    public Optional<Folder> findById(Integer id){
        return folderRepository.findById(id);
    }

    public Folder save(Integer id, String name, HttpServletRequest request){
        Folder folder;
        if (id==null){
            folder= new Folder();
        }
        else {
            folder=folderRepository.findAllByIdFolder(id);
        }
        folder.setNameFolder(name);
        var creator=userService.findByNickname(request.getUserPrincipal().getName()).get();
        folder.setUserCreator(creator);
        return folderRepository.save(folder);
    }
}
