package org.example.zoo.repository;

import org.example.zoo.model.Animal;
import org.example.zoo.utils.XMLParser;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;

public class AnimalRepository {
    private XMLParser parser = new XMLParser();

    /**
     * 📥 Obtener todos los animales del XML
     */
    public List<Animal> getAllAnimals() {
        Document doc = parser.loadXML();
        if (doc == null) return new ArrayList<>();

        List<Animal> animals = new ArrayList<>();
        NodeList animalNodes = doc.getElementsByTagName("animal");

        for (int i = 0; i < animalNodes.getLength(); i++) {
            Element animalElement = (Element) animalNodes.item(i);
            String id = animalElement.getAttribute("id");
            String species = animalElement.hasAttribute("species") ? animalElement.getAttribute("species").trim() : "Desconocido";
            String zooid = animalElement.hasAttribute("zooid") ? animalElement.getAttribute("zooid").trim() : "N/A";

            String name = getElementText(animalElement, "name");
            String scientificName = getElementText(animalElement, "scientific_name");
            String habitat = getElementText(animalElement, "habitat");
            String diet = getElementText(animalElement, "diet");

            animals.add(new Animal(id, name, scientificName, habitat, diet, species, zooid));
        }
        return animals;
    }

    /**
     * 🔍 Buscar animal por ID
     */
    public Animal getAnimalById(String id) {
        return getAllAnimals().stream()
                .filter(animal -> animal.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 🆕 Generar un nuevo ID para un animal
     */
    public String generateAnimalId() {
        List<Animal> animals = getAllAnimals();
        int maxId = 0;

        for (Animal animal : animals) {
            if (animal.getId().startsWith("animal")) {
                try {
                    int num = Integer.parseInt(animal.getId().substring(6)); // Extraer número de animalNN
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return "animal" + String.format("%02d", maxId + 1); // Formato animalNN
    }

    /**
     * ✅ Agregar un nuevo animal y guardarlo en el XML
     */
    public boolean addAnimal(Animal animal) {
        Document doc = parser.loadXML();
        if (doc == null) return false;

        if (getAnimalById(animal.getId()) != null) {
            System.err.println("❌ Error: El animal con ID " + animal.getId() + " ya existe.");
            return false;
        }

        Element root = doc.getDocumentElement();
        Element animalElement = doc.createElement("animal");
        animalElement.setAttribute("id", generateAnimalId());
        animalElement.setAttribute("species", animal.getSpecies());

        // Validar el zooId antes de asignarlo
        String zooid = (animal.getZooId() != null && !animal.getZooId().isEmpty()) ? animal.getZooId() : "zoo00";
        animalElement.setAttribute("zooid", zooid);

        Element name = doc.createElement("name");
        name.appendChild(doc.createTextNode(animal.getName()));
        animalElement.appendChild(name);

        Element scientificName = doc.createElement("scientific_name");
        scientificName.appendChild(doc.createTextNode(animal.getScientificName()));
        animalElement.appendChild(scientificName);

        Element habitat = doc.createElement("habitat");
        habitat.appendChild(doc.createTextNode(animal.getHabitat()));
        animalElement.appendChild(habitat);

        Element diet = doc.createElement("diet");
        diet.appendChild(doc.createTextNode(animal.getDiet()));
        animalElement.appendChild(diet);

        root.appendChild(animalElement);

        boolean saved = parser.saveData(doc);
        if (saved) {
            System.out.println("✅ Animal guardado correctamente.");
        } else {
            System.err.println("❌ Error al guardar el animal.");
        }
        return saved;
    }

    /**
     * 🔄 Actualizar un animal existente
     */
    public boolean updateAnimal(String id, Animal updatedAnimal) {
        Document doc = parser.loadXML();
        if (doc == null) return false;

        NodeList animals = doc.getElementsByTagName("animal");
        boolean found = false;

        for (int i = 0; i < animals.getLength(); i++) {
            Element animal = (Element) animals.item(i);
            String xmlId = animal.getAttribute("id");

            if (xmlId.equals(id)) {
                found = true;

                System.out.println("🔄 Actualizando animal con ID: " + id);

                // ✅ Depuración: Mostrar valores recibidos
                System.out.println("📌 Species recibido: " + updatedAnimal.getSpecies());
                System.out.println("📌 ZooId recibido: " + updatedAnimal.getZooId());


                String newZooId = (updatedAnimal.getZooId() != null && !updatedAnimal.getZooId().trim().isEmpty())
                        ? updatedAnimal.getZooId().trim()
                        : "zoo00";

                // 📌 Asegurar que se actualiza species correctamente
                animal.setAttribute("species", updatedAnimal.getSpecies());
                animal.setAttribute("zooid", newZooId);

                // 📌 Actualizar otros campos
                if (updatedAnimal.getName() != null)
                    animal.getElementsByTagName("name").item(0).setTextContent(updatedAnimal.getName());
                if (updatedAnimal.getScientificName() != null)
                    animal.getElementsByTagName("scientific_name").item(0).setTextContent(updatedAnimal.getScientificName());
                if (updatedAnimal.getHabitat() != null)
                    animal.getElementsByTagName("habitat").item(0).setTextContent(updatedAnimal.getHabitat());
                if (updatedAnimal.getDiet() != null)
                    animal.getElementsByTagName("diet").item(0).setTextContent(updatedAnimal.getDiet());

                // ✅ Guardar cambios en XML
                return parser.saveData(doc);
            }
        }

        System.out.println("❌ No se encontró animal con ID: " + id);
        return false;
    }

    /**
     * 🛠 Método auxiliar para obtener texto de un nodo XML
     */
    private String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        return (nodeList.getLength() > 0) ? nodeList.item(0).getTextContent() : "";
    }

    /**
     * ❌ Eliminar un animal
     */
    public boolean deleteAnimal(String id) {
        Document doc = parser.loadXML();
        if (doc == null) return false;

        NodeList animals = doc.getElementsByTagName("animal");
        boolean found = false;

        for (int i = 0; i < animals.getLength(); i++) {
            Element animal = (Element) animals.item(i);
            if (animal.getAttribute("id").equals(id)) {
                animal.getParentNode().removeChild(animal);
                found = true;
                break;
            }
        }

        if (!found) {
            System.err.println("❌ Error: No se encontró un animal con ID " + id + " para eliminar.");
            return false;
        }

        boolean saved = parser.saveData(doc);
        if (saved) {
            System.out.println("✅ Animal eliminado correctamente.");
        } else {
            System.err.println("❌ Error al eliminar el animal.");
        }
        return saved;
    }


}
