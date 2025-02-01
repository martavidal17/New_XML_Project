// Variables globales
let zooList = [];
let animalList = [];

// Lista fija de continentes (para zoológicos)
const continentList = ["Africa", "America", "Asia", "Europe", "Oceania"];

// Lista fija de especies (ajusta según sea necesario)
const speciesList = ["all", "Mammal", "Bird", "Reptile", "Fish", "Amphibian", "Insect"];

/**
 * Función que inicia la aplicación:
 * - Registra los eventos de botones y filtros.
 * - Carga datos iniciales (zoológicos para selectores y filtros).
 */
document.addEventListener("DOMContentLoaded", init);

async function init() {
    // Botones de control
    const loadZoosBtn = document.getElementById("loadZoos");
    const addZooBtn = document.getElementById("addZoo");
    const loadAnimalsBtn = document.getElementById("loadAnimals");
    const loadStatisticsBtn = document.getElementById("loadStatistics"); // Stub

    if (loadZoosBtn) loadZoosBtn.addEventListener("click", () => fetchZoos(true));
    if (addZooBtn) addZooBtn.addEventListener("click", addZoo);
    if (loadAnimalsBtn)
        loadAnimalsBtn.addEventListener("click", () => fetchAnimals(true));
    if (loadStatisticsBtn)
        loadStatisticsBtn.addEventListener("click", fetchStatistics);

    // Botón de filtrado (para zoológicos)
    document
        .getElementById("filterZoos")
        .addEventListener("click", filterZoos);

    // Cargar inicialmente los zoológicos (sin renderizar tabla) para llenar selectores y filtros
    await fetchZoos(false);
    generateZooSelector();
    generateContinentFilterSelector();
    populateZooLocationSelect(); // Para el formulario de agregar zoo
}


// Después de cargar zoológicos en init(), también generamos el filtro para especies:
document.addEventListener("DOMContentLoaded", async () => {
    await init();
    generateSpeciesFilterSelector();
    // Agregar evento para el botón de filtrar animales por especie:
    const filterSpeciesBtn = document.getElementById("filterAnimalsBySpecies");
    if (filterSpeciesBtn) {
        filterSpeciesBtn.addEventListener("click", filterAnimalsBySpecies);
    }
});

/**
 * Genera el selector de especies para filtrar animales.
 */
function generateSpeciesFilterSelector() {
    const container = document.getElementById("filterSpeciesContainer");
    if (!container) return;
    let selectHTML = '<select id="filterSpecies">';
    speciesList.forEach((species) => {
        // Si se desea que la opción "all" tenga un nombre especial, se puede hacer:
        if (species === "all") {
            selectHTML += `<option value="all" selected>Mostrar Todos</option>`;
        } else {
            selectHTML += `<option value="${species}">${species}</option>`;
        }
    });
    selectHTML += "</select>";
    container.innerHTML = selectHTML;
}

/**
 * Función para filtrar animales por especie.
 */
async function filterAnimalsBySpecies() {
    const selectedSpecies = document.getElementById("filterSpecies").value;
    if (selectedSpecies === "all") {
        await fetchAnimals(true);
        return;
    }
    const apiAnimalUrl = "http://localhost:8082/ws/animal";
    const soapRequest = `
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:org="http://service.zoo.example.org/">
      <soapenv:Header/>
      <soapenv:Body>
        <org:getAnimalsBySpecies>
          <species>${selectedSpecies}</species>
        </org:getAnimalsBySpecies>
      </soapenv:Body>
    </soapenv:Envelope>
  `;
    try {
        const data = await sendSOAPRequest(apiAnimalUrl, soapRequest);
        animalList = Array.from(data.getElementsByTagName("return")).map((animal) => {
            const id = animal.getAttribute("id") || "N/A";
            const name = animal.getElementsByTagName("name")[0]?.textContent?.trim() || "N/A";
            const scientificName = animal.getElementsByTagName("scientific_name")[0]?.textContent?.trim() || "N/A";
            const habitat = animal.getElementsByTagName("habitat")[0]?.textContent?.trim() || "N/A";
            const diet = animal.getElementsByTagName("diet")[0]?.textContent?.trim() || "N/A";
            const speciesNode = animal.getElementsByTagName("species")[0];
            const zooIdNode = animal.getElementsByTagName("zooid")[0];
            const species = speciesNode ? speciesNode.textContent.trim() : (animal.getAttribute("species")?.trim() || "Desconocido");
            const zooId = zooIdNode ? zooIdNode.textContent.trim() : (animal.getAttribute("zooid")?.trim() || "N/A");
            return { id, name, scientificName, habitat, diet, species, zooId };
        });
        renderAnimalsTable(animalList);
    } catch (error) {
        console.error("Error al filtrar animales por especie:", error);
    }
}

