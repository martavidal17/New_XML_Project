package org.example.zoo.utils;

import org.example.zoo.model.Animal;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "zoos")
public class AnimalListWrapper {
    private List<Animal> animals;

    @XmlElement(name = "animal")
    public List<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }
}
