package es.tfg.simuladorteoriacolas.folder;

import java.util.List;

public class FolderDTO {
    private Integer id;

    private String name;

    public FolderDTO(Integer id, String name){
        this.id=id;
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
