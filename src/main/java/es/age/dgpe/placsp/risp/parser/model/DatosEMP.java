/*******************************************************************************
 * Copyright 2021 Subdirección General de Coordinación de la Contratación Electronica - Dirección General Del Patrimonio Del Estado - Subsecretaría de Hacienda - Ministerio de Hacienda - Administración General del Estado - Gobierno de España
 * 
 * Licencia con arreglo a la EUPL, Versión 1.2 o –en cuanto sean aprobadas por la Comisión Europea– versiones posteriores de la EUPL (la «Licencia»);
 * Solo podrá usarse esta obra si se respeta la Licencia.
 * Puede obtenerse una copia de la Licencia en:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Salvo cuando lo exija la legislación aplicable o se acuerde por escrito, el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL», SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
 * Véase la Licencia en el idioma concreto que rige los permisos y limitaciones que establece la Licencia.
 ******************************************************************************/
package es.age.dgpe.placsp.risp.parser.model;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConstants;

import org.dgpe.codice.common.caclib.CommodityClassificationType;
import org.dgpe.codice.common.caclib.PartyIdentificationType;

import es.age.dgpe.placsp.risp.parser.utils.genericode.GenericodeTypes;
import ext.place.codice.common.caclib.AdditionalPublicationDocumentReferenceType;
import ext.place.codice.common.caclib.AdditionalPublicationStatusType;
import ext.place.codice.common.caclib.ContractFolderStatusType;
import ext.place.codice.common.caclib.NoticeInfoType;
 
