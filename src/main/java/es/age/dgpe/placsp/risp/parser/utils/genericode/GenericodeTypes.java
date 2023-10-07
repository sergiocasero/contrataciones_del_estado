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
package es.age.dgpe.placsp.risp.parser.utils.genericode;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.age.dgpe.placsp.risp.parser.view.ParserController;

public enum GenericodeTypes {
	ESTADO("/gc/SyndicationContractFolderStatusCode-2.04.gc"),
	TIPO_CONTRATO("/gc/ContractCode-2.08.gc"),
	TIPO_PROCEDIMIENTO("/gc/SyndicationTenderingProcessCode-2.07.gc"),
	SISTEMA_CONTRATACION("/gc/ContractingSystemTypeCode-2.08.gc"),
	TRAMITACION("/gc/DiligenceTypeCode-1.04.gc"),
	PRESENTACION_OFERTA("/gc/TenderDeliveryCode-1.04.gc"),
	RESULTADO("/gc/TenderResultCode-2.02.gc"),
	TIPO_ADMINISTRACION("/gc/ContractingAuthorityCode-1.04.gc"),
	CODIGO_FINANCIACION("/gc/FundingProgramCode-2.08.gc"),
	ESTADO_CONSULTA_PRELIMINAR("/gc/PreliminaryMarketConsultationStatusCode-2.09.gc"),
	TIPO_CONSULTA_PRELIMINAR("/gc/PreliminaryMarketConsultationTypeCode-2.09.gc");
	
	
	
	private final Logger logger = LogManager.getLogger(ParserController.class.getName());

	
	private HashMap<String, String> codes = null;
	
	GenericodeTypes(String nombreGenericode){
		logger.debug("Se lee la lista de códigos " + nombreGenericode);
		try {
			codes = GenericodeManager.generateMap(GenericodeTypes.class.getResourceAsStream(nombreGenericode));
		} catch (Exception e) {
			logger.error("Se produjo un error al cargar la lista de códigos " + nombreGenericode);
		}
	}
	
	public String getValue(String key) {
		if (codes.containsKey(key)) {
			return codes.get(key);
		}
		else {
			return key;
		}
		
		
	}

}
