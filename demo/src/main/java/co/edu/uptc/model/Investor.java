package co.edu.uptc.model;

import java.util.List;

import co.edu.uptc.model.enums.RiskProfile;

public class Investor {

    private String id;
    private String name;
    private String email;
    private double availableCapital;
    private RiskProfile riskProfile;
    // Cambiamos "inversions" por "investments" (término financiero correcto)
    private List<Investment> investments; 

    /** Constructor vacío de la clase Investor. */
    public Investor() {
    }

    public Investor(String id, String name, String email, double availableCapital, RiskProfile riskProfile,
            List<Investment> investments) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.availableCapital = availableCapital;
        this.riskProfile = riskProfile;
        this.investments = investments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getAvailableCapital() {
        return availableCapital;
    }

    public void setAvailableCapital(double availableCapital) {
        this.availableCapital = availableCapital;
    }

    public RiskProfile getRiskProfile() {
        return riskProfile;
    }

    public void setRiskProfile(RiskProfile riskProfile) {
        this.riskProfile = riskProfile;
    }

    public List<Investment> getInvestments() {
        return investments;
    }

    public void setInvestments(List<Investment> investments) {
        this.investments = investments;
    }

    @Override
    public String toString() {
        // Aunque el toString suele ser solo para los desarrolladores, lo dejamos en inglés neutral
        return "Investor [id=" + id + ", name=" + name + ", email=" + email + ", availableCapital="
                + availableCapital + ", riskProfile=" + riskProfile + ", investments=" + investments + "]";
    }    
}