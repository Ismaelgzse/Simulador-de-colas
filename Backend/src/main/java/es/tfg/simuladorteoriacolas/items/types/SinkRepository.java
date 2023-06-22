package es.tfg.simuladorteoriacolas.items.types;

import es.tfg.simuladorteoriacolas.items.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SinkRepository extends JpaRepository<Sink,Integer> {
    Sink findByItem(Item item);

    Sink findByIdSink(Integer idSink);
}