public enum DatosEMP{
	PRIMERA_PUBLICACION("Primera publicación", EnumFormatos.FECHA_CORTA) {
	@Override
		public GregorianCalendar valorCodice(ContractFolderStatusType contractFolder) {
			GregorianCalendar primeraPublicacion = null;

			try {
				// Se recorren los validnoticeinfo
				for (NoticeInfoType noticeInfo : contractFolder.getValidNoticeInfo()) {
					try {
						// No se tiene en cuenta si es anuncio previo
						if (noticeInfo.getNoticeTypeCode().getValue().compareTo("DOC_PIN") != 0) {
							// Se recorren los medios de publicacion
							for (AdditionalPublicationStatusType additionalPublicationStatus : noticeInfo
									.getAdditionalPublicationStatus()) {
								// Se comprueba si el medio es el perfil de contratante
								if (additionalPublicationStatus.getPublicationMediaName().getValue().equalsIgnoreCase("Perfil del Contratante")) {
									// Se obtiene la fecha más antigua
									for (AdditionalPublicationDocumentReferenceType additionalPublicationDocumentReference : additionalPublicationStatus
											.getAdditionalPublicationDocumentReference()) {
										GregorianCalendar fecha = additionalPublicationDocumentReference.getIssueDate().getValue().toGregorianCalendar();
										if (primeraPublicacion == null
												|| primeraPublicacion.compareTo(fecha) == DatatypeConstants.GREATER) {
											primeraPublicacion = fecha;
										}
									}
								}
							}
						}
					} catch (Exception e) {
						//El ATOM cumple con el esquema, pero no con los requisito
					}
				}
				return primeraPublicacion;
			} catch (Exception e) {
				return null;
			}
		}
	},
	ESTADO ("Estado"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder){
			try {
				String estado = GenericodeTypes.ESTADO.getValue(contractFolder.getContractFolderStatusCode().getValue());
				return estado;
			}catch (Exception e) {
				return null;
			}
		}
	},
	NUMERO_EXPEDIENTE ("Número de expediente"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder){
			try {
				String numExpediente = contractFolder.getContractFolderID().getValue();
				return numExpediente;
			}catch (Exception e) {
				return null;
			}
		}
	},
	OBJETO_CONTRATO ("Objeto del Encargo"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder){
			try {
				String objeto = contractFolder.getProcurementProject().getName().get(0).getValue();
				return objeto;
			}catch (Exception e) {
				return null;
			}
		}
	},
	/*VALOR_ESTIMADO ("Valor estimado del encargo", EnumFormatos.MONEDA){
		@Override
		public BigDecimal valorCodice(ContractFolderStatusType contractFolder){
			try {
				BigDecimal valorEstimado = contractFolder.getProcurementProject().getBudgetAmount().getEstimatedOverallContractAmount().getValue();
				return valorEstimado;
			}catch (Exception e) {
				return null;
			}
			
		}
	},*/
	PRESUPUESTO_BASE_SIN_IMPUESTOS ("Presupuesto base sin impuestos", EnumFormatos.MONEDA){
		@Override
		public BigDecimal valorCodice(ContractFolderStatusType contractFolder) {
			try {
				BigDecimal presupuestoConImpuestos = contractFolder.getProcurementProject().getBudgetAmount().getTaxExclusiveAmount().getValue();
				return presupuestoConImpuestos;
			}catch (Exception e) {
				return null;
			}
		}
	},
	PRESUPUESTO_BASE_CON_IMPUESTOS ("Presupuesto base con impuestos", EnumFormatos.MONEDA){
		@Override
		public BigDecimal valorCodice(ContractFolderStatusType contractFolder) {
			try {
				BigDecimal presupuestoSinImpuestos = contractFolder.getProcurementProject().getBudgetAmount().getTotalAmount().getValue();
				return presupuestoSinImpuestos;	
			}catch (Exception e) {
				return null;
			}
		}
	},
	CPV ("CPV"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder) {
			String valoresCPV = "";
			try {
				for (CommodityClassificationType commodity : contractFolder.getProcurementProject().getRequiredCommodityClassification()) {
					valoresCPV += commodity.getItemClassificationCode().getValue() + SEPARADOR;
				}
				return valoresCPV;
			}catch(Exception e) {
				return valoresCPV;
			}			
		}
	},
	TIPO_CONTRATO ("Tipo de encargo"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder) {
			try {
				return GenericodeTypes.TIPO_CONTRATO.getValue(contractFolder.getProcurementProject().getTypeCode().getValue());
			}catch(Exception e) {
				return null;
			}			
		}
	},
	LUGAR_EJECUCION ("Lugar de ejecución"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder) {
			String codigo = "";
			String descripcion = "";
			try {
				codigo = contractFolder.getProcurementProject().getRealizedLocation().getCountrySubentityCode().getValue();				
			}catch(Exception e) {
				codigo = "";
			}			
			try {
				descripcion = contractFolder.getProcurementProject().getRealizedLocation().getCountrySubentity().getValue();				
			}catch(Exception e) {
				descripcion = "";
			}
			
			if (codigo == "" && descripcion == "") {
				//Se intenta obtener el codigo del país
				try {
					codigo = contractFolder.getProcurementProject().getRealizedLocation().getAddress().getCountry().getIdentificationCode().getValue();
				}catch(Exception e) {
					codigo = "";
				}			
				try {
					descripcion = contractFolder.getProcurementProject().getRealizedLocation().getAddress().getCountry().getName().getValue();
				}catch(Exception e) {
					descripcion = "";
				}
				
			}
			
			return codigo + " - " + descripcion;
		}
	},
	ORGANO_CONTRATACION ("Órgano de Contratación"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder) {
			try {
				return contractFolder.getLocatedContractingParty().getParty().getPartyName().get(0).getName().getValue();
			}catch(Exception e) {
				return null;
			}			
		}
	},
	ID_PLATAFORMA_OC ("ID OC en PLACSP"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder) {
			try {
				String idPlatOC = "";
				for (PartyIdentificationType partyIdentificationType : contractFolder.getLocatedContractingParty().getParty().getPartyIdentification()) {
					if (partyIdentificationType.getID().getSchemeName().compareTo("ID_PLATAFORMA") == 0){
						idPlatOC = partyIdentificationType.getID().getValue();
					}
				}
				return idPlatOC;
			}catch(Exception e) {
				return null;
			}			
		}
	},
	NIF_OC ("NIF OC"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder) {
			try {
				String nif = "";
				for (PartyIdentificationType partyIdentificationType : contractFolder.getLocatedContractingParty().getParty().getPartyIdentification()) {
					if (partyIdentificationType.getID().getSchemeName().compareTo("NIF") == 0){
						nif = partyIdentificationType.getID().getValue();
					}
				}
				return nif;
			}catch(Exception e) {
				return null;
			}			
		}
	},
	DIR3 ("DIR3"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder) {
			try {
				String dir3 = "";
				for (PartyIdentificationType partyIdentificationType : contractFolder.getLocatedContractingParty().getParty().getPartyIdentification()) {
					if (partyIdentificationType.getID().getSchemeName().compareTo("DIR3") == 0){
						dir3 = partyIdentificationType.getID().getValue();
					}
				}				
				return dir3;
			}catch(Exception e) {
				return null;
			}			
		}
	},
	ENLACE_PERFIL_CONTRATANTE ("Enlace al Perfil de Contratante del OC"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder) {
			try {
				return contractFolder.getLocatedContractingParty().getBuyerProfileURIID().getValue();
			}catch(Exception e) {
				return null;
			}			
		}
	},
	TIPO_ADMINISTRACION ("Tipo de Administración"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder) {
			try {				
				return GenericodeTypes.TIPO_ADMINISTRACION.getValue(contractFolder.getLocatedContractingParty().getContractingPartyTypeCode().getValue());
			}catch(Exception e) {
				return null;
			}			
		}
	},
	CODIGO_POSTAL ("Código Postal"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder) {
			try {			
				return contractFolder.getLocatedContractingParty().getParty().getPostalAddress().getPostalZone().getValue();
			}catch(Exception e) {
				return null;
			}			
		}
	},
	FECHA_ACUERDO_EMP ("Fecha del acuerdo del encargo", EnumFormatos.FECHA_CORTA){
		@Override
		public GregorianCalendar valorCodice(ContractFolderStatusType contractFolder) {
			try {
				return contractFolder.getTenderResult().get(0).getAwardDate().getValue().toGregorianCalendar();
			} catch (Exception e) {
				return null;
			}
		}
	},
	MEDIO_PROPIO_PERSONIFICADO ("Medio propio personificado"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder) {
			try {			
				return contractFolder.getTenderResult().get(0).getWinningParty().getPartyName().get(0).getName().getValue();
			}catch(Exception e) {
				return null;
			}			
		}
	},
	MEDIO_PROPIO_PERSONIFICADO_NIF ("NIF Medio propio personificado"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder) {
			try {
				String nif = "";
				for (PartyIdentificationType partyIdentificationType : contractFolder.getTenderResult().get(0).getWinningParty().getPartyIdentification()) {
					if (partyIdentificationType.getID().getSchemeName().compareTo("NIF") == 0){
						nif = partyIdentificationType.getID().getValue();
					}
				}
				return nif;
			}catch(Exception e) {
				return null;
			}					
		}
	},
	MEDIO_PROPIO_PERSONIFICADO_ID_PLATAFORMA ("ID_PLATAFORMA Medio propio personificado"){
		@Override
		public String valorCodice(ContractFolderStatusType contractFolder) {
			try {
				String idPlataforma = "";
				for (PartyIdentificationType partyIdentificationType : contractFolder.getTenderResult().get(0).getWinningParty().getPartyIdentification()) {
					if (partyIdentificationType.getID().getSchemeName().compareTo("ID_PLATAFORMA") == 0){
						idPlataforma = partyIdentificationType.getID().getValue();
					}
				}
				return idPlataforma;
			}catch(Exception e) {
				return null;
			}					
		}
	};

	private final static String SEPARADOR = ";";
	
	private final String titulo;
	private final EnumFormatos formato;

	DatosEMP(String name, EnumFormatos format) {
		this.titulo = name;
		this.formato = format;
	}
	
	DatosEMP(String name){
		this.titulo = name;
		this.formato = EnumFormatos.TEXTO;
	}
	
	
	public String getTiulo() {	
		return titulo;
	}
	
	public EnumFormatos getFormato() {
		return formato;
	}
	
	
	public abstract Object valorCodice(ContractFolderStatusType contractFolder);
	

}
