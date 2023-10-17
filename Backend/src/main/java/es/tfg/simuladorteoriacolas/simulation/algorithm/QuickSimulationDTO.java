package es.tfg.simuladorteoriacolas.simulation.algorithm;

public class QuickSimulationDTO {
    private Double timeSimulation;

    private Integer numberSimulations;

    private Boolean pdfFormat;

    private Boolean csvFormat;

    public QuickSimulationDTO(Double timeSimulation, Integer numberSimulations, Boolean pdfFormat, Boolean csvFormat){
        this.timeSimulation=timeSimulation;
        this.numberSimulations=numberSimulations;
        this.pdfFormat=pdfFormat;
        this.csvFormat=csvFormat;
    }

    public Double getTimeSimulation() {
        return timeSimulation;
    }

    public void setTimeSimulation(Double timeSimulation) {
        this.timeSimulation = timeSimulation;
    }

    public Integer getNumberSimulations() {
        return numberSimulations;
    }

    public void setNumberSimulations(Integer numberSimulations) {
        this.numberSimulations = numberSimulations;
    }

    public Boolean getPdfFormat() {
        return pdfFormat;
    }

    public void setPdfFormat(Boolean pdfFormat) {
        this.pdfFormat = pdfFormat;
    }

    public Boolean getCsvFormat() {
        return csvFormat;
    }

    public void setCsvFormat(Boolean csvFormat) {
        this.csvFormat = csvFormat;
    }
}
