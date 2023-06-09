package es.tfg.simuladorteoriacolas.simulation;

import es.tfg.simuladorteoriacolas.folder.Folder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SimulationRepository extends JpaRepository<Simulation,Integer> {
    List<Simulation> findAllByFolder(Folder folder);

    Page<Simulation> findAllByFolderOrderByIdSimulation(Folder folder, Pageable page);
}
