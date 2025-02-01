package org.example.zoo.service;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import org.example.zoo.model.Animal;
import org.example.zoo.utils.XQueryExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebService
public class XQueryService {
    private XQueryExecutor executor = new XQueryExecutor();

    @WebMethod
    public String getZoosByLocation(@WebParam(name = "location") String location) {
        Map<String, String> params = new HashMap<>();
        params.put("location", location);

        return executor.executeQuery("findZoosByLocation.xql", params);
    }

    @WebMethod
    public String getAnimalsBySpecies(@WebParam(name = "species") String species) {
        Map<String, String> params = new HashMap<>();
        params.put("species", species);

        return executor.executeQuery("findAnimalsByLocation.xql", params);
    }




}