/**
 * Realiza una solicitud SOAP y devuelve el XML parseado.
 */
async function sendSOAPRequest(endpoint, soapRequest) {
    try {
        const response = await fetch(endpoint, {
            method: "POST",
            headers: { "Content-Type": "text/xml" },
            body: soapRequest,
        });
        const responseText = await response.text();
        return new window.DOMParser().parseFromString(responseText, "text/xml");
    } catch (error) {
        console.error("❌ Error en la solicitud SOAP:", error);
        showErrorMessage("Error en la solicitud SOAP.");
        throw error;
    }
}

/**
 * Carga todos los zoológicos.
 * @param {boolean} renderTable - Si es true, renderiza la tabla de zoológicos.
 */
async function fetchZoos(renderTable = false) {
    console.log("📡 Cargando zoológicos...");
    const apiZooUrl = "http://localhost:8082/ws/zoo";
    const soapRequest = `
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                      xmlns:org="http://service.zoo.example.org/">
      <soapenv:Header/>
      <soapenv:Body>
        <org:getAllZoos/>
      </soapenv:Body>
    </soapenv:Envelope>
  `;
    try {
        const data = await sendSOAPRequest(apiZooUrl, soapRequest);
        zooList = Array.from(data.getElementsByTagName("return")).map((zoo) => ({
            id: zoo.getAttribute("id") || "N/A",
            name: zoo.getElementsByTagName("name")[0]?.textContent || "N/A",
            city: zoo.getElementsByTagName("city")[0]?.textContent || "N/A",
            foundation:
                zoo.getElementsByTagName("foundation")[0]?.textContent || "N/A",
            location:
                zoo.getElementsByTagName("location")[0]?.textContent || "Desconocido",
        }));
        console.log("✅ Zoológicos cargados:", zooList);
        // Actualizamos ambos selectores (el de filtrar y el del formulario)
        generateZooSelector();
        generateContinentFilterSelector();
        if (renderTable) {
            renderZoosTable(zooList);
            updateLoadZoosButton(true);
        } else {
            updateLoadZoosButton(false);
        }
    } catch (error) {
        console.error("Error al cargar zoológicos:", error);
    }
}

/**
 * Renderiza la tabla de zoológicos.
 */
function renderZoosTable(zooData) {
    const table = document.getElementById("zoosTable");
    if (!table) return;
    const rows = zooData
        .map(
            (z) => `
      <tr data-id="${z.id}">
        <td>${z.id}</td>
        <td><input type="text" data-field="name" value="${z.name}" onchange="updateZoo('${z.id}', 'name', this.value)"></td>
        <td><input type="text" data-field="city" value="${z.city}" onchange="updateZoo('${z.id}', 'city', this.value)"></td>
        <td><input type="number" data-field="foundation" value="${z.foundation}" onchange="updateZoo('${z.id}', 'foundation', this.value)"></td>
        <td>
          <select data-field="location" onchange="updateZoo('${z.id}', 'location', this.value)">
            ${generateLocationOptions(z.location)}
          </select>
        </td>
        <td><button onclick="deleteZoo('${z.id}')">🗑 Eliminar</button></td>
      </tr>
    `
        )
        .join("");
    table.innerHTML = `
    <tr>
      <th>ID</th>
      <th>Nombre</th>
      <th>Ciudad</th>
      <th>Fundación</th>
      <th>Ubicación</th>
      <th>Acciones</th>
    </tr>
    ${rows}
  `;
}

/**
 * Crea las opciones del select para las ubicaciones (usado en tablas).
 * Usa el listado fijo de continentes.
 */
