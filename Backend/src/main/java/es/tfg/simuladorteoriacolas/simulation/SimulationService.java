package es.tfg.simuladorteoriacolas.simulation;

import es.tfg.simuladorteoriacolas.folder.Folder;
import es.tfg.simuladorteoriacolas.folder.FolderService;
import es.tfg.simuladorteoriacolas.user.UserEntity;
import es.tfg.simuladorteoriacolas.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SimulationService {
    @Autowired
    private SimulationRepository simulationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FolderService folderService;

    public void delete(Integer id){
        simulationRepository.deleteById(id);
    }

    public Page<Simulation> getSimulationsInPages(Folder folder,Integer page){
        return simulationRepository.findAllByFolderOrderByIdSimulation(folder,PageRequest.of(page,5));
    }

    public Optional<Simulation> findById(Integer id){
        return simulationRepository.findById(id);
    }

    public List<Simulation> findAllSimulations(Folder folder){
        return simulationRepository.findAllByFolder(folder);
    }

    public Simulation save(Integer idFolder,SimulationDTO simulationDTO, HttpServletRequest request){
        Simulation simulation;
        if (simulationDTO.getIdSimulation()==null){
            simulation = new Simulation();
        }
        else {
            simulation=simulationRepository.findById(simulationDTO.getIdSimulation()).get();
        }
        simulation.setBody(simulationDTO.getBody());
        simulation.setTitle(simulationDTO.getTitle());
        Folder folder= folderService.findById(idFolder).get();
        simulation.setFolder(folder);
        UserEntity user= userService.findByNickname(request.getUserPrincipal().getName()).get();
        simulation.setUserCreator(user);
        return simulationRepository.save(simulation);
    }
}
