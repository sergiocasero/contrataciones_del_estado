
package es.age.dgpe.placsp.risp.parser.model;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConstants;

import org.dgpe.codice.common.caclib.CommodityClassificationType;
import org.dgpe.codice.common.caclib.PartyIdentificationType;

import es.age.dgpe.placsp.risp.parser.utils.genericode.GenericodeTypes;
import ext.place.codice.common.caclib.AdditionalPublicationDocumentReferenceType;
import ext.place.codice.common.caclib.AdditionalPublicationStatusType;
import ext.place.codice.common.caclib.NoticeInfoType;
import ext.place.codice.common.caclib.PreliminaryMarketConsultationStatusType;
 
public enum DatosCPM{
	PRIMERA_PUBLICACION("Primera publicaci�n", EnumFormatos.FECHA_CORTA) {
	@Override
		public GregorianCalendar valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket) {
			GregorianCalendar primeraPublicacion = null;

			try {
				// Se recorren los validnoticeinfo
				for (NoticeInfoType noticeInfo : preliminaryMarket.getValidNoticeInfo()) {
					try {
						// No se tiene en cuenta si es anuncio previo
						if (noticeInfo.getNoticeTypeCode().getValue().compareTo("DOC_PIN") != 0) {
							// Se recorren los medios de publicacion
							for (AdditionalPublicationStatusType additionalPublicationStatus : noticeInfo
									.getAdditionalPublicationStatus()) {
								// Se comprueba si el medio es el perfil de contratante
								if (additionalPublicationStatus.getPublicationMediaName().getValue().equalsIgnoreCase("Perfil del Contratante")) {
									// Se obtiene la fecha m�s antigua
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
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket){
			try {
				String estado = GenericodeTypes.ESTADO_CONSULTA_PRELIMINAR.getValue(preliminaryMarket.getPreliminaryMarketConsultationStatusCode().getValue());
				return estado;
			}catch (Exception e) {
				return null;
			}
		}
	},
	NUMERO_EXPEDIENTE ("N�mero de consulta preliminar"){
		@Override
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket){
			try {
				String numExpediente = preliminaryMarket.getPreliminaryMarketConsultationID().getValue();
				return numExpediente;
			}catch (Exception e) {
				return null;
			}
		}
	},
	OBJETO_CONTRATO ("Objeto de la consulta"){
		@Override
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket){
			try {
				String objeto = preliminaryMarket.getProcurementProject().getName().get(0).getValue();
				return objeto;
			}catch (Exception e) {
				return null;
			}
		}
	},
	FECHA_INICIO_CONSULTA ("Fecha de incio de la consulta", EnumFormatos.FECHA_CORTA){
		@Override
		public GregorianCalendar valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket) {
			try {
				return preliminaryMarket.getPlannedDate().getValue().toGregorianCalendar();
			} catch (Exception e) {
				return null;
			}
		}
	},
	FECHA_LIMITE_RESPUESTA ("Fecha l�mite de respuesta", EnumFormatos.FECHA_CORTA){
		@Override
		public GregorianCalendar valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket) {
			try {
				return preliminaryMarket.getLimitDate().getValue().toGregorianCalendar();
			} catch (Exception e) {
				return null;
			}
		}
	},
	DIRECCION_PRESENTACION ("Direcci�n para presentaci�n"){
		@Override
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket){
			try {
				String direccionRespuesta = preliminaryMarket.getAttachment().getExternalReference().getURI().getValue();
				return direccionRespuesta;
			}catch (Exception e) {
				return null;
			}
		}
	},
	TIPO_CONSULTA ("Tipo de consulta"){
		@Override
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket){
			try {
				String tipoConsulta = GenericodeTypes.TIPO_CONSULTA_PRELIMINAR.getValue(preliminaryMarket.getConditionTypeCode().getValue());
				return tipoConsulta;
			}catch (Exception e) {
				return null;
			}
		}
	},
	CONDICIONES_CONSULTA ("Condiciones o t�rminos de env�o de la consulta"){
		@Override
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket){
			try {
				String objeto = preliminaryMarket.getConditionsText().getValue();
				return objeto;
			}catch (Exception e) {
				return null;
			}
		}
	},
	FUTURA_LIC_TIPO_CONTRATO ("Futura licitaci�n. Tipo de contrato"){
		@Override
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket) {
			try {
				return GenericodeTypes.TIPO_CONTRATO.getValue(preliminaryMarket.getProcurementProject().getTypeCode().getValue());
			} catch (Exception e) {
				return null;
			}
		}
	},
	FUTURA_LIC_OBJETO ("Futura licitaci�n. Objeto"){
		@Override
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket) {
			try {
				return preliminaryMarket.getProcurementProject().getName().get(0).getValue();
			} catch (Exception e) {
				return null;
			}
		}
	},
	FUTURA_LIC_PROCEDIMIENTO ("Futura licitaci�n. Procedimiento"){
		@Override
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket) {
			try {
				return GenericodeTypes.TIPO_PROCEDIMIENTO.getValue(preliminaryMarket.getTenderingProcess().getProcedureCode().getValue());
			} catch (Exception e) {
				return null;
			}
		}
	},
	CPV ("CPV"){
		@Override
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket) {
			String valoresCPV = "";
			try {
				for (CommodityClassificationType commodity : preliminaryMarket.getProcurementProject().getRequiredCommodityClassification()) {
					valoresCPV += commodity.getItemClassificationCode().getValue() + SEPARADOR;
				}
				return valoresCPV;
			}catch(Exception e) {
				return valoresCPV;
			}			
		}
	},
	ORGANO_CONTRATACION ("�rgano de Contrataci�n"){
		@Override
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket) {
			try {
				return preliminaryMarket.getLocatedContractingParty().getParty().getPartyName().get(0).getName().getValue();
			}catch(Exception e) {
				return null;
			}			
		}
	},
	ID_PLATAFORMA_OC ("ID OC en PLACSP"){
		@Override
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket) {
			try {
				String idPlatOC = "";
				for (PartyIdentificationType partyIdentificationType : preliminaryMarket.getLocatedContractingParty().getParty().getPartyIdentification()) {
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
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket) {
			try {
				String nif = "";
				for (PartyIdentificationType partyIdentificationType : preliminaryMarket.getLocatedContractingParty().getParty().getPartyIdentification()) {
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
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket) {
			try {
				String dir3 = "";
				for (PartyIdentificationType partyIdentificationType : preliminaryMarket.getLocatedContractingParty().getParty().getPartyIdentification()) {
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
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket) {
			try {
				return preliminaryMarket.getLocatedContractingParty().getBuyerProfileURIID().getValue();
			}catch(Exception e) {
				return null;
			}			
		}
	},
	TIPO_ADMINISTRACION ("Tipo de Administraci�n"){
		@Override
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket) {
			try {				
				return GenericodeTypes.TIPO_ADMINISTRACION.getValue(preliminaryMarket.getLocatedContractingParty().getContractingPartyTypeCode().getValue());
			}catch(Exception e) {
				return null;
			}			
		}
	},
	CODIGO_POSTAL ("C�digo Postal"){
		@Override
		public String valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket) {
			try {			
				return preliminaryMarket.getLocatedContractingParty().getParty().getPostalAddress().getPostalZone().getValue();
			}catch(Exception e) {
				return null;
			}			
		}
	};

	private final static String SEPARADOR = ";";
	
	private final String titulo;
	private final EnumFormatos formato;

	DatosCPM(String name, EnumFormatos format) {
		this.titulo = name;
		this.formato = format;
	}
	
	DatosCPM(String name){
		this.titulo = name;
		this.formato = EnumFormatos.TEXTO;
	}
	
	
	public String getTiulo() {	
		return titulo;
	}
	
	public EnumFormatos getFormato() {
		return formato;
	}
	
	
	public abstract Object valorCodice(PreliminaryMarketConsultationStatusType preliminaryMarket);
	

}