function generateLocationOptions(selectedLocation) {
    selectedLocation = selectedLocation
        ? selectedLocation.trim().toLowerCase()
        : "desconocido";
    return continentList
        .map(
            (loc) => `
      <option value="${loc}" ${
                loc.toLowerCase() === selectedLocation ? "selected" : ""
            }>${loc}</option>
    `
        )
        .join("");
}

/**
 * Renderiza la tabla de animales.
 */
function renderAnimalsTable(animalData) {
    const table = document.getElementById("animalsTable");
    if (!table) return;
    const rows = animalData
        .map(
            (a) => `
      <tr data-id="${a.id}">
        <td>${a.id}</td>
        <td><input type="text" data-field="name" value="${a.name}" onchange="updateAnimal('${a.id}', 'name', this.value)"></td>
        <td><input type="text" data-field="scientificName" value="${a.scientificName}" onchange="updateAnimal('${a.id}', 'scientificName', this.value)"></td>
        <td><input type="text" data-field="habitat" value="${a.habitat}" onchange="updateAnimal('${a.id}', 'habitat', this.value)"></td>
        <td><input type="text" data-field="diet" value="${a.diet}" onchange="updateAnimal('${a.id}', 'diet', this.value)"></td>
        <td><input type="text" data-field="species" value="${a.species}" onchange="updateAnimal('${a.id}', 'species', this.value)"></td>
        <td>
          <select data-field="zooid" onchange="updateAnimal('${a.id}', 'zooid', this.value)">
            ${generateZooOptions(a.zooId)}
          </select>
        </td>
        <td><button onclick="deleteAnimal('${a.id}')">🗑 Eliminar</button></td>
      </tr>
    `
        )
        .join("");
    table.innerHTML = `
    <tr>
      <th>ID</th>
      <th>Nombre</th>
      <th>Nombre Científico</th>
      <th>Hábitat</th>
      <th>Dieta</th>
      <th>Especie</th>
      <th>Zoológico</th>
      <th>Acciones</th>
    </tr>
    ${rows}
  `;
}

/**
 * Carga todos los animales.
 * @param {boolean} renderTable - Si es true, renderiza la tabla de animales.
 */
async function fetchAnimals(renderTable = false) {
    if (zooList.length === 0) {
        console.log("🔄 Cargando zoológicos antes de animales...");
        await fetchZoos(false);
    }
    console.log("📡 Cargando animales...");
    const apiAnimalUrl = "http://localhost:8082/ws/animal";
    const soapRequest = `
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                      xmlns:org="http://service.zoo.example.org/">
      <soapenv:Header/>
      <soapenv:Body>
        <org:getAllAnimals/>
      </soapenv:Body>
    </soapenv:Envelope>
  `;
    try {
        const data = await sendSOAPRequest(apiAnimalUrl, soapRequest);
        animalList = Array.from(data.getElementsByTagName("return")).map((animal) => {
            const id = animal.getAttribute("id") || "N/A";
            const name =
                animal.getElementsByTagName("name")[0]?.textContent?.trim() || "N/A";
            const scientificName =
                animal.getElementsByTagName("scientific_name")[0]?.textContent?.trim() || "N/A";
            const habitat =
                animal.getElementsByTagName("habitat")[0]?.textContent?.trim() || "N/A";
            const diet =
                animal.getElementsByTagName("diet")[0]?.textContent?.trim() || "N/A";
            const speciesNode = animal.getElementsByTagName("species")[0];
            const zooIdNode = animal.getElementsByTagName("zooid")[0];
            const species = speciesNode
                ? speciesNode.textContent.trim()
                : (animal.getAttribute("species")?.trim() || "Desconocido");
            const zooId = zooIdNode
                ? zooIdNode.textContent.trim()
                : (animal.getAttribute("zooid")?.trim() || "N/A");
            return { id, name, scientificName, habitat, diet, species, zooId };
        });
        console.log("🐾 Lista de animales procesada:", animalList);
        if (renderTable) {
            renderAnimalsTable(animalList);
            updateLoadAnimalsButton(true);
        } else {
            updateLoadAnimalsButton(false);
        }
    } catch (error) {
        console.error("Error al cargar animales:", error);
    }
}

/**
 * Genera las opciones del select de zoológicos para el formulario de animales.
 */
