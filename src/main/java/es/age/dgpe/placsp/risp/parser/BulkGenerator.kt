package es.age.dgpe.placsp.risp.parser

import es.age.dgpe.placsp.risp.parser.view.ParserController


fun main() {
    val parser = ParserController()

    parser.textFieldDirOrigen.text = "./2021_01/licitacionesPerfilesContratanteCompleto3.atom"
    parser.textFieldOutputFile.text = "./2021_01/8.xlsx"

    parser.generarXLSX()
}