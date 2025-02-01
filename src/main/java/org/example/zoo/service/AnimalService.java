package org.example.zoo.service;

import jakarta.jws.soap.SOAPBinding;
import org.example.zoo.model.Animal;
import org.example.zoo.repository.AnimalRepository;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;
@WebService
public class AnimalService {
    private AnimalRepository repository = new AnimalRepository();

    @WebMethod
    public List<Animal> getAllAnimals() {
        List<Animal> animals = repository.getAllAnimals();

        // ✅ Agregar logs para depurar los datos recuperados
        System.out.println("📥 Animales obtenidos de la BD XML:");
        for (Animal a : animals) {
            System.out.println("ID: " + a.getId() + ", Especie: " + a.getSpecies() + ", ZooID: " + a.getZooId());
        }

        return animals;
    }


    @WebMethod
    public Animal getAnimalById(@WebParam(name = "id") String id) {
        return repository.getAnimalById(id);
    }

    @WebMethod
    public String addAnimal(@WebParam(name = "animal") Animal animal) {
        boolean success = repository.addAnimal(animal);
        return success ? "✅ Animal agregado exitosamente" : "❌ Error al agregar animal";
    }


    @WebMethod
    public boolean updateAnimal(@WebParam(name = "id") String id, @WebParam(name = "updatedAnimal") Animal updatedAnimal) {
        if (updatedAnimal == null) {
            System.err.println("❌ Error: updatedAnimal es null.");
            return false;
        }

        // ✅ Depurar valores antes de pasarlos al repositorio
        System.out.println("📥 Recibido animal para actualizar:");
        System.out.println("ID: " + id);
        System.out.println("Name: " + updatedAnimal.getName());
        System.out.println("Scientific Name: " + updatedAnimal.getScientificName());
        System.out.println("Species: " + updatedAnimal.getSpecies());  // 🔥 Verificar que no sea null
        System.out.println("Zoo ID: " + updatedAnimal.getZooId());

        return repository.updateAnimal(id, updatedAnimal);
    }





    @WebMethod
public String deleteAnimal(@WebParam(name = "id") String id) {
    boolean success = repository.deleteAnimal(id);
    return success ? "✅ Animal eliminado exitosamente" : "❌ Error al eliminar animal";
}

}
