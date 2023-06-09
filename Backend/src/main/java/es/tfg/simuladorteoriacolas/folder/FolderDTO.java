package es.tfg.simuladorteoriacolas.folder;

public class FolderDTO {
    private Integer idFolder;

    private String nameFolder;

    public FolderDTO(Integer id, String name){
        this.idFolder =id;
        this.nameFolder =name;
    }

    public String getNameFolder() {
        return nameFolder;
    }

    public void setNameFolder(String nameFolder) {
        this.nameFolder = nameFolder;
    }

    public Integer getIdFolder() {
        return idFolder;
    }

    public void setIdFolder(Integer idFolder) {
        this.idFolder = idFolder;
    }
}
