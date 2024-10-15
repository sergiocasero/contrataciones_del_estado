
package es.age.dgpe.placsp.risp.parser.model;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import org.dgpe.codice.common.caclib.CommodityClassificationType;
import org.dgpe.codice.common.caclib.ProcurementProjectLotType;
import org.dgpe.codice.common.caclib.ProcurementProjectType;

import es.age.dgpe.placsp.risp.parser.utils.genericode.GenericodeTypes;
import ext.place.codice.common.caclib.ContractFolderStatusType;

public enum DatosResultados {
	NUMERO_EXPEDIENTE("N�mero de expediente") {
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder, int indice) {
			String numExpediente = contractFolder.getContractFolderID().getValue();
			return numExpediente;
		}
	},
	NUMERO_LOTE("Lote") {
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder, int indice) {
			String numLote = "Sin lotes";
			try {
				numLote = contractFolder.getTenderResult().get(indice).getAwardedTenderedProject()
						.getProcurementProjectLotID().getValue();
			} catch (Exception e) {
				numLote = "Sin lotes";
			}
			return numLote;
		}
	},
	OBJETO("Objeto licitaci�n/lote"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				ProcurementProjectLotType procurementProjectLot = getProcurementProjectLot(contractFolder, indice);
				if (procurementProjectLot == null) {
					return contractFolder.getProcurementProject().getName().get(0).getValue();
				}else {
					return procurementProjectLot.getProcurementProject().getName().get(0).getValue();
				}
			}catch (Exception e) {
				return null;
			}
		}
	},
	IMPORTE_CON_IMPUESTOS("Presupuesto base con impuestos licitaci�n/lote", EnumFormatos.MONEDA){
		@Override
		public BigDecimal valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				ProcurementProjectLotType procurementProjectLot = getProcurementProjectLot(contractFolder, indice);
				if (procurementProjectLot == null) {
					return contractFolder.getProcurementProject().getBudgetAmount().getTotalAmount().getValue();
				}else {
					return procurementProjectLot.getProcurementProject().getBudgetAmount().getTotalAmount().getValue();
				}
			}catch (Exception e) {
				return null;
			}
		}
	},
	IMPORTE_SIN_IMPUESTOS("Presupuesto base sin impuestos licitaci�n/lote", EnumFormatos.MONEDA){
		@Override
		public BigDecimal valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				ProcurementProjectLotType procurementProjectLot = getProcurementProjectLot(contractFolder, indice);
				if (procurementProjectLot == null) {
					return contractFolder.getProcurementProject().getBudgetAmount().getTaxExclusiveAmount().getValue();
				}else {
					return procurementProjectLot.getProcurementProject().getBudgetAmount().getTaxExclusiveAmount().getValue();
				}
			}catch (Exception e) {
				return null;
			}
		}
	},
	CPV("CPV licitaci�n/lote"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder, int indice) {
			String valoresCPV = "";
			try {
				ProcurementProjectLotType procurementProjectLot = getProcurementProjectLot(contractFolder, indice);
				if (procurementProjectLot == null) {
					for (CommodityClassificationType commodity : contractFolder.getProcurementProject().getRequiredCommodityClassification()) {
						valoresCPV += commodity.getItemClassificationCode().getValue() + SEPARADOR;
					}
				}else {
					for (CommodityClassificationType commodity :  procurementProjectLot.getProcurementProject().getRequiredCommodityClassification()) {
						valoresCPV += commodity.getItemClassificationCode().getValue() + SEPARADOR;
					}
				}
				return valoresCPV;
			}catch (Exception e) {
				return null;
			}
		}
	},
	LUGAR_EJEUCION("Lugar ejecuci�n licitaci�n/lote"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder, int indice) {
			String codigo = "";
			String descripcion = "";
			try {
				ProcurementProjectLotType procurementProjectLot = getProcurementProjectLot(contractFolder, indice);
				ProcurementProjectType procurementProjectType;
				if (procurementProjectLot == null) {
					//La licitaci�n no est� estructurada en lotes
					procurementProjectType = contractFolder.getProcurementProject();
				}
				else {
					//La licitaci�n se estructura en lotes
					procurementProjectType = procurementProjectLot.getProcurementProject();
				}
				
				//Se obtiene el c�digo y la descripci�n del lugar de ejecuci�n
				try {
					codigo = procurementProjectType.getRealizedLocation().getCountrySubentityCode().getValue();				
				}catch(Exception e) {
					codigo = "";
				}			
				try {
					descripcion = procurementProjectType.getRealizedLocation().getCountrySubentity().getValue();				
				}catch(Exception e) {
					descripcion = "";
				}

				if (codigo == "" && descripcion == "") {
					//No existe un c�digo NUTS. Se intenta obtener el codigo del pa�s
					try {
						codigo = procurementProjectType.getRealizedLocation().getAddress().getCountry().getIdentificationCode().getValue();
					}catch(Exception e) {
						codigo = "";
					}			
					try {
						descripcion = procurementProjectType.getRealizedLocation().getAddress().getCountry().getName().getValue();
					}catch(Exception e) {
						descripcion = "";
					}
				}	
			}catch (Exception e) {
				return null;
			}
			return codigo + " - " + descripcion;
		}
	},
	RESULTADO("Resultado licitaci�n/lote"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				return GenericodeTypes.RESULTADO.getValue(contractFolder.getTenderResult().get(indice).getResultCode().getValue());
			} catch (Exception e) {
				return null;
			}
		}
	},
	FECHA_ACUERDO("Fecha del acuerdo licitaci�n/lote", EnumFormatos.FECHA_CORTA){
		@Override
		public GregorianCalendar valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				return contractFolder.getTenderResult().get(indice).getAwardDate().getValue().toGregorianCalendar();
			} catch (Exception e) {
				return null;
			}
		}
	},
	OFERTAS_RECIBIDAS("N�mero de ofertas recibidas por licitaci�n/lote", EnumFormatos.NUMERO){
		@Override
		public BigDecimal valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				return contractFolder.getTenderResult().get(indice).getReceivedTenderQuantity().getValue();
			} catch (Exception e) {
				return null;
			}
		}
	},
	PRECIO_OFERTA_MAS_BAJA("Precio de la oferta m�s baja por licitaci�n/lote", EnumFormatos.MONEDA){
		@Override
		public BigDecimal valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				return contractFolder.getTenderResult().get(indice).getLowerTenderAmount().getValue();
			} catch (Exception e) {
				return null;
			}
		}
	},
	PRECIO_OFERTA_MAS_ALTA("Precio de la oferta m�s alta por licitaci�n/lote", EnumFormatos.MONEDA){
		@Override
		public BigDecimal valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				return contractFolder.getTenderResult().get(indice).getHigherTenderAmount().getValue();
			} catch (Exception e) {
				return null;
			}
		}
	},
	EXCLUIDAS_OFERTAS_ANORM_BAJAS("Se han exclu�do ofertas por ser anormalmente bajas por licitaci�n/lote"){
		@Override
		public Boolean valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				return contractFolder.getTenderResult().get(indice).getAbnormallyLowTendersIndicator().isValue();
			} catch (Exception e) {
				return null;
			}
		}
	},
	NUMERO_CONTRATO("N�mero del contrato licitaci�n/lote", EnumFormatos.TEXTO){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				return contractFolder.getTenderResult().get(indice).getContract().getID().getValue();
			} catch (Exception e) {
				return null;
			}
		}
	},
	FECHA_FORMALIZACION("Fecha formalizaci�n del contrato licitaci�n/lote", EnumFormatos.FECHA_CORTA){
		@Override
		public GregorianCalendar valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				return contractFolder.getTenderResult().get(indice).getContract().getIssueDate().getValue().toGregorianCalendar();
			} catch (Exception e) {
				return null;
			}
		}
	},
	FECHA_ENTRADA_VIGOR("Fecha entrada en vigor del contrato de licitaci�n/lote", EnumFormatos.FECHA_CORTA){
		@Override
		public GregorianCalendar valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				return contractFolder.getTenderResult().get(indice).getStartDate().getValue().toGregorianCalendar();
			} catch (Exception e) {
				return null;
			}
		}
	},
	ADJUDICATARIO("Adjudicatario licitaci�n/lote"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				return contractFolder.getTenderResult().get(indice).getWinningParty().getPartyName().get(0).getName().getValue();
			} catch (Exception e) {
				return null;
			}
		}
	},
	TIPO_ID_ADJUDICATARIO("Tipo de identificador de adjudicatario por licitaci�n/lote"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				return contractFolder.getTenderResult().get(indice).getWinningParty().getPartyIdentification().get(0).getID().getSchemeName();
			} catch (Exception e) {
				return null;
			}
		}
	},
	ID_ADJUDICATARIO ("Identificador Adjudicatario de la licitaci�n/lote"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				return contractFolder.getTenderResult().get(indice).getWinningParty().getPartyIdentification().get(0).getID().getValue();
			}catch(Exception e) {
				return null;
			}			
		}
	},
	ES_PYME("El adjudicatario es o no PYME de la licitaci�n/lote"){
		@Override
		public Boolean valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				return contractFolder.getTenderResult().get(indice).getSMEAwardedIndicator().isValue();
			} catch (Exception e) {
				return null;
			}
		}
	},
	IMPORTE_ADJ_SIN_IMPUESTOS("Importe adjudicaci�n sin impuestos licitaci�n/lote", EnumFormatos.MONEDA){
		@Override
		public BigDecimal valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				return contractFolder.getTenderResult().get(indice).getAwardedTenderedProject().getLegalMonetaryTotal().getTaxExclusiveAmount().getValue();
			} catch (Exception e) {
				return null;
			}
		}
	},
	IMPORTE_ADJ_CON_IMPUESTOS("Importe adjudicaci�n con impuestos licitaci�n/lote", EnumFormatos.MONEDA){
		@Override
		public BigDecimal valorCodice(ContractFolderStatusType contractFolder, int indice) {
			try {
				return contractFolder.getTenderResult().get(indice).getAwardedTenderedProject().getLegalMonetaryTotal().getPayableAmount().getValue();
			} catch (Exception e) {
				return null;
			}
		}
	};

	private final static String SEPARADOR = ";";

	private final String titulo;
	private final EnumFormatos formato;

	DatosResultados(String name, EnumFormatos format) {
		this.titulo = name;
		this.formato = format;
	}
	
	DatosResultados(String name) {
		this.titulo = name;
		this.formato = EnumFormatos.TEXTO;
	}

	public String getTiulo() {
		return titulo;
	}
	
	public EnumFormatos getFormato() {
		return formato;
	}

	public abstract Object valorCodice(ContractFolderStatusType contractFolder, int indiceTenderResult);

	/**
	 * M�todo que devuleve el procurementproject asociado al resultado que existe en
	 * el lugar indicado
	 * 
	 * @param contractFolder
	 * @param indiceTenderResult
	 * @return Se devuelve null en caso de que no exista
	 */
	private static ProcurementProjectLotType getProcurementProjectLot(ContractFolderStatusType contractFolder, int indiceTenderResult) {
		try {

			String numeroLote = contractFolder.getTenderResult().get(indiceTenderResult).getAwardedTenderedProject()
					.getProcurementProjectLotID().getValue();

			// se busca el procurementprojectlot
			for (ProcurementProjectLotType procurementProjectLotType : contractFolder.getProcurementProjectLot()) {
				if (procurementProjectLotType.getID().getValue().compareTo(numeroLote) == 0) {
					return procurementProjectLotType;
				}
			}
			return null;
		}
		catch (Exception e) {
			return null;
		}

	}

}
