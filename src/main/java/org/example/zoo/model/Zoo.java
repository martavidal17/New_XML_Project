package org.example.zoo.model;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "zoo")
public class Zoo {
    private String id;
    private String name;
    private String city;
    private int foundation;
    private String location; // 🔥 Asegurar que existe

    public Zoo() {}

    public Zoo(String id, String name, String city, int foundation, String location) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.foundation = foundation;
        this.location = location;
    }

    @XmlAttribute(name = "id")
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @XmlElement(name = "name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @XmlElement(name = "city")
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    @XmlElement(name = "foundation")
    public int getFoundation() { return foundation; }
    public void setFoundation(int foundation) { this.foundation = foundation; }

    public String getLocation() {
        return location != null ? location : "N/A"; // ✅ Evitar valores nulos
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
