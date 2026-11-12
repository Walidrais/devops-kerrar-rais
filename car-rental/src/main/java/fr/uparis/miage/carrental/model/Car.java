package fr.uparis.miage.carrental.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String plateNumber;

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @Positive
    private int pricePerDay;

    private boolean rented;

    protected Car() {
    }

    public Car(String plateNumber, String brand, String model, int pricePerDay) {
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.pricePerDay = pricePerDay;
    }

    public Long getId() { return id; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(int pricePerDay) { this.pricePerDay = pricePerDay; }
    public boolean isRented() { return rented; }
    public void setRented(boolean rented) { this.rented = rented; }
}
