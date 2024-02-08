package es.tfg.simuladorteoriacolas.simulation;

import es.tfg.simuladorteoriacolas.exportation.excel.ExcelGeneratorService;
import es.tfg.simuladorteoriacolas.folder.Folder;
import es.tfg.simuladorteoriacolas.folder.FolderService;
import es.tfg.simuladorteoriacolas.items.ItemDTO;
import es.tfg.simuladorteoriacolas.items.ItemService;
import es.tfg.simuladorteoriacolas.items.types.ItemTypesService;
import es.tfg.simuladorteoriacolas.exportation.pdf.PdfGeneratorService;
import es.tfg.simuladorteoriacolas.simulation.algorithm.Algorithm;
import es.tfg.simuladorteoriacolas.simulation.algorithm.QuickSimulationAlgorithm;
import es.tfg.simuladorteoriacolas.user.UserEntity;
import es.tfg.simuladorteoriacolas.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class SimulationService {

    @Autowired
    private ExcelGeneratorService excelGeneratorService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

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
        simulation.setStatusQuickSimulation("0");
        simulation.setStatusSimulation("0");
        return simulationRepository.save(simulation);
    }

    public void simulate(Integer simulationId){
        Simulation simulation=simulationRepository.findById(simulationId).get();
        //Gets all items of the simulation
        List<ItemDTO> simulationItems= itemService.getSimulationItems(simulation);
        //Creates and starts the algorithm
        Algorithm algorithm= new Algorithm(simulationId,simulationItems,this,simpMessageSendingOperations);
        Thread thread= new Thread(algorithm);
        thread.start();
    }

    public PDDocument generatePDF(List<List<ItemDTO>> simulations) throws IOException {
        return pdfGeneratorService.generatePdf(simulations);
    }

    public Workbook generateExcel(List<List<ItemDTO>> simulations, String filename) throws IOException {
        return excelGeneratorService.generateExcel(simulations,filename);
    }

    @Async
    public CompletableFuture<List<List<ItemDTO>>> quickSimulationsFunc(Integer simulationId, Double timeSimulation, Integer numberSimulations,List<List<ItemDTO>> simulations) throws ExecutionException, InterruptedException {
        try {
            Simulation simulation=simulationRepository.findById(simulationId).get();
            simulation.setStatusQuickSimulation("1");
            simulationRepository.save(simulation);
            List<ItemDTO> simulationItems=itemService.getSimulationItems(simulation);

            //The time of the simulation is reduced according to the number of minutes
            Double durationOfQuickSimulation;

            //If the simulation time is less than 30 minutes
            if(timeSimulation<30){
                durationOfQuickSimulation=timeSimulation*60*0.15*1000;
            }

            //If the simulation time is between 30 and 60 minutes
            else if (timeSimulation>=30 && timeSimulation<60){
                durationOfQuickSimulation=timeSimulation*60*0.1*1000;
            }

            //If the simulation time is greater than 60 minutes
            else {
                durationOfQuickSimulation=timeSimulation*60*0.07*1000;
            }

            var multiplierTime= (timeSimulation*60000)/durationOfQuickSimulation;
            ExecutorService executorService= Executors.newCachedThreadPool();
            List<List<ItemDTO>> allSimulations= new ArrayList<>();
            List<Future<List<ItemDTO>>> futureList= new ArrayList<>();

            //Create as many concurrent simulations as necessary
            for (var i=0;i<numberSimulations;i++){
                QuickSimulationAlgorithm quickSimulationAlgorithm= new QuickSimulationAlgorithm(simulations.get(i),timeSimulation,multiplierTime);
                futureList.add(executorService.submit(quickSimulationAlgorithm));
            }
            //Waits for the results of the algorithm
            for (Future<List<ItemDTO>> future: futureList) {
                var futureAux=future.get();
                allSimulations.add(futureAux);
            }

            simulation.setStatusQuickSimulation("0");
            simulationRepository.save(simulation);
            return CompletableFuture.completedFuture(allSimulations);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }
        return CompletableFuture.completedFuture(null);
    }
}
