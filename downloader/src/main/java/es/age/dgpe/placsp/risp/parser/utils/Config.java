
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
