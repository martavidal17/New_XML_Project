package org.example.zoo.model;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "animal")
public class Animal {
    private String id;
    private String name;
    private String scientificName;
    private String habitat;
    private String diet;
    private String species;
    private String zooId;

    public Animal() {}

    public Animal(String id, String name, String scientificName, String habitat, String diet, String species, String zooId) {
        this.id = id;
        this.name = name;
        this.scientificName = scientificName;
        this.habitat = habitat;
        this.diet = diet;
        this.species = species;
        this.zooId = zooId;
    }

    @XmlAttribute(name = "id")  // 📌 Cambiado a @XmlAttribute para que se mapee desde el XML
    public String getId() {
        return id;
    }

    public void setId(String id) {
        System.out.println("🛠 Asignando ID al animal: " + id);
        this.id = id;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "scientific_name")
    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    @XmlElement(name = "habitat")
    public String getHabitat() {
        return habitat;
    }

    public void setHabitat(String habitat) {
        this.habitat = habitat;
    }

    @XmlElement(name = "diet")
    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getSpecies() {
        return species != null ? species : "N/A"; // ✅ Evitar valores nulos
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    @XmlElement(name = "zooid")
    public String getZooId() {
        return zooId;
    }

    public void setZooId(String zooId) {
        this.zooId = zooId;
    }
}
