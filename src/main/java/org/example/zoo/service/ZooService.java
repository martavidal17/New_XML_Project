package org.example.zoo.service;

import org.example.zoo.model.Zoo;
import org.example.zoo.repository.ZooRepository;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;

@WebService
public class ZooService {
    private ZooRepository repository = new ZooRepository();

    @WebMethod
    public List<Zoo> getAllZoos() {
        List<Zoo> zoos = repository.getAllZoos();
        // ✅ Log para verificar si se está enviando bien la ubicación
        for (Zoo zoo : zoos) {
            System.out.println("📡 Enviando zoo: " + zoo.getId() + " | Ubicación: " + zoo.getLocation());
        }
        return zoos;
    }


    @WebMethod
    public Zoo getZooById(@WebParam(name = "id") String id) {
        return repository.getZooById(id);
    }

    @WebMethod
    public String addZoo(@WebParam(name = "zoo") Zoo zoo) {
        boolean success = repository.addZoo(zoo);
        return success ? "✅ Zoológico agregado exitosamente" : "❌ Error al agregar zoológico";
    }

    @WebMethod
    public String updateZoo(@WebParam(name = "id") String id, @WebParam(name = "updatedZoo") Zoo updatedZoo) {
        System.out.println("📥 Recibiendo solicitud de actualización para ID: " + id);

        if (id == null || id.trim().isEmpty()) {
            System.err.println("❌ Error: ID nulo o vacío en la solicitud SOAP.");
            return "❌ Error: ID nulo o vacío en la solicitud SOAP.";
        }

        boolean success = repository.updateZoo(id, updatedZoo);
        return success ? "✅ Zoológico actualizado exitosamente" : "❌ Error al actualizar zoológico";
    }


    @WebMethod
    public String deleteZoo(@WebParam(name = "id") String id) {
        boolean success = repository.deleteZoo(id);
        return success ? "✅ Zoológico eliminado exitosamente" : "❌ Error al eliminar zoológico";
    }

}
