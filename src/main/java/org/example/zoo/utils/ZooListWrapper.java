package org.example.zoo.utils;

import org.example.zoo.model.Zoo;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "zoos")
public class ZooListWrapper {
    private List<Zoo> zoos;

    @XmlElement(name = "zoo") // <- Asegura que JAXB detecta cada <zoo> dentro de <zoos>
    public List<Zoo> getZoos() {
        return zoos;
    }

    public void setZoos(List<Zoo> zoos) {
        this.zoos = zoos;
    }
}
