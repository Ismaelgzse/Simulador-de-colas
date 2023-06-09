package es.tfg.simuladorteoriacolas.folder;

import es.tfg.simuladorteoriacolas.simulation.Simulation;

import java.util.List;

public class FolderWithPagedSimulationsDTO {
    private Integer idFolder;

    private String nameFolder;

    private List<Simulation> simulations;

    private Boolean isLastPage;

    public FolderWithPagedSimulationsDTO(Integer idFolder, String nameFolder, List<Simulation> simulations,Boolean last){
        this.idFolder=idFolder;
        this.nameFolder=nameFolder;
        this.simulations=simulations;
        this.isLastPage =last;
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

    public List<Simulation> getSimulations() {
        return simulations;
    }

    public void setSimulations(List<Simulation> simulations) {
        this.simulations = simulations;
    }

    public Boolean getIsLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(Boolean isLastPage) {
        this.isLastPage = isLastPage;
    }
}
