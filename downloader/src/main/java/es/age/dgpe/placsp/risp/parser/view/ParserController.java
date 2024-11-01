
package es.age.dgpe.placsp.risp.parser.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.purl.atompub.tombstones._1.DeletedEntryType;
import org.w3._2005.atom.EntryType;
import org.w3._2005.atom.FeedType;
import org.w3._2005.atom.LinkType;

import es.age.dgpe.placsp.risp.parser.model.DatosCPM;
import es.age.dgpe.placsp.risp.parser.model.DatosEMP;
import es.age.dgpe.placsp.risp.parser.model.DatosLicitacionGenerales;
import es.age.dgpe.placsp.risp.parser.model.DatosResultados;
import es.age.dgpe.placsp.risp.parser.model.SpreeadSheetManager;
import ext.place.codice.common.caclib.ContractFolderStatusType;
import ext.place.codice.common.caclib.PreliminaryMarketConsultationStatusType;

public class ParserController {

	private final Logger logger = LogManager.getLogger(ParserController.class.getName());

	public String textFieldDirOrigen;

	public String textFieldOutputFile;

	public boolean isDosTablas = false;

	public String n_licitaciones;

	public String n_ficheros;


	// Reference to the main application.

	private static Unmarshaller atomUnMarshaller;


	ArrayList<DatosLicitacionGenerales> seleccionLicitacionGenerales;
	ArrayList<DatosResultados> seleccionLicitacionResultados;
	ArrayList<DatosEMP> seleccionEncargosMediosPropios;
	ArrayList<DatosCPM> seleccionConsultasPreliminares;


	/**
	 * The constructor. The constructor is called before the initialize() method.
	 */
	public ParserController() {

	}
	
