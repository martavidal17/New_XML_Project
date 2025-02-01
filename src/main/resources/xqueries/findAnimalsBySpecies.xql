xquery version "3.1";

(: Declaramos la variable externa que contendrá el valor de la especie :)
declare variable $species as xs:string external;

(: Buscamos todos los elementos "animal" en el documento "zoo.xml" cuyo
   hijo "species" sea exactamente igual al valor de $species :)
for $animal in doc("zoo.xml")//animal
where $animal/species = $species
return $animal
