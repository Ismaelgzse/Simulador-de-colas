package es.tfg.simuladorteoriacolas.simulation;

import es.tfg.simuladorteoriacolas.folder.Folder;
import es.tfg.simuladorteoriacolas.folder.FolderService;
import es.tfg.simuladorteoriacolas.items.ItemDTO;
import es.tfg.simuladorteoriacolas.items.ItemService;
import es.tfg.simuladorteoriacolas.items.types.ItemTypesService;
import es.tfg.simuladorteoriacolas.simulation.algorithm.Algorithm;
import es.tfg.simuladorteoriacolas.user.UserEntity;
import es.tfg.simuladorteoriacolas.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SimulationService {

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    @Autowired
    private ItemTypesService itemTypesService;

    @Autowired
    private ItemService itemService;

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

    public Simulation save(Simulation simulation){
        return simulationRepository.save(simulation);
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
        simulation.setStatusSimulation(simulationDTO.getStatusSimulation());
        simulation.setBody(simulationDTO.getBody());
        simulation.setTitle(simulationDTO.getTitle());
        Folder folder= folderService.findById(idFolder).get();
        simulation.setFolder(folder);
        UserEntity user= userService.findByNickname(request.getUserPrincipal().getName()).get();
        simulation.setUserCreator(user);
        return simulationRepository.save(simulation);
    }

    public void simulate(Integer simulationId){
        Simulation simulation=simulationRepository.findById(simulationId).get();
        List<ItemDTO> simulationItems= itemService.getSimulationItems(simulation);
        Algorithm algorithm= new Algorithm(simulationId,simulationItems,itemTypesService,this,simpMessageSendingOperations);
        Thread thread= new Thread(algorithm);

        thread.start();
    }
}
