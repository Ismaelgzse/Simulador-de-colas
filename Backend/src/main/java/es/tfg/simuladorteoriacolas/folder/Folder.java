package es.tfg.simuladorteoriacolas.folder;

import es.tfg.simuladorteoriacolas.user.User;
import jakarta.persistence.*;
import lombok.Builder;

@Entity
@Builder
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idFolder;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    private User userCreator;

    public Folder() {

    }

    public Folder(String name, User userCreator){
        this.name=name;
        this.userCreator=userCreator;
    }

    public Integer getIdFolder() {
        return idFolder;
    }

    public void setIdFolder(Integer idFolder) {
        this.idFolder = idFolder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUserCreator() {
        return userCreator;
    }

    public void setUserCreator(User userCreator) {
        this.userCreator = userCreator;
    }
}
