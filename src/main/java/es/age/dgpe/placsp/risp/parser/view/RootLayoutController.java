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
package es.age.dgpe.placsp.risp.parser.view;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.age.dgpe.placsp.risp.parser.MainApp;
import es.age.dgpe.placsp.risp.parser.utils.Config;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;

public class RootLayoutController implements Initializable {
	
	private final Logger logger = LogManager.getLogger(ParserController.class.getName());

	@FXML
	private MenuItem aboutOpenPLACSP;
	
	@FXML
	private MenuItem helpAlert;
	
	@FXML
	private MenuItem portalDatosAbiertos;
	
	
	// Reference to the main application.
	private MainApp mainApp;

	Alert aInfoAbout; 

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		aInfoAbout = new Alert(AlertType.NONE);

		EventHandler<ActionEvent> eventInfoAbout = new 
				EventHandler<ActionEvent>() { 
			public void handle(ActionEvent e) { 
				// set alert type 
				aInfoAbout.setAlertType(AlertType.INFORMATION);
				aInfoAbout.setTitle("Sobre OpenPLACSP");
				aInfoAbout.setHeaderText("");

				aInfoAbout.setContentText("Versión: " + Config.getProperty("open-placsp.version") +  "\n"+
						"Licencia: EUPL-1.2\n"+
						"Contacto: agregacion.contratacionsectorpublico@hacienda.gob.es \n" +
						"Más información en https://www.contrataciondelsectorpublico.gob.es");
				aInfoAbout.show(); 
			} 
		};
		
		
		EventHandler<ActionEvent> eventHelpAlert = new 
				EventHandler<ActionEvent>() { 
			public void handle(ActionEvent e) { 
				try {
					File file = new File(Config.getProperty("open-placsp.documentation.path"));
					HostServices hostServices = mainApp.getHostServices();
					hostServices.showDocument(file.getAbsolutePath());
				} catch (Exception ex) {
					logger.error("Error al aceder a la documentación " + ex.getMessage());
					aInfoAbout.setAlertType(AlertType.ERROR);
					aInfoAbout.setHeaderText(null);
					aInfoAbout.setContentText("Se produjo un error al acceder a la documentación. Consulte directamente en el instalable de la aplicación o en el portal de datos abiertos del Ministerio de Haciedna");
					aInfoAbout.show();
				}
			} 
		};
		
		aboutOpenPLACSP.setOnAction(eventInfoAbout);
		helpAlert.setOnAction(eventHelpAlert);
	}
	

	@FXML
	private void goToDatosAbiertosPLACSP() {
		try {
			logger.info("Acceso al portal de datos abiertos");
			java.awt.Desktop.getDesktop().browse(new URI("https://www.hacienda.gob.es/es-ES/GobiernoAbierto/Datos%20Abiertos/Paginas/licitaciones_plataforma_contratacion.aspx"));
		} catch (IOException e) {
			e.printStackTrace();
			String auxError = "Excepción de entrada/salida, con el navegador, si desea visitar la página de datos abiertos, use la siguiente dirección: \n"
					+ "https://www.hacienda.gob.es/es-ES/GobiernoAbierto/Datos%20Abiertos/Paginas/licitaciones_plataforma_contratacion.aspx";
			logger.error(auxError);
			logger.debug(e.getStackTrace());


		} catch (URISyntaxException e) {
			e.printStackTrace();
			String auxError = "Excepción de interpretación de la URI, con el navegador, si desea visitar la página de datos abiertos, use la siguiente dirección: \n"
					+ "https://www.hacienda.gob.es/es-ES/GobiernoAbierto/Datos%20Abiertos/Paginas/licitaciones_plataforma_contratacion.aspx";
			logger.error(auxError);
			logger.debug(e.getStackTrace());

		}
	}
	
}
