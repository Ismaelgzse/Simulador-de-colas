package es.tfg.simuladorteoriacolas.simulation;

public class SimulationDTO {

    private Integer idSimulation;

    private String title;

    private String body;

    private Integer folderId;

    public SimulationDTO(Integer id, String title, String body,Integer folderId){
        this.idSimulation =id;
        this.title=title;
        this.body=body;
        this.folderId=folderId;
    }

    public Integer getIdSimulation() {
        return idSimulation;
    }

    public void setIdSimulation(Integer idSimulation) {
        this.idSimulation = idSimulation;
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

    public Integer getFolderId() {
        return folderId;
    }

    public void setFolderId(Integer folderId) {
        this.folderId = folderId;
    }
}