	private void selectDir(TextField inout, boolean isIn) {
		String previousPath = inout.getText().trim();
		FileChooser fileChooser = new FileChooser();
		if(isIn) {
			fileChooser.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("ATOM", "*.atom"));
		} else {
			fileChooser.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("XLSX", "*.xlsx"));
		}
		if(previousPath.length()>0){
			Path currentPath = Paths.get(previousPath);
			fileChooser.setInitialDirectory(new File(currentPath.getParent().toString()));
		}
		Stage currentStage = (Stage) inout.getScene().getWindow();
		//la diferencia principal con el selectDirIn es el showSaveDialog en lugar de showOpenDialog y la restricci�n de extensi�n
		File file;
		if(isIn) {
			file = fileChooser.showOpenDialog(currentStage);
		} else {
			file = fileChooser.showSaveDialog(currentStage);
		}
		
		if (file != null) {
			inout.setText(file.getAbsolutePath());
		}
	}

	public Boolean procesarDirectorio() {
		// Collecci�n que registra las entries que ya han sido procesadas
		HashSet<String> entriesProcesadas = new HashSet<String>();
		HashMap<String, GregorianCalendar> entriesDeleted = new HashMap<String, GregorianCalendar>();
		int numeroEntries = 0;
		int numeroFicherosProcesados = 0;

		FeedType res = null;
		FileOutputStream output_file = null;
		InputStreamReader inStream = null;

		try {

			//Se crea el Stream de salida en el path indicado
			output_file = new FileOutputStream(new File(textFieldOutputFile));

			logger.debug("Se realiza la revisi�n de los datos seleccionados");

			// Create the JAXBContext
			JAXBContext jc = JAXBContext.newInstance(
					"org.w3._2005.atom:org.dgpe.codice.common.caclib:org.dgpe.codice.common.cbclib:ext.place.codice.common.caclib:ext.place.codice.common.cbclib:org.purl.atompub.tombstones._1");
			atomUnMarshaller = jc.createUnmarshaller();


			//Se crean las hojas necesarias
			logger.debug("Creaci�n de hojas de c�lculo");
			SpreeadSheetManager spreeadSheetManager = new SpreeadSheetManager(isDosTablas, seleccionEncargosMediosPropios.size()>0, seleccionConsultasPreliminares.size()>0);

			logger.debug("Se comienzan a a�adir los t�tulos");
			insertarTitulos(spreeadSheetManager);
			//Se cambian los tama�os de las columnas
			spreeadSheetManager.updateColumnsSize();
			logger.info("T�tulos a�adidos y tama�os de columnas ajustados");

			// Se comprueba que exista el ficheroRISP a procesar
			File ficheroRISP = new File(textFieldDirOrigen);
			String directorioPath = ficheroRISP.getParent();
			boolean existeFicheroRisp = ficheroRISP.exists() && ficheroRISP.isFile();

			if (existeFicheroRisp) {
				logger.info("Directorio originen de ficheros RISP-PLACSP: " + directorioPath);
				logger.info("Fichero r�iz: " + ficheroRISP.getName());
			} else {
				logger.error("No se puede acceder al fichero " + textFieldDirOrigen);
			}

			File[] lista_ficherosRISP = ficheroRISP.getParentFile().listFiles();
			logger.info("N�mero previsto de ficheros a procesar: " + lista_ficherosRISP.length);

			// calculo de cada salto
			double saltoBar = 1.00 / lista_ficherosRISP.length;
			double saltoAcumuladoBar = 0;

			while (existeFicheroRisp) {
				logger.info("Procesando fichero: " + ficheroRISP.getName());

				saltoAcumuladoBar += saltoBar;
				logger.info("Ratio de archivos procesados: " + saltoAcumuladoBar * 100.00 + " %");

				res = null;
				inStream = new InputStreamReader(new FileInputStream(ficheroRISP), StandardCharsets.UTF_8);
				res = ((JAXBElement<FeedType>) atomUnMarshaller.unmarshal(inStream)).getValue();

				// Se a�aden las licitaciones que han dejado de ser v�lidas
				if (res.getAny() != null) {
					for (int indice = 0; indice < res.getAny().size(); indice++) {
						DeletedEntryType deletedEntry = ((JAXBElement<DeletedEntryType>) res.getAny().get(indice)).getValue();
						if (!entriesDeleted.containsKey(deletedEntry.getRef())) {
							entriesDeleted.put(deletedEntry.getRef(), deletedEntry.getWhen().toGregorianCalendar());
						}
					}
				}

				// Se recorren las licitaciones (elementos entry)
				numeroEntries += res.getEntry().size();
				for (EntryType entry : res.getEntry()) {
					// Se comprueba si ya se ha procesado una entry con el mismo identoficador y que es m�s reciente
					if (!entriesProcesadas.contains(entry.getId().getValue())) {
						// Se comprueba si se encuentra en la la lista de licitaciones Deleted
						GregorianCalendar fechaDeleted = null;
						if (entriesDeleted.containsKey(entry.getId().getValue())) {
							fechaDeleted = entriesDeleted.get(entry.getId().getValue());
						}

						//Se compruebe si se trata de una licitaci�n, un encargo a medio propio o un una consulta preliminar de mercado
						boolean isCPM = false;
						if (((JAXBElement<?>)entry.getAny().get(0)).getValue() instanceof PreliminaryMarketConsultationStatusType) {
							isCPM = true;
						}

						if (isCPM) {
							//Se trata de una consulta preliminar de mercado, solo cuando se han seleccionado campos
							if(seleccionConsultasPreliminares.size()>0) {
								procesarCPM(entry, spreeadSheetManager.getWorkbook().getSheet(SpreeadSheetManager.CPM), fechaDeleted, seleccionConsultasPreliminares);
							}
						}else {
							//Se trata de una licitaci�n o de un encargo a medio propio
							//Si existe el resultCode con valor 11, entonces es EMP. Si no, es licitaci�n
							boolean isEMP = false;
							try {
								//Se comprueba si es un EMP
								isEMP = (((JAXBElement<ContractFolderStatusType>) entry.getAny().get(0)).getValue().getTenderResult().get(0).getResultCode().getValue().compareTo("11") == 0);
							}
							catch(Exception e){
								isEMP = false;
							}

							if (isEMP) {
								//Se trata de un EMP
								//y se han seleccionado campos
								if(seleccionEncargosMediosPropios.size()>0) {
									procesarEncargo(entry, spreeadSheetManager.getWorkbook().getSheet(SpreeadSheetManager.EMP), fechaDeleted, seleccionEncargosMediosPropios);
								}
							} else {
								//Es una licitaci�n
								if (isDosTablas) {
									//La salida es en dos tablas
									procesarEntry(entry, spreeadSheetManager.getWorkbook().getSheet(SpreeadSheetManager.LICITACIONES), fechaDeleted, seleccionLicitacionGenerales);
									procesarEntryResultados(entry, spreeadSheetManager.getWorkbook().getSheet(SpreeadSheetManager.RESULTADOS), fechaDeleted, seleccionLicitacionResultados);
								}else {
									//La salida es en una tabla
									procesarEntryCompleta(entry, spreeadSheetManager.getWorkbook().getSheet(SpreeadSheetManager.LICITACIONES), fechaDeleted, seleccionLicitacionGenerales, seleccionLicitacionResultados);
								}
							}

						}

						entriesProcesadas.add(entry.getId().getValue());
					}
				}
				// se comprueba cu�l es el siguiente fichero a procesar
				for (LinkType linkType : res.getLink()) {
					existeFicheroRisp = false;
					if (linkType.getRel().toLowerCase().compareTo("next") == 0) {
						String[] tempArray = linkType.getHref().split("/");
						String nombreSiguienteRIPS = tempArray[tempArray.length - 1];
						ficheroRISP = new File(directorioPath + "/" + nombreSiguienteRIPS);
						existeFicheroRisp = ficheroRISP.exists() && ficheroRISP.isFile();
					}
				}
				inStream.close();
				numeroFicherosProcesados++;
			}

			logger.info("Creando el fichero " + textFieldOutputFile);
			logger.info("N�mero de ficheros procesados " + numeroFicherosProcesados);
			logger.info("N�mero de elementos entry existentes: " + numeroEntries);
			logger.info("Licitaciones insertadas en el fichero: " + entriesProcesadas.size());

			spreeadSheetManager.insertarFiltro(seleccionLicitacionGenerales.size(), seleccionLicitacionResultados.size(), seleccionEncargosMediosPropios.size(), seleccionConsultasPreliminares.size());



			logger.info("Comienzo de escritura del fichero de salida");
			spreeadSheetManager.getWorkbook().write(output_file); // write excel document to output stream
			output_file.close(); // close the file
			spreeadSheetManager.getWorkbook().close();
			// para mostrar algunos resultados en la interfaz de usuario
			n_licitaciones = (Integer.toString(entriesProcesadas.size()));
			n_ficheros = (Integer.toString(numeroFicherosProcesados));



			logger.info("Fin del proceso de generaci�n del fichero");



		} catch (JAXBException e) {// ventanas de error para las excepciones contempladas
			e.printStackTrace();
			String auxError = "Error al procesar el fichero ATOM. No se puede continuar con el proceso.";
			logger.error(auxError);
			logger.debug(e.getStackTrace());
		} catch (FileNotFoundException e) {
			String auxError = "Error al generar el fichero de salida. No se pudo crear un fichero en la ruta indicada o no pudo ser abierto por alguna otra raz�n.";
			logger.error(auxError);
			logger.debug(e.toString());
		} catch (Exception e) {
			// error inesperado
			String auxError = "Error inesperado, revise la configuraci�n y el log...";
			e.printStackTrace();
			logger.error(auxError);
			logger.debug(e.getStackTrace());
			logger.debug(e.getMessage());
		} finally {
			return true;
		}
	}

	/**
	 * Funci�n paara procesr una entry y extraer todos sus datos.
	 * @param entry
	 * @param sheet 
	 */
	private void procesarEntry(EntryType entry, SXSSFSheet sheet, GregorianCalendar fechaDeleted, ArrayList<DatosLicitacionGenerales> buscadorDatosSeleecionables) {		
		logger.debug("Procesando entry: " + entry.toString());

		Cell cell;
		ContractFolderStatusType contractFolder = ((JAXBElement<ContractFolderStatusType>) entry.getAny().get(0)).getValue();

		Row row = sheet.createRow(sheet.getLastRowNum()+1);

		//Se obtienen los datos de la licitaci�n
		int cellnum = 0;

		//Se a�aden los datos b�sicos y obligatorios de la entry: id, lin y updated
		cell = row.createCell(cellnum++);
		cell.setCellValue(entry.getId().getValue().substring(entry.getId().getValue().lastIndexOf("/")+1));
		cell = row.createCell(cellnum++);
		cell.setCellValue(entry.getLink().get(0).getHref());



		//Se obtiene la fechaUpdated de la entry
		GregorianCalendar updated = entry.getUpdated().getValue().toGregorianCalendar();

		//La fecha de actualizaci�n ser� la m�s recinte comparando la �ltima entry con la fecha deleted si existe.
		if (fechaDeleted == null || fechaDeleted.compareTo(updated) < 0) {
			//La entry es v�lida, no hay un deleted-entry posterior
			cell = row.createCell(cellnum++);
			cell.setCellValue((LocalDateTime)entry.getUpdated().getValue().toGregorianCalendar().toZonedDateTime().toLocalDateTime());
			cell.setCellStyle(SpreeadSheetManager.getCellStyleFechaLarga());
			cell = row.createCell(cellnum++);
			cell.setCellValue("VIGENTE");
		}else {
			//La entry no es v�lida, hay un deleted-entry posterior
			cell = row.createCell(cellnum++);
			cell.setCellValue((LocalDateTime)fechaDeleted.toZonedDateTime().toLocalDateTime());
			cell.setCellStyle(SpreeadSheetManager.getCellStyleFechaLarga());
			cell = row.createCell(cellnum++);
			if (((fechaDeleted.getTimeInMillis() - updated.getTimeInMillis())/1000/3660/24/365) > 5){
				cell.setCellValue("ARCHIVADA");
			}else {
				cell.setCellValue("ANULADA");
			}
		}

		for (DatosLicitacionGenerales dato: buscadorDatosSeleecionables) {
			Object datoCodice = dato.valorCodice(contractFolder); 
			cell = row.createCell(cellnum++);
			if (datoCodice instanceof BigDecimal) {
				cell.setCellValue((double) ((BigDecimal)datoCodice).doubleValue());
			}else if (datoCodice instanceof String) { 
				cell.setCellValue((String) datoCodice);
			}else if (datoCodice instanceof GregorianCalendar) {
				cell.setCellValue((LocalDateTime) ((GregorianCalendar)datoCodice).toZonedDateTime().toLocalDateTime());
			}else if (datoCodice instanceof Boolean) {
				cell.setCellValue((Boolean) datoCodice);
			}
			cell.setCellStyle(SpreeadSheetManager.getCellStyleFormato(dato.getFormato()));

		}


	}



	private void procesarEntryResultados(EntryType entry, SXSSFSheet sheet, GregorianCalendar fechaDeleted, ArrayList<DatosResultados> buscadorDatosResultados) {	
		Cell cell;

		ContractFolderStatusType contractFolder = ((JAXBElement<ContractFolderStatusType>) entry.getAny().get(0)).getValue();

		//Se obtiene el n�mero de elementos tenderResult, en caso de que tenga,
		if(contractFolder.getTenderResult() != null) {			
			for (int indice = 0; indice < contractFolder.getTenderResult().size(); indice++) {
				Row row = sheet.createRow(sheet.getLastRowNum()+1);
				int cellnum = 0;

				//Se a�aden los datos b�sicos y obligatorios de la entry: id, link y vigencia
				cell = row.createCell(cellnum++);
				cell.setCellValue(entry.getId().getValue().substring(entry.getId().getValue().lastIndexOf("/")+1));
				cell = row.createCell(cellnum++);
				cell.setCellValue(entry.getLink().get(0).getHref());

				//Se obtiene la fechaUpdated de la entry
				GregorianCalendar updated = entry.getUpdated().getValue().toGregorianCalendar();

				//La fecha de actualizaci�n ser� la m�s recinte comparando la �ltima entry con la fecha deleted si existe.
				if (fechaDeleted == null || fechaDeleted.compareTo(updated) < 0) {
					//La entry es v�lida, no hay un deleted-entry posterior
					cell = row.createCell(cellnum++);
					cell.setCellValue((LocalDateTime)entry.getUpdated().getValue().toGregorianCalendar().toZonedDateTime().toLocalDateTime());
					cell.setCellStyle(SpreeadSheetManager.getCellStyleFechaLarga());
				}else {
					//La entry no es v�lida, hay un deleted-entry posterior
					cell = row.createCell(cellnum++);
					cell.setCellValue((LocalDateTime)fechaDeleted.toZonedDateTime().toLocalDateTime());
					cell.setCellStyle(SpreeadSheetManager.getCellStyleFechaLarga());
				}

				for (DatosResultados dato: buscadorDatosResultados) {
					Object datoCodice = dato.valorCodice(contractFolder, indice); 
					cell = row.createCell(cellnum++);
					if (datoCodice instanceof BigDecimal) {
						cell.setCellValue((double) ((BigDecimal)datoCodice).doubleValue());
					}else if (datoCodice instanceof String) {
						cell.setCellValue((String) datoCodice);
					}else if (datoCodice instanceof GregorianCalendar) {
						cell.setCellValue((LocalDateTime) ((GregorianCalendar)datoCodice).toZonedDateTime().toLocalDateTime());
					}else if (datoCodice instanceof Boolean) {
						cell.setCellValue((Boolean) datoCodice);
					}
					cell.setCellStyle(SpreeadSheetManager.getCellStyleFormato(dato.getFormato()));
				}

			}
		}
	}

	/**
	 *  Se a�aden los datos en una �nica hoja. Los "datos seleccionables" se repiten por cada resultado. Si no hay resultados, se insertan una �nica vez
	 * @param entry
	 * @param sheet
	 * @param fechaDeleted
	 * @param buscadorDatosResultados
	 */
	private void procesarEntryCompleta(EntryType entry, SXSSFSheet sheet, GregorianCalendar fechaDeleted,
			ArrayList<DatosLicitacionGenerales> buscadorDatosSeleccionables,
			ArrayList<DatosResultados> buscadorDatosResultados) {

		Cell cell;
		ContractFolderStatusType contractFolder = ((JAXBElement<ContractFolderStatusType>) entry.getAny().get(0)).getValue();

		//Se obtiene el n�mero de elementos tenderResult, en caso de que tenga,
		if(contractFolder.getTenderResult().size() > 0) {
			//hay resultados en esta entry, por lo que se insertan tantas filas como resultados

			for (int indice = 0; indice < contractFolder.getTenderResult().size(); indice++) {
				//Se insertan los datos comunes
				procesarEntry(entry, sheet, fechaDeleted, buscadorDatosSeleccionables);

				//En la misma fila, se completan con los datos del tenderresult
				Row row = sheet.getRow(sheet.getLastRowNum());
				int cellnum = buscadorDatosSeleccionables.size()+4;

				for (DatosResultados dato: buscadorDatosResultados) {
					Object datoCodice = dato.valorCodice(contractFolder, indice); 
					cell = row.createCell(cellnum++);
					if (datoCodice instanceof BigDecimal) {
						cell.setCellValue((double) ((BigDecimal)datoCodice).doubleValue());
					}else if (datoCodice instanceof String) {
						cell.setCellValue((String) datoCodice);
					}else if (datoCodice instanceof GregorianCalendar) {
						cell.setCellValue((LocalDateTime) ((GregorianCalendar)datoCodice).toZonedDateTime().toLocalDateTime());
					}else if (datoCodice instanceof Boolean) {
						cell.setCellValue((Boolean) datoCodice);
					}
					cell.setCellStyle(SpreeadSheetManager.getCellStyleFormato(dato.getFormato()));
				}		
			}	
		}else {
			//No hay resultados en esta entry, solo se inserta una �nica vez
			procesarEntry(entry, sheet, fechaDeleted, buscadorDatosSeleccionables);						
		}
	}


	/**
	 * M�todo que recorre el TreeView y vuelca en los arrayList los datos seleccionados
	 */
	private void recogerDatosSeleccionados() {
		HashMap<String, String> seleccionadosArbol;

		seleccionLicitacionGenerales = new ArrayList<DatosLicitacionGenerales>();
		seleccionLicitacionResultados = new ArrayList<DatosResultados>();
		seleccionEncargosMediosPropios = new ArrayList<DatosEMP>();
		seleccionConsultasPreliminares = new ArrayList<DatosCPM>();

		Collections.addAll(seleccionLicitacionGenerales, DatosLicitacionGenerales.values());

		//Resultados de la licitaci�n. Se recorre los datos posibles buscando los selecionados
		Collections.addAll(seleccionLicitacionResultados, DatosResultados.values());

		//Encargos a medios propios. Se recorre los datos posibles buscando los selecionados
		Collections.addAll(seleccionEncargosMediosPropios, DatosEMP.values());

		//Consultas preliminares. Se recorre los datos posibles buscando los selecionados
		Collections.addAll(seleccionConsultasPreliminares, DatosCPM.values());

		if (logger.isDebugEnabled()) {
			//Se imprimen los datos seleccionados
			for (DatosLicitacionGenerales  seleccion: seleccionLicitacionGenerales) {
				logger.debug(seleccion.getTiulo());
			}

			for (DatosResultados  seleccion: seleccionLicitacionResultados) {
				logger.debug(seleccion.getTiulo());
			}

			for (DatosEMP  seleccion: seleccionEncargosMediosPropios) {
				logger.debug(seleccion.getTiulo());
			}

			for (DatosCPM  seleccion: seleccionConsultasPreliminares) {
				logger.debug(seleccion.getTiulo());
			}

		}

	}



	private void procesarEncargo(EntryType entry, SXSSFSheet sheet, GregorianCalendar fechaDeleted, ArrayList<DatosEMP> buscadorDatosSelecionables) {		
		

		Cell cell;
		ContractFolderStatusType contractFolder = ((JAXBElement<ContractFolderStatusType>) entry.getAny().get(0)).getValue();

		Row row = sheet.createRow(sheet.getLastRowNum()+1);

		//Se obtienen los datos de la licitaci�n
		int cellnum = 0;

		//Se a�aden los datos b�sicos y obligatorios de la entry: id, lin y updated
		cell = row.createCell(cellnum++);
		cell.setCellValue(entry.getId().getValue().substring(entry.getId().getValue().lastIndexOf("/")+1));
		cell = row.createCell(cellnum++);
		cell.setCellValue(entry.getLink().get(0).getHref());



		//Se obtiene la fechaUpdated de la entry
		GregorianCalendar updated = entry.getUpdated().getValue().toGregorianCalendar();

		//La fecha de actualizaci�n ser� la m�s recinte comparando la �ltima entry con la fecha deleted si existe.
		if (fechaDeleted == null || fechaDeleted.compareTo(updated) < 0) {
			//La entry es v�lida, no hay un deleted-entry posterior
			cell = row.createCell(cellnum++);
			cell.setCellValue((LocalDateTime)entry.getUpdated().getValue().toGregorianCalendar().toZonedDateTime().toLocalDateTime());
			cell.setCellStyle(SpreeadSheetManager.getCellStyleFechaLarga());
			cell = row.createCell(cellnum++);
			cell.setCellValue("VIGENTE");
		}else {
			//La entry no es v�lida, hay un deleted-entry posterior
			cell = row.createCell(cellnum++);
			cell.setCellValue((LocalDateTime)fechaDeleted.toZonedDateTime().toLocalDateTime());
			cell.setCellStyle(SpreeadSheetManager.getCellStyleFechaLarga());
			cell = row.createCell(cellnum++);
			if (((fechaDeleted.getTimeInMillis() - updated.getTimeInMillis())/1000/3660/24/365) > 5){
				cell.setCellValue("ARCHIVADA");
			}else {
				cell.setCellValue("ANULADA");
			}
		}

		for (DatosEMP dato: buscadorDatosSelecionables) {
			Object datoCodice = dato.valorCodice(contractFolder); 
			cell = row.createCell(cellnum++);
			if (datoCodice instanceof BigDecimal) {
				cell.setCellValue((double) ((BigDecimal)datoCodice).doubleValue());
			}else if (datoCodice instanceof String) { 
				cell.setCellValue((String) datoCodice);
			}else if (datoCodice instanceof GregorianCalendar) {
				cell.setCellValue((LocalDateTime) ((GregorianCalendar)datoCodice).toZonedDateTime().toLocalDateTime());
			}else if (datoCodice instanceof Boolean) {
				cell.setCellValue((Boolean) datoCodice);
			}
			cell.setCellStyle(SpreeadSheetManager.getCellStyleFormato(dato.getFormato()));

		}

	}

	private void procesarCPM(EntryType entry, SXSSFSheet sheet, GregorianCalendar fechaDeleted, ArrayList<DatosCPM> buscadorDatosSelecionables) {		
		Cell cell;
		PreliminaryMarketConsultationStatusType preliminaryMarketConsultationStatusType = ((JAXBElement<PreliminaryMarketConsultationStatusType>) entry.getAny().get(0)).getValue();

		Row row = sheet.createRow(sheet.getLastRowNum()+1);

		//Se obtienen los datos de la licitaci�n
		int cellnum = 0;

		//Se a�aden los datos b�sicos y obligatorios de la entry: id, lin y updated
		cell = row.createCell(cellnum++);
		cell.setCellValue(entry.getId().getValue().substring(entry.getId().getValue().lastIndexOf("/")+1));
		cell = row.createCell(cellnum++);
		cell.setCellValue(entry.getLink().get(0).getHref());



		//Se obtiene la fechaUpdated de la entry
		GregorianCalendar updated = entry.getUpdated().getValue().toGregorianCalendar();

		//La fecha de actualizaci�n ser� la m�s recinte comparando la �ltima entry con la fecha deleted si existe.
		if (fechaDeleted == null || fechaDeleted.compareTo(updated) < 0) {
			//La entry es v�lida, no hay un deleted-entry posterior
			cell = row.createCell(cellnum++);
			cell.setCellValue((LocalDateTime)entry.getUpdated().getValue().toGregorianCalendar().toZonedDateTime().toLocalDateTime());
			cell.setCellStyle(SpreeadSheetManager.getCellStyleFechaLarga());
			cell = row.createCell(cellnum++);
			cell.setCellValue("VIGENTE");
		}else {
			//La entry no es v�lida, hay un deleted-entry posterior
			cell = row.createCell(cellnum++);
			cell.setCellValue((LocalDateTime)fechaDeleted.toZonedDateTime().toLocalDateTime());
			cell.setCellStyle(SpreeadSheetManager.getCellStyleFechaLarga());
			cell = row.createCell(cellnum++);
			if (((fechaDeleted.getTimeInMillis() - updated.getTimeInMillis())/1000/3660/24/365) > 5){
				cell.setCellValue("ARCHIVADA");
			}else {
				cell.setCellValue("ANULADA");
			}
		}

		for (DatosCPM dato: buscadorDatosSelecionables) {
			Object datoCodice = dato.valorCodice(preliminaryMarketConsultationStatusType); 
			cell = row.createCell(cellnum++);
			if (datoCodice instanceof BigDecimal) {
				cell.setCellValue((double) ((BigDecimal)datoCodice).doubleValue());
			}else if (datoCodice instanceof String) { 
				cell.setCellValue((String) datoCodice);
			}else if (datoCodice instanceof GregorianCalendar) {
				cell.setCellValue((LocalDateTime) ((GregorianCalendar)datoCodice).toZonedDateTime().toLocalDateTime());
			}else if (datoCodice instanceof Boolean) {
				cell.setCellValue((Boolean) datoCodice);
			}
			cell.setCellStyle(SpreeadSheetManager.getCellStyleFormato(dato.getFormato()));

		}


	}


	/**
	 * M�todo que inserta los t�tulos en las hojas disponibles
	 * @param spreeadSheetManager
	 */
	private void insertarTitulos(SpreeadSheetManager spreeadSheetManager) {
		//Se a�aden los t�tulos de los elementos que se van a insertar en las hojas
		SXSSFSheet hoja;
		Row row;
		int cellnum;
		Cell cell;

		//HOJA LICITACIONES
		hoja = spreeadSheetManager.getWorkbook().getSheet(SpreeadSheetManager.LICITACIONES);
		row = hoja.createRow(0);
		cellnum = 0;
		cell = row.createCell(cellnum++);
		cell.setCellValue("Identificador");
		cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
		cell = row.createCell(cellnum++);
		cell.setCellValue("Link licitaci�n");
		cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
		cell = row.createCell(cellnum++);
		cell.setCellValue("Fecha actualizaci�n");
		cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
		cell = row.createCell(cellnum++);
		cell.setCellValue("Vigente/Anulada/Archivada");
		cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
		for (DatosLicitacionGenerales dato : seleccionLicitacionGenerales) {
			cell = row.createCell(cellnum++);
			cell.setCellValue((String) dato.getTiulo());
			cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
		}
		if (spreeadSheetManager.getWorkbook().getSheet(SpreeadSheetManager.RESULTADOS) == null) {
			for (DatosResultados dato : seleccionLicitacionResultados) {
				cell = row.createCell(cellnum++);
				cell.setCellValue((String) dato.getTiulo());
				cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
			}
		}


		//HOJA Resultados
		if (spreeadSheetManager.getWorkbook().getSheet(SpreeadSheetManager.RESULTADOS) != null) {
			hoja = spreeadSheetManager.getWorkbook().getSheet(SpreeadSheetManager.RESULTADOS);
			row = hoja.createRow(0);
			cellnum = 0;
			cell = row.createCell(cellnum++);
			cell.setCellValue("Identificador");
			cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
			cell = row.createCell(cellnum++);
			cell.setCellValue("Link licitaci�n");
			cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
			cell = row.createCell(cellnum++);
			cell.setCellValue("Fecha actualizaci�n");
			cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
			for (DatosResultados dato : seleccionLicitacionResultados) {
				cell = row.createCell(cellnum++);
				cell.setCellValue((String) dato.getTiulo());
				cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
			}
		}


		//HOJA Encargos a medios propios
		if (spreeadSheetManager.getWorkbook().getSheet(SpreeadSheetManager.EMP) != null) {
			hoja = spreeadSheetManager.getWorkbook().getSheet(SpreeadSheetManager.EMP);
			row = hoja.createRow(0);
			cellnum = 0;
			cell = row.createCell(cellnum++);
			cell.setCellValue("Identificador");
			cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
			cell = row.createCell(cellnum++);
			cell.setCellValue("Link Encargo");
			cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
			cell = row.createCell(cellnum++);
			cell.setCellValue("Fecha actualizaci�n");
			cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
			cell = row.createCell(cellnum++);
			cell.setCellValue("Vigente/Anulada/Archivada");
			cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
			for (DatosEMP dato : seleccionEncargosMediosPropios) {
				cell = row.createCell(cellnum++);
				cell.setCellValue((String) dato.getTiulo());
				cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
			}
		}

		//HOJA Consultas preliminares mercado
		if (spreeadSheetManager.getWorkbook().getSheet(SpreeadSheetManager.CPM) != null) {
			hoja = spreeadSheetManager.getWorkbook().getSheet(SpreeadSheetManager.CPM);
			row = hoja.createRow(0);
			cellnum = 0;
			cell = row.createCell(cellnum++);
			cell.setCellValue("Identificador");
			cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
			cell = row.createCell(cellnum++);
			cell.setCellValue("Link Consulta");
			cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
			cell = row.createCell(cellnum++);
			cell.setCellValue("Fecha actualizaci�n");
			cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
			cell = row.createCell(cellnum++);
			cell.setCellValue("Vigente/Anulada/Archivada");
			cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
			for (DatosCPM dato : seleccionConsultasPreliminares) {
				cell = row.createCell(cellnum++);
				cell.setCellValue((String) dato.getTiulo());
				cell.setCellStyle(SpreeadSheetManager.getCellStyleTitulo());
			}
		}
	}

	public Boolean generarXLSX(){
		recogerDatosSeleccionados();
		return procesarDirectorio();
	}




}
