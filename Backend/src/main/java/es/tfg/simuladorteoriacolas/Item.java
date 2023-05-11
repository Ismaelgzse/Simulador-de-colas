package es.tfg.simuladorteoriacolas;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Item {
    @Id
    public int id;
}
