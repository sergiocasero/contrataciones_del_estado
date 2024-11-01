package es.age.dgpe.placsp.risp.parser.model;

import ext.place.codice.common.caclib.ContractFolderStatusType;

public interface DatosCodice {
	public abstract Object valorCodice(ContractFolderStatusType contractFolder);
	
	
	
	public EnumFormatos getFormato();
}
