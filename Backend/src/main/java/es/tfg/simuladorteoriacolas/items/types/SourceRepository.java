package es.tfg.simuladorteoriacolas.items.types;

import es.tfg.simuladorteoriacolas.items.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SourceRepository extends JpaRepository<Source,Integer> {
    Source findByItem(Item item);

    Source findByIdSource(Integer idSource);

    Source deleteByItem(Item item);
}
