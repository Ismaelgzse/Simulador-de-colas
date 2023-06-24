package es.tfg.simuladorteoriacolas.items.types;

import es.tfg.simuladorteoriacolas.items.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueRepository extends JpaRepository<Queue,Integer> {
    Queue findByItem(Item item);

    Queue findByIdQueue(Integer idQueue);

    Queue deleteByItem(Item item);
}
