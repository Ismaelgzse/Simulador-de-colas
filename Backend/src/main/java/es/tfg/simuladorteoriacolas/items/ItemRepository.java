package es.tfg.simuladorteoriacolas.items;

import es.tfg.simuladorteoriacolas.simulation.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Integer> {
    List<Item> findAllByIdSimulation(Simulation simulation);

    List<Item> deleteAllByIdSimulation(Simulation simulation);

}
