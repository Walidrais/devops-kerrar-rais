package fr.uparis.miage.carrental.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plateNumber;
    private Long customerId;
    private String customerName;
    private LocalDate beginDate;
    private LocalDate endDate;
    private int totalPrice;
    private boolean returned;

    protected Rental() {
    }

    public Rental(String plateNumber, Long customerId, String customerName,
                  LocalDate beginDate, LocalDate endDate, int totalPrice) {
        this.plateNumber = plateNumber;
        this.customerId = customerId;
        this.customerName = customerName;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
    }

    public Long getId() { return id; }
    public String getPlateNumber() { return plateNumber; }
    public Long getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public LocalDate getBeginDate() { return beginDate; }
    public LocalDate getEndDate() { return endDate; }
    public int getTotalPrice() { return totalPrice; }
    public boolean isReturned() { return returned; }
    public void setReturned(boolean returned) { this.returned = returned; }
}
