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
package es.age.dgpe.placsp.risp.parser;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.age.dgpe.placsp.risp.parser.view.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class MainApp extends Application{

	 private Stage primaryStage;
	 private BorderPane rootLayout;
	 
	 private static final Logger logger = LogManager.getLogger(MainApp.class);
	 


	    @Override
	    public void start(Stage primaryStage) {
	    	logger.debug("Inicio de la aplicación");
	    	
	    	
	        this.primaryStage = primaryStage;
	        this.primaryStage.setTitle("OpenPLACSP");
	        this.primaryStage.getIcons().add(new Image("/images/icon.gif"));
	        

	        initRootLayout();

	        showParserOverview();
	    }
	    
	    /**
	     * Initializes the root layout.
	     */
	    public void initRootLayout() {
	        try {
	            // Load root layout from fxml file.
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
	            rootLayout = (BorderPane) loader.load();
	            
	            // Show the scene containing the root layout.
	            Scene scene = new Scene(rootLayout);
	            
	            RootLayoutController controller = loader.getController();
	            controller.setMainApp(this);
	            
	            primaryStage.setScene(scene);
	            primaryStage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    /**
	     * Shows the person overview inside the root layout.
	     */
	    public void showParserOverview() {
	        try {
	            // Load person overview.
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(MainApp.class.getResource("view/Parser.fxml"));
	            AnchorPane personOverview = (AnchorPane) loader.load();
	            
	            // Set person overview into the center of root layout.
	            rootLayout.setCenter(personOverview);
	            

	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    
		/**
		 * Returns the main stage.
		 * @return
		 */
		public Stage getPrimaryStage() {
			return primaryStage;
		}

	    public static void main(String[] args) {
	        launch(args);
	    }


		
		

	

}
