package co.edu.uptc.model;

import java.util.List;
import java.util.UUID;

import co.edu.uptc.model.enums.PerfilRiesgo;

public class Inversionist {

    private String id=UUID.randomUUID().toString();
    private String name;
    private String email;
    private double availableCapital;
    private PerfilRiesgo riskProfile;
    private List<Inversion> inversions;

    public Inversionist(String id, String name, String email, double availableCapital, PerfilRiesgo riskProfile,
            List<Inversion> inversions) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.availableCapital = availableCapital;
        this.riskProfile = riskProfile;
        this.inversions = inversions;
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
    public PerfilRiesgo getRiskProfile() {
        return riskProfile;
    }
    public void setRiskProfile(PerfilRiesgo riskProfile) {
        this.riskProfile = riskProfile;
    }
    public List<Inversion> getInversions() {
        return inversions;
    }
    public void setInversions(List<Inversion> inversions) {
        this.inversions = inversions;
    }

    @Override
    public String toString() {
        return "Inversionista [id=" + id + ", name=" + name + ", email=" + email + ", availableCapital="
                + availableCapital + ", riskProfile=" + riskProfile + ", inversions=" + inversions + "]";
    }    
}
