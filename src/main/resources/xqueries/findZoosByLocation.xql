xquery version "1.0";

declare variable $location external;

for $zoo in doc("zoo.xml")//zoo
where $zoo/@location = $location
return $zoo
