package es.age.dgpe.placsp.risp.parser

import es.age.dgpe.placsp.risp.parser.view.ParserController
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


private val logger = Logger.getLogger(ParserController::class.java.name)

fun main(args: Array<String>) {
    bulkProcessing(2018, 2024)
}

fun deletePreviousOutput() {
    File("./output").deleteRecursively()
}

fun bulkProcessing(fromYear: Int, toYear: Int) {
    // get all year folders inside ./documents
    val years = listOf(fromYear..toYear).flatten().map { it.toString() }

    years.forEach { year ->
        // get all month folders inside ./documents/$year
        listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12").forEach {month ->
            if(!isAlreadyDownloaded(month, year)) {
                val zipDir = downloadContractZip(month, year)
                extractContractsFrom(month, year, zipDir)
            }
        }

        if(!isAlreadyDownloaded("minor", year)) {
            val minorContractZipDir = downloadMinorContracts(year)
            extractContractsFrom("minor", year, minorContractZipDir)
        }
    }
}

fun isAlreadyDownloaded(month: String, year: String): Boolean {
    val outputFile = "./output/$year/$month/${month}_$year.xlsx"
    return File(outputFile).exists()
}

fun extractContractsFrom(month: String, year: String, zipDir: String) {
    val parser = ParserController()
    val filename = "licitacionesPerfilesContratanteCompleto3.atom"
    val atomFile = "$zipDir/$filename"
    logger.info("Parsing $atomFile")

    // create year and month folder
    File("./output").mkdirs()
    File("./output/$year").mkdirs()
    File("./output/$year/$month").mkdirs()

    val outputFile = "./output/$year/$month/${month}_$year.xlsx"
    File(outputFile).delete()

    parser.textFieldDirOrigen = atomFile
    parser.textFieldOutputFile = outputFile
    parser.generarXLSX()
}

fun downloadMinorContracts(year: String): String {
    val url = "https://contrataciondelsectorpublico.gob.es/sindicacion/sindicacion_1143/contratosMenoresPerfilesContratantes_$year.zip"
    val zipFile = "./${year}/minor/${year}.zip"
    val unzipDir = "./documents/${year}/minor/"

    logger.info("Downloading $url to $zipFile")

    // download with okio
    downloadAndUnzipZipFile(url, File(unzipDir))

    // return unzipDir
    return unzipDir
}

fun downloadContractZip(month: String, year: String): String {
    // download zip from https://contrataciondelsectorpublico.gob.es/sindicacion/sindicacion_643/licitacionesPerfilesContratanteCompleto3_AAAAMM.zip
    // where AAAA is the year and MM is the month
    // then, unzip it in the AAAA_MM folder (create it if not exists)

    val url = "https://contrataciondelsectorpublico.gob.es/sindicacion/sindicacion_643/licitacionesPerfilesContratanteCompleto3_${year}${month}.zip"
    val zipFile = "./${year}/${month}/${year}${month}.zip"
    val unzipDir = "./documents/${year}/${month}/"

    logger.info("Downloading $url to $zipFile")

    // download with okio
    downloadAndUnzipZipFile(url, File(unzipDir))

    // return unzipDir
    return unzipDir
}

fun downloadAndUnzipZipFile(zipUrl: String, destinationDirectory: File) {
    try {
        // Conectar al servidor y descargar el archivo ZIP
        val url = URL(zipUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        val inputStream = connection.inputStream

        // Crear un directorio de destino si no existe
        if (!destinationDirectory.exists()) {
            logger.info("Creating directory $destinationDirectory")
            destinationDirectory.mkdirs()
        }

        // Descomprimir el archivo ZIP
        val zipInputStream = ZipInputStream(inputStream)
        var entry: ZipEntry?
        while (zipInputStream.nextEntry.also { entry = it } != null) {
            val entryName = entry!!.name
            val entryFile = File(destinationDirectory, entryName)

            // Si la entrada es un directorio, asegúrate de que exista
            if (entry!!.isDirectory) {
                entryFile.mkdirs()
            } else {
                // Si la entrada es un archivo, escribir su contenido en el archivo de destino
                val sink: BufferedSink = entryFile.sink().buffer()
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (zipInputStream.read(buffer).also { bytesRead = it } != -1) {
                    sink.write(buffer, 0, bytesRead)
                }
                sink.close()
            }
        }
        zipInputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}