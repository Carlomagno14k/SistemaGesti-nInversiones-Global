package co.edu.uptc.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Inversion {
    private String id;
    private String inversionistId;
    private String assetId;
    private double amount;
    private double purchaseAmount;
    private LocalDate date;
    private LocalTime time;

    public Inversion(String id, String inversionistId, String assetId, double amount, double purchaseAmount,
            LocalDate date, LocalTime time) {
        this.id = id;
        this.inversionistId = inversionistId;
        this.assetId = assetId;
        this.amount = amount;
        this.purchaseAmount = purchaseAmount;
        this.date = date;
        this.time = time;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getInversionistId() {
        return inversionistId;
    }
    public void setInversionistId(String inversionistId) {
        this.inversionistId = inversionistId;
    }
    public String getAssetId() {
        return assetId;
    }
    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public double getPurchaseAmount() {
        return purchaseAmount;
    }
    public void setPurchaseAmount(double purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public LocalTime getTime() {
        return time;
    }
    public void setTime(LocalTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Inversion [id=" + id + ", inversionistId=" + inversionistId + ", assetId=" + assetId + ", amount="
                + amount + ", purchaseAmount=" + purchaseAmount + ", date=" + date + ", time=" + time + "]";
    }
}
