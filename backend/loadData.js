// loadData.js
const fs = require('fs');
const xlsx = require('xlsx');
const Tuple = require('./models');
const { log } = require('console');
const { Sequelize, DataTypes } = require('sequelize');

const sequelize = new Sequelize('licitaciones', 'licitaciones', 'licitaciones', {
  host: 'localhost', // Cambia esto al host de tu base de datos MySQL si es diferente
  dialect: 'mysql',
  dialectModule: require('mysql2'),
});

// Path to the XLSX file you want to parse
const xlsxFile = '../output/10_2023.xlsx';


const columnHeaders = [
  "Identificador",
  "LinkLicitacion",
  "FechaActualizacion",
  "VigenteAnuladaArchivada",
  "PrimeraPublicacion",
  "Estado",
  "NumeroExpediente",
  "ObjetoDelContrato",
  "ValorEstimadoContrato",
  "PresupuestoBaseSinImpuestos",
  "PresupuestoBaseConImpuestos",
  "CPV",
  "TipoContrato",
  "LugarEjecucion",
  "OrganoContratacion",
  "IDOCenPLACSP",
  "NIFOC",
  "DIR3",
  "EnlacePerfilContratanteOC",
  "TipoAdministracion",
  "CodigoPostal",
  "TipoProcedimiento",
  "SistemaContratacion",
  "Tramitacion",
  "FormaPresentacionOferta",
  "FechaPresentacionOfertas",
  "FechaPresentacionSolicitudesParticipacion",
  "DirectivaAplicacion",
  "FinanciacionEuropeaFuente",
  "DescripcionFinanciacionEuropea",
  "SubcontratacionPermitida",
  "SubcontratacionPermitidaPorcentaje",
  "NumeroExpedienteLote",
  "Lote",
  "ObjetoLicitacionLote",
  "PresupuestoBaseConImpuestosLote",
  "PresupuestoBaseSinImpuestosLote",
  "CPVLicitacionLote",
  "LugarEjecucionLote",
  "ResultadoLicitacionLote",
  "FechaAcuerdoLicitacionLote",
  "NumOfertasRecibidasLicitacionLote",
  "PrecioOfertaMasBajaLicitacionLote",
  "PrecioOfertaMasAltaLicitacionLote",
  "ExclusionOfertasAnormalmenteBajasLicitacionLote",
  "NumContratoLicitacionLote",
  "FechaFormalizacionContratoLicitacionLote",
  "FechaEntradaVigorContratoLicitacionLote",
  "AdjudicatarioLicitacionLote",
  "TipoIdentificadorAdjudicatarioLicitacionLote",
  "IdentificadorAdjudicatarioLicitacionLote",
  "AdjudicatarioEsPYMELicitacionLote",
  "ImporteAdjudicacionSinImpuestosLicitacionLote",
  "ImporteAdjudicacionConImpuestosLicitacionLote"
]

// Función para cargar datos en la base de datos
async function loadData() {
  try {
    await sequelize.authenticate();
    console.log('Conexión a la base de datos establecida correctamente.');

    const workbook = xlsx.readFile(xlsxFile);
    const sheet = workbook.Sheets[workbook.SheetNames[1]];

    // Obtén un array de objetos donde cada objeto representa una fila como un Tuple
    const data = xlsx.utils.sheet_to_json(sheet, { header: 1 });

    // Define un array para almacenar los objetos Tuple
    const tuples = [];

    // Recorre las filas (comenzando desde la segunda fila, ya que la primera fila contiene los encabezados)
    for (let i = 1; i < data.length; i++) {
      const rowData = data[i];
      const tupleObject = {};

      // Recorre las columnas y asigna los valores a los campos de Tuple según los encabezados de las columnas
      for (let j = 0; j < columnHeaders.length; j++) {
        // here, if columnHeaders[j] is FechaActualizacion, PrimeraPublicacion, FechaPresentacionOfertas, FechaPresentacionSolicitudesParticipacion,
        // FechaAcuerdoLicitacionLote, FechaFormalizacionContratoLicitacionLote, FechaEntradaVigorContratoLicitacionLote
        // it's a date in 5/10/2023  17:00:46 format, so we need to convert it to a Date object
        const dateFields = [
          "FechaActualizacion", 
          "PrimeraPublicacion", 
          "FechaPresentacionOfertas", 
          "FechaPresentacionSolicitudesParticipacion",
          "FechaAcuerdoLicitacionLote",
          "FechaFormalizacionContratoLicitacionLote",
          "FechaEntradaVigorContratoLicitacionLote"
        ]

        if (dateFields.includes(columnHeaders[j])) {
          // date is in 45203.50273425926 format, so we need to convert it to a Date object
          if(rowData[j] === undefined) { 
            tupleObject[columnHeaders[j]] = null;
          } else {
            const date = new Date((rowData[j] - (25567 + 2))*86400*1000);
            tupleObject[columnHeaders[j]] = new Date(date);
          }
        } else {
          tupleObject[columnHeaders[j]] = rowData[j];
        }
      }

      // Crea un nuevo objeto Tuple y agrégalo al array
      tuples.push(tupleObject);
    }

    // Sincroniza el modelo con la base de datos
    await Tuple.sync();

    // Inserta el array de objetos Tuple en la base de datos MySQL
    await Tuple.bulkCreate(tuples);

    console.log('Datos insertados en la base de datos con éxito.');
  } catch (error) {
    console.error('Error al cargar los datos:', error);
  } finally {
    // Cierra la conexión a la base de datos cuando hayas terminado
    await sequelize.close();
    console.log('Conexión a la base de datos cerrada.');
  }
}

loadData();