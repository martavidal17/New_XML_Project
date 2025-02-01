package org.example.zoo.model;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "conservation_statistic")
public class ConservationStatistic {
    private String animalId;
    private int year;
    private int populationInWild;
    private int populationInCaptivity;
    private String status;

    public ConservationStatistic() {}

    public ConservationStatistic(String animalId, int year, int populationInWild, int populationInCaptivity, String status) {
        this.animalId = animalId;
        this.year = year;
        this.populationInWild = populationInWild;
        this.populationInCaptivity = populationInCaptivity;
        this.status = status;
    }

    @XmlAttribute(name = "animalid")  // 📌 Cambiado a @XmlAttribute para que JAXB lo lea como un atributo XML
    public String getAnimalId() {
        return animalId;
    }

    public void setAnimalId(String animalId) {
        System.out.println("🛠 Asignando ID al animal en estadísticas: " + animalId);
        this.animalId = animalId;
    }

    @XmlAttribute(name = "year")  // 📌 Año también como atributo XML
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @XmlElement(name = "population_in_wild")
    public int getPopulationInWild() {
        return populationInWild;
    }

    public void setPopulationInWild(int populationInWild) {
        this.populationInWild = populationInWild;
    }

    @XmlElement(name = "population_in_captivity")
    public int getPopulationInCaptivity() {
        return populationInCaptivity;
    }

    public void setPopulationInCaptivity(int populationInCaptivity) {
        this.populationInCaptivity = populationInCaptivity;
    }

    @XmlElement(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
