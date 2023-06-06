package es.tfg.simuladorteoriacolas.folder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.tfg.simuladorteoriacolas.simulation.Simulation;
import es.tfg.simuladorteoriacolas.user.UserEntity;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idFolder;

    @Column(nullable = false)
    private String nameFolder;

    @OneToMany(mappedBy="folder", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Simulation> simulations;

    @JsonIgnore
    @ManyToOne
    private UserEntity userCreator;

    public Folder() {

    }

    public Folder(String name, UserEntity userCreator){
        this.nameFolder =name;
        this.userCreator=userCreator;
    }

    public Integer getIdFolder() {
        return idFolder;
    }

    public void setIdFolder(Integer idFolder) {
        this.idFolder = idFolder;
    }

    public String getNameFolder() {
        return nameFolder;
    }

    public void setNameFolder(String nameFolder) {
        this.nameFolder = nameFolder;
    }

    public UserEntity getUserCreator() {
        return userCreator;
    }

    public void setUserCreator(UserEntity userCreator) {
        this.userCreator = userCreator;
    }
}
