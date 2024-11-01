# Contrataciones del estado

IMPORTANTE: Este es un proyecto realizado por Sergio Casero Hernández en su TIEMPO LIBRE, esto es un side-project y las contribuciones se hacen cuando se pueden. Si quieres contribuir al proyecto, siéntete libre de lanzar una pull-request.

Está totalmente prohibido utilizar este código o la solución que aquí se propone por parte de cualquier tercero con fines lucrativos (ver licencia)

## Objetivo

El objetivo de este proyecto es conseguir un buscador de contratos del estado ágil, rápido y que no esté limitado como el oficial de https://contratacionesdelestado.com, lo ideal sería ofrecer un buscador estilo "Google" que permita buscar por cualquier campo disponible en la base de datos y que se arrojen resultados.
También hay que trabajar en un "conversor" masivo para que los datos se puedan descargar en CSV. Actualmente ya más de 6.400.000 de contratos

## Problemática

El problema es que aunque los datos "sean abiertos" para descargarlos de forma masiva es un proceso bastante tedioso. El estado pone a disposición una herramienta escrita en Java llamada "OpenPLA", que aunque no permite hacer 
descarga masiva de la información, si que se puede modificar para tal efecto.

## Estado actual

- Descargar contratos ✅ (Desde el 1 de enero de 2018)
- Inyectar contratos en una base de datos ✅
- API Rest para consultar los contratos ✅
- Descargar contratos menores: TODO
- Frontend que permita interactuar con la API: TODO
- Descarga masiva de la base de datos en formato CSV : TODO
- Migrar de SQLite a Postgres: TODO

## Diagrama de la solución

## Lenguajes de programación usados

- Java: Todo el códido oficial de OpenPLA está en Java
- Kotlin: Por comodidad, este proyecto se ha configurado con gradle para que cualquiera compilarlo fácilmente, y se ha añadido el lenguaje kotlin para interactuar con la parte legacy
- Python: Extractor de la información descarga con los scripts desarrollados en Kotlin, y para la API
- Typescript: Frontend

## Descargar los contratos

Para descargar los contratos hay que hacerlo de una forma "curiosa", básicamente lo que proporciona el estado es una serie de endpoints para descargar la información en formato PDF: https://contrataciondelestado.es/sindicacion/sindicacion_643/licitaciones
PerfilesContratanteCompleto3_202101.zip, donde tenemos que cambiar los últimos digitos, en formato AAAA y MM, así podemos iterar. Aunque a priori podría descargar información de años atrás, si se intenta con fechas inferiores 
al 01/01/2018... directamente no funciona.

Una vez hemos descargado los ficheros ZIP, los tenemos que descomprimir y nos encontramos con ficheros ATOM. Estos ficheros contienen las URLs a los contratos de ese mes en cuestión, pero no hay ninguna información más.

Por último, tenemos que usar un fichero xlsx como plantilla, y a la herramienta OpenPLA le pasamos tanto los ficheros ATOM como la plantilla XLSX y ésta se encargará de rellenar ese XLSX en un nuevo fichero. El código está en el fichero `BulkGenerator.kt`

## Inyectar los contratos en una base de datos

Una vez tenemos todos los contratos en ficheros XLSX, tenemos un script `importar_licitaciones.py` que va recorriendo estos ficheros e inyecta la información en bases de datos.

## Frontend

TODO
