package org.example.zoo.repository;

import org.example.zoo.model.ConservationStatistic;
import org.example.zoo.utils.XMLParser;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;

public class ConservationRepository {
    private XMLParser parser = new XMLParser();

    /**
     * 📥 Obtener todas las estadísticas de conservación desde el XML
     */
    public List<ConservationStatistic> getAllConservationStatistics() {
        Document doc = parser.loadXML();
        if (doc == null) return new ArrayList<>();

        List<ConservationStatistic> statistics = new ArrayList<>();
        NodeList statNodes = doc.getElementsByTagName("conservation_statistic");

        for (int i = 0; i < statNodes.getLength(); i++) {
            Element statElement = (Element) statNodes.item(i);
            String animalId = statElement.getAttribute("animalid");
            int year = Integer.parseInt(statElement.getAttribute("year"));

            int populationInWild = Integer.parseInt(statElement.getElementsByTagName("population_in_wild").item(0).getTextContent());
            int populationInCaptivity = Integer.parseInt(statElement.getElementsByTagName("population_in_captivity").item(0).getTextContent());
            String status = statElement.getElementsByTagName("status").item(0).getTextContent();

            statistics.add(new ConservationStatistic(animalId, year, populationInWild, populationInCaptivity, status));
        }
        return statistics;
    }

    /**
     * 🔍 Buscar estadísticas de conservación por ID de animal y año
     */
    public ConservationStatistic getConservationStatistic(String animalId, int year) {
        return getAllConservationStatistics().stream()
                .filter(stat -> stat.getAnimalId().equals(animalId) && stat.getYear() == year)
                .findFirst()
                .orElse(null);
    }

    /**
     * ✅ Agregar una nueva estadística de conservación
     */
    public boolean addConservationStatistic(ConservationStatistic stat) {
        Document doc = parser.loadXML();
        if (doc == null) return false;

        if (getConservationStatistic(stat.getAnimalId(), stat.getYear()) != null) {
            System.err.println("❌ Error: La estadística para el animal ID " + stat.getAnimalId() + " en el año " + stat.getYear() + " ya existe.");
            return false;
        }

        Element root = doc.getDocumentElement();
        Element statElement = doc.createElement("conservation_statistic");
        statElement.setAttribute("animalid", stat.getAnimalId());
        statElement.setAttribute("year", String.valueOf(stat.getYear()));

        Element populationWild = doc.createElement("population_in_wild");
        populationWild.appendChild(doc.createTextNode(String.valueOf(stat.getPopulationInWild())));
        statElement.appendChild(populationWild);

        Element populationCaptivity = doc.createElement("population_in_captivity");
        populationCaptivity.appendChild(doc.createTextNode(String.valueOf(stat.getPopulationInCaptivity())));
        statElement.appendChild(populationCaptivity);

        Element status = doc.createElement("status");
        status.appendChild(doc.createTextNode(stat.getStatus()));
        statElement.appendChild(status);

        root.appendChild(statElement);

        boolean saved = parser.saveData(doc);
        if (saved) {
            System.out.println("✅ Estadística de conservación guardada correctamente.");
        } else {
            System.err.println("❌ Error al guardar la estadística de conservación.");
        }
        return saved;
    }

    /**
     * 🔄 Actualizar estadísticas de conservación
     */
    public boolean updateConservationStatistic(String animalId, int year, ConservationStatistic updatedStat) {
        Document doc = parser.loadXML();
        if (doc == null) return false;

        NodeList statNodes = doc.getElementsByTagName("conservation_statistic");
        for (int i = 0; i < statNodes.getLength(); i++) {
            Element statElement = (Element) statNodes.item(i);
            if (statElement.getAttribute("animalid").equals(animalId) && Integer.parseInt(statElement.getAttribute("year")) == year) {
                statElement.getElementsByTagName("population_in_wild").item(0).setTextContent(String.valueOf(updatedStat.getPopulationInWild()));
                statElement.getElementsByTagName("population_in_captivity").item(0).setTextContent(String.valueOf(updatedStat.getPopulationInCaptivity()));
                statElement.getElementsByTagName("status").item(0).setTextContent(updatedStat.getStatus());

                boolean saved = parser.saveData(doc);
                if (saved) {
                    System.out.println("✅ Estadística de conservación actualizada correctamente.");
                } else {
                    System.err.println("❌ Error al actualizar la estadística de conservación.");
                }
                return saved;
            }
        }
        System.err.println("❌ Error: No se encontró la estadística para el animal ID " + animalId + " en el año " + year);
        return false;
    }

    /**
     * ❌ Eliminar estadísticas de conservación
     */
    public boolean deleteConservationStatistic(String animalId, int year) {
        Document doc = parser.loadXML();
        if (doc == null) return false;

        NodeList statNodes = doc.getElementsByTagName("conservation_statistic");
        boolean found = false;

        for (int i = 0; i < statNodes.getLength(); i++) {
            Element statElement = (Element) statNodes.item(i);
            if (statElement.getAttribute("animalid").equals(animalId) && Integer.parseInt(statElement.getAttribute("year")) == year) {
                statElement.getParentNode().removeChild(statElement);
                found = true;
                break;
            }
        }

        if (!found) {
            System.err.println("❌ Error: No se encontró la estadística para el animal ID " + animalId + " en el año " + year + " para eliminar.");
            return false;
        }

        boolean saved = parser.saveData(doc);
        if (saved) {
            System.out.println("✅ Estadística de conservación eliminada correctamente.");
        } else {
            System.err.println("❌ Error al eliminar la estadística de conservación.");
        }
        return saved;
    }

    /**
     * 📊 Obtener estadísticas de conservación por año
     */
    public List<ConservationStatistic> getStatisticsByYear(int year) {
        List<ConservationStatistic> statistics = getAllConservationStatistics();
        List<ConservationStatistic> filteredStats = new ArrayList<>();

        for (ConservationStatistic stat : statistics) {
            if (stat.getYear() == year) {
                filteredStats.add(stat);
            }
        }

        return filteredStats;
    }
}
