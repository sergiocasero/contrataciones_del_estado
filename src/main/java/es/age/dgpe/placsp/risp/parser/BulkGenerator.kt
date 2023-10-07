package es.age.dgpe.placsp.risp.parser

import es.age.dgpe.placsp.risp.parser.view.ParserController
import okio.BufferedSink
import okio.buffer
import okio.sink
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


private val logger = LogManager.getLogger(ParserController::class.java.name)
fun main() {
    bulkDownload()
}

fun bulkProcessing() {
    val parser = ParserController()

    parser.textFieldDirOrigen = "./2021_01/licitacionesPerfilesContratanteCompleto3.atom"
    parser.textFieldOutputFile = "./2021_01/8.xlsx"

    val result = parser.generarXLSX()

    // loop to download all zips, the first month is 01/2012, latest is 10/2023


}

fun bulkDownload() {
    for (year in 2022..2023) {
        for (month in 1..12) {
            val monthStr = if (month < 10) "0$month" else "$month"
            downloadZip(year.toString(), monthStr)
        }
    }
}

fun downloadZip(year: String, month: String) {
    // download zip from https://contrataciondelsectorpublico.gob.es/sindicacion/sindicacion_643/licitacionesPerfilesContratanteCompleto3_AAAAMM.zip
    // where AAAA is the year and MM is the month
    // then, unzip it in the AAAA_MM folder (create it if not exists)

    val url = "https://contrataciondelsectorpublico.gob.es/sindicacion/sindicacion_643/licitacionesPerfilesContratanteCompleto3_${year}${month}.zip"
    val zipFile = "./${year}/${month}/${year}${month}.zip"
    val unzipDir = "./documents/${year}/${month}/"

    logger.info("Downloading $url to $zipFile")

    // download with okio
    downloadAndUnzipZipFile(url, File(unzipDir))
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