function generateZooOptions(selectedZooId) {
    if (!zooList.length)
        return `<option value="N/A">No hay zoológicos</option>`;
    return zooList
        .map(
            (zoo) => `
      <option value="${zoo.id}" ${
                zoo.id === selectedZooId ? "selected" : ""
            }>${zoo.name} (${zoo.city})</option>
    `
        )
        .join("");
}

/**
 * Genera el selector de zoológicos para el formulario de animales.
 */
function generateZooSelector() {
    const zooSelect = document.getElementById("animalZooId");
    if (!zooSelect) {
        console.warn("⚠ No se encontró el selector de zoológicos (animalZooId)");
        return;
    }
    zooSelect.innerHTML =
        '<option value="default" disabled selected>Seleccione un Zoológico</option>';
    if (zooList.length === 0) {
        zooSelect.innerHTML =
            '<option value="default" disabled>No hay zoológicos disponibles</option>';
        return;
    }
    zooList.forEach((zoo) => {
        const option = document.createElement("option");
        option.value = zoo.id;
        option.textContent = `${zoo.name} (${zoo.city})`;
        zooSelect.appendChild(option);
    });
    console.log("✅ Selector de zoológicos actualizado.");
}

/**
 * Genera el selector de continentes para filtrar zoológicos (fijo).
 */
function generateContinentFilterSelector() {
    const container = document.getElementById("filterContinentContainer");
    if (!container) return;
    let selectHTML = '<select id="filterZooLocation">';
    selectHTML += '<option value="all" selected>Mostrar Todos</option>';
    continentList.forEach((continent) => {
        selectHTML += `<option value="${continent}">${continent}</option>`;
    });
    selectHTML += "</select>";
    container.innerHTML = selectHTML;
}

/**
 * Llena el selector del formulario de agregar zoo con los continentes fijos.
 */
function populateZooLocationSelect() {
    const zooLocationSelect = document.getElementById("zooLocation");
    if (!zooLocationSelect) return;
    zooLocationSelect.innerHTML =
        '<option value="default" selected disabled>-- Seleccione un continente --</option>';
    continentList.forEach((continent) => {
        zooLocationSelect.innerHTML += `<option value="${continent}">${continent}</option>`;
    });
}

/**
 * Actualiza el estilo y texto del botón de carga de zoológicos.
 */
function updateLoadZoosButton(isLoaded) {
    const btn = document.getElementById("loadZoos");
    if (btn) {
        if (isLoaded) {
            btn.style.backgroundColor = "#66BB6A";
            btn.innerHTML = "✅ Tabla de Zoológicos Cargada";
        } else {
            btn.style.backgroundColor = "";
            btn.innerHTML = "Cargar Zoológicos";
        }
    }
}

/**
 * Actualiza el estilo y texto del botón de carga de animales.
 */
function updateLoadAnimalsButton(isLoaded) {
    const btn = document.getElementById("loadAnimals");
    if (btn) {
        if (isLoaded) {
            btn.style.backgroundColor = "#66BB6A";
            btn.innerHTML = "✅ Tabla de Animales Cargada";
        } else {
            btn.style.backgroundColor = "";
            btn.innerHTML = "Cargar Animales";
        }
    }
}

/**
 * Agrega un nuevo zoológico.
 */
async function addZoo() {
    const name = document.getElementById("zooName").value.trim();
    const city = document.getElementById("zooCity").value.trim();
    const foundation = document.getElementById("zooFoundation").value.trim();
    const location = document.getElementById("zooLocation").value;
    if (!name || !city || !foundation || location === "default") {
        showErrorMessage("⚠ Todos los campos son obligatorios.");
        return;
    }
    const apiZooUrl = "http://localhost:8082/ws/zoo";
    const soapRequest = `
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                      xmlns:org="http://service.zoo.example.org/">
      <soapenv:Header/>
      <soapenv:Body>
        <org:addZoo>
          <zoo>
            <name>${name}</name>
            <city>${city}</city>
            <foundation>${foundation}</foundation>
            <location>${location}</location>
          </zoo>
        </org:addZoo>
      </soapenv:Body>
    </soapenv:Envelope>
  `;
    try {
        await sendSOAPRequest(apiZooUrl, soapRequest);
        showSuccessMessage("✅ Zoológico agregado correctamente.");
        await fetchZoos(false);
        resetZooForm();
    } catch (error) {
        console.error("Error al agregar zoológico:", error);
    }
}

