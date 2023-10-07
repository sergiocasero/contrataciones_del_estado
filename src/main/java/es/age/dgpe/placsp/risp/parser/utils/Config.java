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
 * Véase la Licencia en el idioma concreto que rige los permisos y limitaciones que establece la Licencia.*/

package es.age.dgpe.placsp.risp.parser.utils;

import java.io.InputStream;
import java.util.Properties;
;
public class Config {
	
	private static final String  FILE_PROPERTIES = "/open-placsp.properties";

	private static Properties defaultProps = new Properties();
	static {
		try {	
			InputStream in = Config.class.getResourceAsStream(FILE_PROPERTIES);
			defaultProps.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getProperty(String key) {
		return defaultProps.getProperty(key);
	}

}
