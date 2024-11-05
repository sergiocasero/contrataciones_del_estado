import os
import sqlite3
import pandas as pd

# Crear la conexión a la base de datos SQLite y el cursor
def crear_tabla_licitaciones(db_name="licitaciones.db"):
    conn = sqlite3.connect(db_name)
    cursor = conn.cursor()

    # Habilitar modo WAL para mejor rendimiento en escrituras concurrentes
    cursor.execute('PRAGMA journal_mode=WAL')
    
    # Aumentar el tamaño del cache
    cursor.execute('PRAGMA cache_size=-2000000') # Aproximadamente 2GB de cache
    
    # Otras optimizaciones
    cursor.execute('PRAGMA synchronous=NORMAL')
    cursor.execute('PRAGMA temp_store=MEMORY')
    cursor.execute('PRAGMA mmap_size=30000000000')

    # Crear la tabla licitaciones
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS licitaciones (
            Identificador TEXT,
            Link_licitacion TEXT,
            Fecha_actualizacion TEXT,
            Vigente_Anulada_Archivada TEXT,
            Primera_publicacion TEXT,
            Estado TEXT,
            Numero_expediente TEXT,
            Objeto_contrato TEXT,
            Valor_estimado_contrato REAL,
            Presupuesto_base_sin_impuestos REAL,
            Presupuesto_base_con_impuestos REAL,
            CPV TEXT,
            Tipo_contrato TEXT,
            Lugar_ejecucion TEXT,
            Organo_Contratacion TEXT,
            ID_OC_en_PLACSP TEXT,
            NIF_OC TEXT,
            DIR3 TEXT,
            Enlace_perfil_contratante_OC TEXT,
            Tipo_administracion TEXT,
            Codigo_postal TEXT,
            Tipo_procedimiento TEXT,
            Sistema_contratacion TEXT,
            Tramitacion TEXT,
            Forma_presentacion_oferta TEXT,
            Fecha_presentacion_ofertas TEXT,
            Fecha_presentacion_solicitudes TEXT,
            Directiva_aplicacion TEXT,
            Financiacion_Europea_y_fuente TEXT,
            Descripcion_financiacion_europea TEXT,
            Subcontratacion_permitida TEXT,
            Subcontratacion_permitida_porcentaje REAL,
            Numero_expediente_lote TEXT,
            Objeto_licitacion_lote TEXT,
            Presupuesto_base_con_impuestos_lote REAL,
            Presupuesto_base_sin_impuestos_lote REAL,
            CPV_licitacion_lote TEXT,
            Lugar_ejecucion_lote TEXT,
            Resultado_licitacion_lote TEXT,
            Fecha_acuerdo_lote TEXT,
            Numero_ofertas_recibidas_lote INTEGER,
            Precio_oferta_mas_baja_lote REAL,
            Precio_oferta_mas_alta_lote REAL,
            Ofertas_excluidas_anormalmente_bajas TEXT,
            Numero_contrato_lote TEXT,
            Fecha_formalizacion_contrato_lote TEXT,
            Fecha_entrada_en_vigor_contrato_lote TEXT,
            Adjudicatario_lote TEXT,
            Tipo_identificador_adjudicatario_lote TEXT,
            Identificador_Adjudicatario_lote TEXT,
            Adjudicatario_es_o_no_PYME TEXT,
            Importe_adjudicacion_sin_impuestos_lote REAL,
            Importe_adjudicacion_con_impuestos_lote REAL
        )
    ''')

    # Crear índices para mejorar el rendimiento de búsquedas
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_identificador ON licitaciones(Identificador)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_cpv ON licitaciones(CPV)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_adjudicatario ON licitaciones(Adjudicatario_lote)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_id_adjudicatario ON licitaciones(Identificador_Adjudicatario_lote)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_organo ON licitaciones(Organo_Contratacion)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_fecha ON licitaciones(Fecha_actualizacion)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_nif ON licitaciones(NIF_OC)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_cp ON licitaciones(Codigo_postal)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_estado ON licitaciones(Estado)')

    conn.commit()
    conn.close()

def optimizar_db(db_name="licitaciones.db"):
    """Función para optimizar la base de datos después de inserciones masivas"""
    conn = sqlite3.connect(db_name)
    cursor = conn.cursor()
    cursor.execute('VACUUM')
    cursor.execute('ANALYZE')
    conn.commit()
    conn.close()

# Función para insertar masivamente desde los ficheros .xlsx
def insertar_datos_desde_xlsx(carpeta, db_name="licitaciones.db", batch_size=1000):
    conn = sqlite3.connect(db_name)
    
    # Deshabilitar temporalmente algunas restricciones para mejorar rendimiento
    conn.execute('PRAGMA synchronous = OFF')
    conn.execute('PRAGMA journal_mode = MEMORY')
    conn.execute('PRAGMA cache_size = -2000000')
    
    # Iniciar transacción
    conn.execute('BEGIN TRANSACTION')
    
    try:
        # Recorrer la carpeta y subcarpetas
        for subdir, dirs, files in os.walk(carpeta):
            for file in files:
                if file.endswith('.xlsx'):
                    file_path = os.path.join(subdir, file)
                    print(f"Procesando archivo: {file_path}")

                    # Leer el archivo Excel en chunks para manejar archivos grandes
                    df = pd.read_excel(file_path, sheet_name='Licitaciones')
                    for i in range(0, len(df), batch_size):
                        chunk = df.iloc[i:i + batch_size]
                        # Renombrar columnas para que coincidan con la tabla de SQLite
                        chunk.columns = [
                            'Identificador', 'Link_licitacion', 'Fecha_actualizacion',
                            'Vigente_Anulada_Archivada', 'Primera_publicacion', 'Estado',
                            'Numero_expediente', 'Objeto_contrato', 'Valor_estimado_contrato',
                            'Presupuesto_base_sin_impuestos', 'Presupuesto_base_con_impuestos',
                            'CPV', 'Tipo_contrato', 'Lugar_ejecucion', 'Organo_Contratacion',
                            'ID_OC_en_PLACSP', 'NIF_OC', 'DIR3', 'Enlace_perfil_contratante_OC',
                            'Tipo_administracion', 'Codigo_postal', 'Tipo_procedimiento',
                            'Sistema_contratacion', 'Tramitacion', 'Forma_presentacion_oferta',
                            'Fecha_presentacion_ofertas', 'Fecha_presentacion_solicitudes',
                            'Directiva_aplicacion', 'Financiacion_Europea_y_fuente',
                            'Descripcion_financiacion_europea', 'Subcontratacion_permitida',
                            'Subcontratacion_permitida_porcentaje', 'Numero_expediente',
                            'Numero_expediente_lote', 'Objeto_licitacion_lote',
                            'Presupuesto_base_con_impuestos_lote',
                            'Presupuesto_base_sin_impuestos_lote', 'CPV_licitacion_lote',
                            'Lugar_ejecucion_lote', 'Resultado_licitacion_lote',
                            'Fecha_acuerdo_lote', 'Numero_ofertas_recibidas_lote',
                            'Precio_oferta_mas_baja_lote', 'Precio_oferta_mas_alta_lote',
                            'Ofertas_excluidas_anormalmente_bajas', 'Numero_contrato_lote',
                            'Fecha_formalizacion_contrato_lote',
                            'Fecha_entrada_en_vigor_contrato_lote', 'Adjudicatario_lote',
                            'Tipo_identificador_adjudicatario_lote',
                            'Identificador_Adjudicatario_lote', 'Adjudicatario_es_o_no_PYME',
                            'Importe_adjudicacion_sin_impuestos_lote',
                            'Importe_adjudicacion_con_impuestos_lote'
                        ]

                        chunk.to_sql('licitaciones', conn, if_exists='append', index=False, method='multi')
                        
        conn.commit()
        
    except Exception as e:
        conn.rollback()
        raise e
        
    finally:
        conn.close()
        
    # Optimizar la base de datos después de todas las inserciones
    optimizar_db(db_name)

crear_tabla_licitaciones()
insertar_datos_desde_xlsx('../downloader/output')
