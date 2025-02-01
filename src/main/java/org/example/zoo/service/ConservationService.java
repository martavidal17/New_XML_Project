package org.example.zoo.service;

import org.example.zoo.model.ConservationStatistic;
import org.example.zoo.repository.ConservationRepository;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.List;

@WebService
public class ConservationService {
    private ConservationRepository repository = new ConservationRepository();

    @WebMethod
    public List<ConservationStatistic> getAllStatistics() {
        return repository.getAllConservationStatistics();
    }

    @WebMethod
    public ConservationStatistic getStatisticsByAnimalId(@WebParam(name = "animalId") String animalId, @WebParam(name = "year") int year) {
        return repository.getConservationStatistic(animalId, year);
    }

    @WebMethod
    public String addConservationStatistic(@WebParam(name = "stat") ConservationStatistic stat) {
        boolean success = repository.addConservationStatistic(stat);
        return success ? "✅ Estadística de conservación agregada exitosamente" : "❌ Error al agregar estadística";
    }

    @WebMethod
    public String updateConservationStatistic(@WebParam(name = "animalId") String animalId, @WebParam(name = "year") int year, @WebParam(name = "updatedStat") ConservationStatistic updatedStat) {
        boolean success = repository.updateConservationStatistic(animalId, year, updatedStat);
        return success ? "✅ Estadística de conservación actualizada exitosamente" : "❌ Error al actualizar estadística";
    }

    @WebMethod
    public String deleteConservationStatistic(@WebParam(name = "animalId") String animalId, @WebParam(name = "year") int year) {
        boolean success = repository.deleteConservationStatistic(animalId, year);
        return success ? "✅ Estadística de conservación eliminada exitosamente" : "❌ Error al eliminar estadística";
    }
}
