package es.tfg.simuladorteoriacolas.items.products;

public class Product {
    private String name;

    private Integer idProduct;

    private Double arrivalTime;

    public String getName() {
        return name;
    }

    public Product(String name, Integer idProduct, Double arrivalTime) {
        this.name = name;
        this.idProduct = idProduct;
        this.arrivalTime = arrivalTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(Integer idProduct) {
        this.idProduct = idProduct;
    }

    public Double getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
