import os
import mysql.connector
import pandas as pd
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# Create database connection and cursor
def crear_tabla_licitaciones():
    conn = mysql.connector.connect(
        host=os.getenv('database_url'),
        user=os.getenv('database_user'),
        password=os.getenv('database_password'),
        database=os.getenv('database_name'),
        charset='utf8mb4',
        collation='utf8mb4_unicode_ci'
    )
    cursor = conn.cursor()

    # Create licitaciones table
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS licitaciones (
            Identificador VARCHAR(255) COLLATE utf8mb4_unicode_ci PRIMARY KEY,
            Link_licitacion TEXT COLLATE utf8mb4_unicode_ci,
            Fecha_actualizacion VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Vigente_Anulada_Archivada VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Primera_publicacion VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Estado VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Numero_expediente VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Objeto_contrato TEXT COLLATE utf8mb4_unicode_ci,
            Valor_estimado_contrato DECIMAL(15,2),
            Presupuesto_base_sin_impuestos DECIMAL(15,2),
            Presupuesto_base_con_impuestos DECIMAL(15,2),
            CPV TEXT COLLATE utf8mb4_unicode_ci,
            Tipo_contrato VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Lugar_ejecucion VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Organo_Contratacion TEXT COLLATE utf8mb4_unicode_ci,
            ID_OC_en_PLACSP VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            NIF_OC VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            DIR3 VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Enlace_perfil_contratante_OC TEXT COLLATE utf8mb4_unicode_ci,
            Tipo_administracion VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Codigo_postal VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Tipo_procedimiento VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Sistema_contratacion VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Tramitacion VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Forma_presentacion_oferta VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Fecha_presentacion_ofertas VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Fecha_presentacion_solicitudes VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Directiva_aplicacion VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Financiacion_Europea_y_fuente VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Descripcion_financiacion_europea TEXT COLLATE utf8mb4_unicode_ci,
            Subcontratacion_permitida VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Subcontratacion_permitida_porcentaje DECIMAL(5,2),
            Numero_expediente_lote VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Objeto_licitacion_lote TEXT COLLATE utf8mb4_unicode_ci,
            Presupuesto_base_con_impuestos_lote DECIMAL(15,2),
            Presupuesto_base_sin_impuestos_lote DECIMAL(15,2),
            CPV_licitacion_lote TEXT COLLATE utf8mb4_unicode_ci,
            Lugar_ejecucion_lote VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Resultado_licitacion_lote VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Fecha_acuerdo_lote VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Numero_ofertas_recibidas_lote INT,
            Precio_oferta_mas_baja_lote DECIMAL(15,2),
            Precio_oferta_mas_alta_lote DECIMAL(15,2),
            Ofertas_excluidas_anormalmente_bajas VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Numero_contrato_lote VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Fecha_formalizacion_contrato_lote VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Fecha_entrada_en_vigor_contrato_lote VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Adjudicatario_lote TEXT COLLATE utf8mb4_unicode_ci,
            Tipo_identificador_adjudicatario_lote VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Identificador_Adjudicatario_lote VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Adjudicatario_es_o_no_PYME VARCHAR(255) COLLATE utf8mb4_unicode_ci,
            Importe_adjudicacion_sin_impuestos_lote DECIMAL(15,2),
            Importe_adjudicacion_con_impuestos_lote DECIMAL(15,2)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    ''')

    conn.commit()
    conn.close()

# Function to bulk insert from .xlsx files
def insertar_datos_desde_xlsx(carpeta):

    # Walk through folder and subfolders
    for subdir, dirs, files in os.walk(carpeta):
        for file in files:
            if file.endswith('.xlsx'):
                conn = mysql.connector.connect(
                    host=os.getenv('database_url'),
                    user=os.getenv('database_user'),
                    password=os.getenv('database_password'),
                    database=os.getenv('database_name'),
                    charset='utf8mb4',
                    collation='utf8mb4_unicode_ci'
                )
                cursor = conn.cursor()
                file_path = os.path.join(subdir, file)
                print(f"Procesando archivo: {file_path}")

                # Read Excel file
                df = pd.read_excel(file_path, sheet_name='Licitaciones')

                # Rename columns to match MySQL table
                df.columns = [
                    'Identificador',
                    'Link_licitacion',
                    'Fecha_actualizacion',
                    'Vigente_Anulada_Archivada',
                    'Primera_publicacion',
                    'Estado', 
                    'Numero_expediente',
                    'Objeto_contrato',
                    'Valor_estimado_contrato',
                    'Presupuesto_base_sin_impuestos',
                    'Presupuesto_base_con_impuestos',
                    'CPV',
                    'Tipo_contrato',
                    'Lugar_ejecucion',
                    'Organo_Contratacion',
                    'ID_OC_en_PLACSP',
                    'NIF_OC',
                    'DIR3',
                    'Enlace_perfil_contratante_OC',
                    'Tipo_administracion',
                    'Codigo_postal',
                    'Tipo_procedimiento',
                    'Sistema_contratacion',
                    'Tramitacion',
                    'Forma_presentacion_oferta',
                    'Fecha_presentacion_ofertas',
                    'Fecha_presentacion_solicitudes',
                    'Directiva_aplicacion',
                    'Financiacion_Europea_y_fuente',
                    'Descripcion_financiacion_europea',
                    'Subcontratacion_permitida',
                    'Subcontratacion_permitida_porcentaje',
                    'Numero_expediente',
                    'Numero_expediente_lote',
                    'Objeto_licitacion_lote',
                    'Presupuesto_base_con_impuestos_lote',
                    'Presupuesto_base_sin_impuestos_lote',
                    'CPV_licitacion_lote',
                    'Lugar_ejecucion_lote',
                    'Resultado_licitacion_lote',
                    'Fecha_acuerdo_lote',
                    'Numero_ofertas_recibidas_lote',
                    'Precio_oferta_mas_baja_lote',
                    'Precio_oferta_mas_alta_lote',
                    'Ofertas_excluidas_anormalmente_bajas',
                    'Numero_contrato_lote',
                    'Fecha_formalizacion_contrato_lote',
                    'Fecha_entrada_en_vigor_contrato_lote',
                    'Adjudicatario_lote',
                    'Tipo_identificador_adjudicatario_lote',
                    'Identificador_Adjudicatario_lote',
                    'Adjudicatario_es_o_no_PYME',
                    'Importe_adjudicacion_sin_impuestos_lote',
                    'Importe_adjudicacion_con_impuestos_lote'
                ]

                # Replace NaN values with None before inserting
                df = df.replace({pd.NA: None, float('nan'): None})

                # Remove duplicate Numero_expediente column
                df = df.loc[:,~df.columns.duplicated()]

                # Trim Organo_Contratacion to maximum TEXT length (65,535 characters)
                df['Organo_Contratacion'] = df['Organo_Contratacion'].str.slice(0, 65534)

                # Insert data row by row into database
                for _, row in df.iterrows():
                    placeholders = ', '.join(['%s'] * len(df.columns))
                    columns = ', '.join(df.columns)
                    sql = f"INSERT IGNORE INTO licitaciones ({columns}) VALUES ({placeholders})"
                    cursor.execute(sql, tuple(row))

                conn.commit()
                conn.close()


crear_tabla_licitaciones()
insertar_datos_desde_xlsx('../downloader/output')