/**
 * Actualiza un zoológico.
 */
async function updateZoo(id, field, newValue) {
    console.log(`✏️ Actualizando zoológico ID: ${id}, campo: ${field}, nuevo valor: ${newValue}`);
    const row = document.querySelector(`tr[data-id="${id}"]`);
    if (!row) {
        console.error(`❌ No se encontró la fila del zoológico con ID: ${id}`);
        return;
    }
    let name = row.querySelector('input[data-field="name"]').value;
    let city = row.querySelector('input[data-field="city"]').value;
    let foundation = row.querySelector('input[data-field="foundation"]').value;
    let location = row.querySelector('select[data-field="location"]').value;
    if (field === "name") name = newValue;
    if (field === "city") city = newValue;
    if (field === "foundation") foundation = newValue;
    if (field === "location") location = newValue;
    const apiZooUrl = "http://localhost:8082/ws/zoo";
    const soapRequest = `
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                      xmlns:org="http://service.zoo.example.org/">
      <soapenv:Header/>
      <soapenv:Body>
        <org:updateZoo>
          <id>${id}</id>
          <updatedZoo>
            <name>${name}</name>
            <city>${city}</city>
            <foundation>${foundation}</foundation>
            <location>${location}</location>
          </updatedZoo>
        </org:updateZoo>
      </soapenv:Body>
    </soapenv:Envelope>
  `;
    try {
        await sendSOAPRequest(apiZooUrl, soapRequest);
        console.log("✅ Zoológico actualizado correctamente.");
    } catch (error) {
        console.error("Error al actualizar zoológico:", error);
    }
}

/**
 * Agrega un nuevo animal.
 */
async function addAnimal() {
    if (zooList.length === 0) {
        await fetchZoos(false);
        generateZooSelector();
    }
    const name = document.getElementById("animalName").value.trim();
    const scientificName = document.getElementById("animalScientificName").value.trim();
    const habitat = document.getElementById("animalHabitat").value.trim();
    const diet = document.getElementById("animalDiet").value.trim();
    const species = document.getElementById("animalSpecies").value.trim();
    const zooid = document.getElementById("animalZooId").value.trim();
    if (!name || !scientificName || !habitat || !diet || !species || !zooid) {
        showErrorMessage("⚠ Todos los campos son obligatorios.");
        return;
    }
    if (!zooList.some((zoo) => zoo.id === zooid)) {
        showErrorMessage(`⚠ Zoológico ID '${zooid}' no es válido.`);
        return;
    }
    const apiAnimalUrl = "http://localhost:8082/ws/animal";
    const soapRequest = `
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                      xmlns:org="http://service.zoo.example.org/">
      <soapenv:Header/>
      <soapenv:Body>
        <org:addAnimal>
          <animal>
            <name>${name}</name>
            <scientific_name>${scientificName}</scientific_name>
            <habitat>${habitat}</habitat>
            <diet>${diet}</diet>
            <species>${species}</species>
            <zooid>${zooid}</zooid>
          </animal>
        </org:addAnimal>
      </soapenv:Body>
    </soapenv:Envelope>
  `;
    try {
        const response = await sendSOAPRequest(apiAnimalUrl, soapRequest);
        if (response.getElementsByTagName("faultstring").length > 0) {
            const errorMessage = response.getElementsByTagName("faultstring")[0].textContent;
            showErrorMessage(`⚠ Error del servidor: ${errorMessage}`);
            return;
        }
        showSuccessMessage("✅ Animal agregado correctamente.");
        await fetchAnimals(false);
        resetAnimalForm();
    } catch (error) {
        console.error("Error al agregar animal:", error);
    }
}

/**
 * Actualiza un animal.
 */
