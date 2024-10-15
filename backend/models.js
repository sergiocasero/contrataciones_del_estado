const { Sequelize, DataTypes } = require('sequelize');

// Configura la conexión a la base de datos MySQL
const sequelize = new Sequelize('licitaciones', 'licitaciones', 'licitaciones', {
  host: 'localhost', // Cambia esto al host de tu base de datos MySQL si es diferente
  dialect: 'mysql',
  dialectModule: require('mysql2'),
  logging: false
});

// Define el modelo para la tabla que coincida con tu esquema de datos
const Tuple = sequelize.define('Tuple', {
  Identificador: DataTypes.STRING,
  LinkLicitacion: DataTypes.STRING,
  FechaActualizacion: DataTypes.DATE,
  VigenteAnuladaArchivada: DataTypes.STRING,
  PrimeraPublicacion: DataTypes.DATE,
  Estado: DataTypes.STRING,
  NumeroExpediente: DataTypes.STRING,
  ObjetoDelContrato: DataTypes.STRING,
  ValorEstimadoContrato: DataTypes.DECIMAL(18, 2),
  PresupuestoBaseSinImpuestos: DataTypes.DECIMAL(18, 2),
  PresupuestoBaseConImpuestos: DataTypes.DECIMAL(18, 2),
  CPV: DataTypes.TEXT,
  TipoContrato: DataTypes.STRING,
  LugarEjecucion: DataTypes.STRING,
  OrganoContratacion: DataTypes.STRING,
  IDOCenPLACSP: DataTypes.STRING,
  NIFOC: DataTypes.STRING,
  DIR3: DataTypes.STRING,
  EnlacePerfilContratanteOC: DataTypes.STRING,
  TipoAdministracion: DataTypes.STRING,
  CodigoPostal: DataTypes.STRING,
  TipoProcedimiento: DataTypes.STRING,
  SistemaContratacion: DataTypes.STRING,
  Tramitacion: DataTypes.STRING,
  FormaPresentacionOferta: DataTypes.STRING,
  FechaPresentacionOfertas: DataTypes.DATE,
  FechaPresentacionSolicitudesParticipacion: DataTypes.DATE,
  DirectivaAplicacion: DataTypes.STRING,
  FinanciacionEuropeaFuente: DataTypes.STRING,
  DescripcionFinanciacionEuropea: DataTypes.STRING,
  SubcontratacionPermitida: DataTypes.STRING,
  SubcontratacionPermitidaPorcentaje: DataTypes.DECIMAL(5, 2),
  NumeroExpedienteLote: DataTypes.STRING,
  Lote: DataTypes.STRING,
  ObjetoLicitacionLote: DataTypes.STRING,
  PresupuestoBaseConImpuestosLote: DataTypes.DECIMAL(18, 2),
  PresupuestoBaseSinImpuestosLote: DataTypes.DECIMAL(18, 2),
  CPVLicitacionLote: DataTypes.STRING,
  LugarEjecucionLote: DataTypes.STRING,
  ResultadoLicitacionLote: DataTypes.STRING,
  FechaAcuerdoLicitacionLote: DataTypes.DATE,
  NumOfertasRecibidasLicitacionLote: DataTypes.INTEGER,
  PrecioOfertaMasBajaLicitacionLote: DataTypes.DECIMAL(18, 2),
  PrecioOfertaMasAltaLicitacionLote: DataTypes.DECIMAL(18, 2),
  ExclusionOfertasAnormalmenteBajasLicitacionLote: DataTypes.STRING,
  NumContratoLicitacionLote: DataTypes.STRING,
  FechaFormalizacionContratoLicitacionLote: DataTypes.DATE,
  FechaEntradaVigorContratoLicitacionLote: DataTypes.DATE,
  AdjudicatarioLicitacionLote: DataTypes.STRING,
  TipoIdentificadorAdjudicatarioLicitacionLote: DataTypes.STRING,
  IdentificadorAdjudicatarioLicitacionLote: DataTypes.STRING,
  AdjudicatarioEsPYMELicitacionLote: DataTypes.STRING,
  ImporteAdjudicacionSinImpuestosLicitacionLote: DataTypes.DECIMAL(18, 2),
  ImporteAdjudicacionConImpuestosLicitacionLote: DataTypes.DECIMAL(18, 2),
});

// Sincroniza el modelo con la base de datos
Tuple.sync()
  .then(() => {
    console.log('Modelo sincronizado con la base de datos.');
  })
  .catch((error) => {
    console.error('Error al sincronizar el modelo:', error);
  });


module.exports = Tuple;