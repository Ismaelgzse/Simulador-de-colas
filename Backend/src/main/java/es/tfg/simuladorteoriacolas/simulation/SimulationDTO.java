package es.tfg.simuladorteoriacolas.simulation;

public class SimulationDTO {

    private Integer id;

    private String title;

    private String body;

    private Integer folder;

    public SimulationDTO(Integer id, String title, String body,Integer folder){
        this.id=id;
        this.title=title;
        this.body=body;
        this.folder=folder;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getFolder() {
        return folder;
    }

    public void setFolder(Integer folder) {
        this.folder = folder;
    }
}