async function updateAnimal(id, field, newValue) {
    console.log(`✏️ Actualizando Animal ID: ${id}, Campo: ${field}, Nuevo Valor: ${newValue}`);
    const row = document.querySelector(`tr[data-id="${id}"]`);
    if (!row) {
        console.error(`❌ No se encontró la fila del animal con ID: ${id}`);
        return;
    }
    const name = row.querySelector('input[data-field="name"]').value.trim();
    const scientificName = row.querySelector('input[data-field="scientificName"]').value.trim();
    const habitat = row.querySelector('input[data-field="habitat"]').value.trim();
    const diet = row.querySelector('input[data-field="diet"]').value.trim();
    const species = row.querySelector('input[data-field="species"]').value.trim();
    const zooid = row.querySelector('select[data-field="zooid"]').value.trim();
    if (!id || !name || !scientificName || !habitat || !diet || !species || !zooid) {
        showErrorMessage("⚠ Todos los campos son obligatorios.");
        return;
    }
    const apiAnimalUrl = "http://localhost:8082/ws/animal";
    const soapRequest = `
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                      xmlns:org="http://service.zoo.example.org/">
      <soapenv:Header/>
      <soapenv:Body>
        <org:updateAnimal>
          <id>${id}</id>
          <updatedAnimal>
            <name>${name}</name>
            <scientific_name>${scientificName}</scientific_name>
            <habitat>${habitat}</habitat>
            <diet>${diet}</diet>
            <species>${species}</species>
            <zooid>${zooid}</zooid>
          </updatedAnimal>
        </org:updateAnimal>
      </soapenv:Body>
    </soapenv:Envelope>
  `;
    try {
        const response = await sendSOAPRequest(apiAnimalUrl, soapRequest);
        if (response.getElementsByTagName("faultstring").length > 0) {
            const errorMessage = response.getElementsByTagName("faultstring")[0].textContent;
            showErrorMessage(`⚠ Error del servidor: ${errorMessage}`);
            return;
        }
        row.querySelector(`[data-field="${field}"]`).value = newValue;
        showSuccessMessage("✅ Animal actualizado correctamente.");
    } catch (error) {
        console.error("Error al actualizar animal:", error);
    }
}

/**
 * Elimina un zoológico.
 */
async function deleteZoo(id) {
    console.log(`🗑 Eliminando zoológico con ID: ${id}`);
    const soapRequest = `
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                      xmlns:org="http://service.zoo.example.org/">
      <soapenv:Header/>
      <soapenv:Body>
        <org:deleteZoo>
          <id>${id}</id>
        </org:deleteZoo>
      </soapenv:Body>
    </soapenv:Envelope>
  `;
    try {
        await sendSOAPRequest("http://localhost:8082/ws/zoo", soapRequest);
        showSuccessMessage("✅ Zoológico eliminado.");
        deleteAnimalsByZoo(id);
        setTimeout(async () => {
            await fetchZoos(true);
            await fetchAnimals(false);
        }, 500);
    } catch (error) {
        console.error("Error al eliminar zoológico:", error);
    }
}

/**
 * Elimina un animal.
 */
async function deleteAnimal(id) {
    const soapRequest = `
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                      xmlns:org="http://service.zoo.example.org/">
      <soapenv:Header/>
      <soapenv:Body>
        <org:deleteAnimal>
          <id>${id}</id>
        </org:deleteAnimal>
      </soapenv:Body>
    </soapenv:Envelope>
  `;
    try {
        await sendSOAPRequest("http://localhost:8082/ws/animal", soapRequest);
        showSuccessMessage("✅ Animal eliminado.");
        setTimeout(async () => {
            await fetchZoos(false);
            await fetchAnimals(true);
        }, 500);
    } catch (error) {
        console.error("Error al eliminar animal:", error);
    }
}

/**
 * Elimina de la tabla los animales asociados a un zoológico.
 */
function deleteAnimalsByZoo(zooid) {
    console.log(`🗑 Eliminando animales asociados al zoológico ID: ${zooid}`);
    const table = document.getElementById("animalsTable");
    if (!table) {
        console.error("⚠ No se encontró la tabla de animales.");
        return;
    }
    table.querySelectorAll("tr[data-id]").forEach((row) => {
        const animalZooId = row.querySelector('select[data-field="zooid"]').value;
        if (animalZooId === zooid) {
            row.remove();
            console.log(`🗑 Eliminado de la tabla el animal con ID: ${row.getAttribute("data-id")}`);
        }
    });
}

