package es.tfg.simuladorteoriacolas.simulation;

import es.tfg.simuladorteoriacolas.folder.Folder;
import es.tfg.simuladorteoriacolas.folder.FolderService;
import es.tfg.simuladorteoriacolas.user.User;
import es.tfg.simuladorteoriacolas.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimulationService {
    @Autowired
    private SimulationRepository simulationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FolderService folderService;

    public List<Simulation> findAllSimulations(Folder folder){
        return simulationRepository.findAllByFolder(folder);
    }

    public Simulation save(SimulationDTO simulationDTO, HttpServletRequest request){
        Simulation simulation;
        if (simulationDTO.getId()==null){
            simulation = new Simulation();
        }
        else {
            simulation=simulationRepository.findById(simulationDTO.getId()).get();
        }
        simulation.setBody(simulationDTO.getBody());
        simulation.setTitle(simulationDTO.getTitle());
        Folder folder= folderService.findById(simulationDTO.getFolder()).get();
        simulation.setFolder(folder);
        User user= userService.findByNickname(request.getUserPrincipal().getName()).get();
        simulation.setUserCreator(user);
        return simulationRepository.save(simulation);
    }
}
