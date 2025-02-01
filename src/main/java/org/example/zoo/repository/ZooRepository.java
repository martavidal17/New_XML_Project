package org.example.zoo.repository;

import org.example.zoo.model.Zoo;
import org.example.zoo.utils.XMLParser;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;

public class ZooRepository {
    private XMLParser parser = new XMLParser();

    public List<Zoo> getAllZoos() {
        Document doc = parser.loadXML();
        if (doc == null) return new ArrayList<>();

        List<Zoo> zoos = new ArrayList<>();
        NodeList zooNodes = doc.getElementsByTagName("zoo");

        for (int i = 0; i < zooNodes.getLength(); i++) {
            Element zooElement = (Element) zooNodes.item(i);
            String id = zooElement.getAttribute("id");

            // 🔥 Asegurar que 'location' se extrae correctamente del atributo
            String location = zooElement.getAttribute("location").trim();
            if (location.isEmpty()) location = "Desconocido"; // 👈 Si está vacío, asignar "Desconocido"

            String name = zooElement.getElementsByTagName("name").item(0).getTextContent();
            String city = zooElement.getElementsByTagName("city").item(0).getTextContent();
            int foundation = Integer.parseInt(zooElement.getElementsByTagName("foundation").item(0).getTextContent());

            // ✅ Verificar en la consola del servidor si la ubicación es correcta
            System.out.println("📌 Cargando zoológico: " + id + " | Ubicación: " + location);

            zoos.add(new Zoo(id, name, city, foundation, location));
        }
        return zoos;
    }


    public Zoo getZooById(String id) {
        return getAllZoos().stream()
                .filter(zoo -> zoo.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public String generateZooId() {
        List<Zoo> zoos = getAllZoos();
        int maxId = 0;

        for (Zoo zoo : zoos) {
            if (zoo.getId().startsWith("zoo")) {
                try {
                    int num = Integer.parseInt(zoo.getId().substring(3)); // Extraer número de zooNN
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return "zoo" + String.format("%02d", maxId + 1); // Formato zooNN
    }

    /**
     * 🏛️ Agregar zoológico y guardar en el XML
     */
    public boolean addZoo(Zoo zoo) {
        Document doc = parser.loadXML();
        if (doc == null) return false;

        System.out.println("📥 Recibiendo zoológico para agregar: " + zoo.getName());

        if (zoo.getLocation() == null || zoo.getLocation().trim().isEmpty()) {
            System.err.println("❌ Error: Ubicación no válida para el zoológico.");
            return false;
        }

        Element root = doc.getDocumentElement();
        Element zooElement = doc.createElement("zoo");
        zooElement.setAttribute("id", generateZooId());
        zooElement.setAttribute("location", zoo.getLocation()); // 🔥 Asegurando que location no sea null

        Element name = doc.createElement("name");
        name.appendChild(doc.createTextNode(zoo.getName()));
        zooElement.appendChild(name);

        Element city = doc.createElement("city");
        city.appendChild(doc.createTextNode(zoo.getCity()));
        zooElement.appendChild(city);

        Element foundation = doc.createElement("foundation");
        foundation.appendChild(doc.createTextNode(String.valueOf(zoo.getFoundation())));
        zooElement.appendChild(foundation);

        root.appendChild(zooElement);

        boolean saved = parser.saveData(doc); // 🔥 GUARDANDO CAMBIOS AQUÍ
        if (saved) {
            System.out.println("✅ Zoológico guardado con éxito.");
        } else {
            System.err.println("❌ Error al guardar el zoológico en XML.");
        }
        return saved;
    }

    public boolean updateZoo(String id, Zoo updatedZoo) {
        Document doc = parser.loadXML();
        if (doc == null) return false;

        NodeList zoos = doc.getElementsByTagName("zoo");
        boolean found = false;

        for (int i = 0; i < zoos.getLength(); i++) {
            Element zoo = (Element) zoos.item(i);
            String xmlId = zoo.getAttribute("id");

            System.out.println("🔎 Buscando zoológico con ID: " + id + " | Encontrado en XML: " + xmlId);

            if (xmlId.equals(id)) {
                found = true;
                System.out.println("✅ Encontrado zoológico con ID: " + id + " - Actualizando...");

                zoo.setAttribute("location", updatedZoo.getLocation());
                zoo.getElementsByTagName("name").item(0).setTextContent(updatedZoo.getName());
                zoo.getElementsByTagName("city").item(0).setTextContent(updatedZoo.getCity());
                zoo.getElementsByTagName("foundation").item(0).setTextContent(String.valueOf(updatedZoo.getFoundation()));

                boolean saved = parser.saveData(doc);
                if (saved) {
                    System.out.println("✅ Zoológico actualizado correctamente.");
                    return true;
                } else {
                    System.err.println("❌ Error al guardar cambios en el XML.");
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * ❌ Eliminar zoológico del XML y guardar los cambios
     */

    public boolean deleteZoo(String id) {
        Document doc = parser.loadXML();
        if (doc == null) return false;

        NodeList zoos = doc.getElementsByTagName("zoo");
        boolean zooDeleted = false;

        for (int i = 0; i < zoos.getLength(); i++) {
            Element zoo = (Element) zoos.item(i);
            if (zoo.getAttribute("id").equals(id)) {
                zoo.getParentNode().removeChild(zoo);
                zooDeleted = true;
                System.out.println("✅ Zoológico con ID " + id + " eliminado.");
                break;
            }
        }

        if (!zooDeleted) {
            System.err.println("❌ No se encontró zoológico con ID " + id);
            return false;
        }

        // 🔥 Ahora eliminamos los animales asociados al zoológico eliminado
        NodeList animals = doc.getElementsByTagName("animal");
        for (int i = animals.getLength() - 1; i >= 0; i--) {
            Element animal = (Element) animals.item(i);
            if (animal.getAttribute("zooid").equals(id)) {
                animal.getParentNode().removeChild(animal);
                System.out.println("🗑 Animal con ID " + animal.getAttribute("id") + " eliminado porque pertenecía al zoológico " + id);
            }
        }

        return parser.saveData(doc); // 🔥 Guardamos los cambios en el XML
    }

}