/**
 * Filtra zoológicos por continente.
 */
async function filterZoos() {
    const selectedLocation = document.getElementById("filterZooLocation").value;
    if (selectedLocation === "all") {
        await fetchZoos(true);
        return;
    }
    const apiZooUrl = "http://localhost:8082/ws/zoo";
    const soapRequest = `
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:org="http://service.zoo.example.org/">
      <soapenv:Header/>
      <soapenv:Body>
        <org:getZoosByLocation>
          <location>${selectedLocation}</location>
        </org:getZoosByLocation>
      </soapenv:Body>
    </soapenv:Envelope>
  `;
    try {
        const data = await sendSOAPRequest(apiZooUrl, soapRequest);
        zooList = Array.from(data.getElementsByTagName("return")).map((zoo) => ({
            id: zoo.getAttribute("id") || "N/A",
            name: zoo.getElementsByTagName("name")[0]?.textContent || "N/A",
            city: zoo.getElementsByTagName("city")[0]?.textContent || "N/A",
            foundation: zoo.getElementsByTagName("foundation")[0]?.textContent || "N/A",
            location: zoo.getElementsByTagName("location")[0]?.textContent || "Desconocido",
        }));
        renderZoosTable(zooList);
    } catch (error) {
        console.error("Error al filtrar zoológicos:", error);
    }
}

/**
 * Restablece el formulario de zoológicos.
 */
function resetZooForm() {
    document.getElementById("zooName").value = "";
    document.getElementById("zooCity").value = "";
    document.getElementById("zooFoundation").value = "";
    document.getElementById("zooLocation").value = "default";
}

/**
 * Restablece el formulario de animales.
 */
function resetAnimalForm() {
    console.log("🔄 Restableciendo formulario de animal...");
    document.getElementById("animalName").value = "";
    document.getElementById("animalScientificName").value = "";
    document.getElementById("animalHabitat").value = "";
    document.getElementById("animalDiet").value = "";
    document.getElementById("animalSpecies").value = "";
    document.getElementById("animalZooId").value = "default";
    console.log("✅ Formulario de animal restablecido.");
}

/**
 * Alterna la visibilidad de la tabla de zoológicos.
 */
function toggleZooTable() {
    const tableContainer = document.getElementById("zoosTableContainer");
    const button = document.getElementById("toggleZooTable");
    if (!tableContainer || !button) return;
    if (tableContainer.style.display === "none" || tableContainer.style.display === "") {
        tableContainer.style.display = "block";
        button.innerHTML = "👁 Ocultar Tabla de Zoológicos";
    } else {
        tableContainer.style.display = "none";
        button.innerHTML = "👁 Mostrar Tabla de Zoológicos";
    }
}

/**
 * Alterna la visibilidad de la tabla de animales.
 */
function toggleAnimalsTable() {
    const tableContainer = document.getElementById("animalsTableContainer");
    const button = document.getElementById("toggleAnimalTable");
    if (!tableContainer || !button) {
        console.error("⚠ No se encontró 'animalsTableContainer' o 'toggleAnimalTable' en el DOM.");
        return;
    }
    if (tableContainer.style.display === "none" || tableContainer.style.display === "") {
        tableContainer.style.display = "block";
        button.innerHTML = "👁 Ocultar Tabla de Animales";
    } else {
        tableContainer.style.display = "none";
        button.innerHTML = "👁 Mostrar Tabla de Animales";
    }
}

/**
 * Muestra un mensaje de error en la aplicación.
 */
function showErrorMessage(message) {
    const statusDiv = document.getElementById("status");
    if (statusDiv) {
        statusDiv.innerHTML = message;
        statusDiv.style.color = "red";
    }
}

/**
 * Muestra un mensaje de éxito en la aplicación.
 */
function showSuccessMessage(message) {
    const statusDiv = document.getElementById("status");
    if (statusDiv) {
        statusDiv.innerHTML = message;
        statusDiv.style.color = "green";
    }
}

/**
 * Función stub para cargar estadísticas (completar según se requiera).
 */
function fetchStatistics() {
    console.log("Cargando estadísticas...");
    // Implementa la lógica necesaria para obtener y renderizar estadísticas.
}
