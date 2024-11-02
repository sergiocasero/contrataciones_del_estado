import os
import glob
from flask import Flask, jsonify, request, send_from_directory
from flask_cors import CORS
import sqlite3

app = Flask(__name__)
CORS(app)

DATABASE = 'licitaciones.db'

# Función para obtener conexión a la base de datos
def get_db_connection():
    conn = sqlite3.connect(DATABASE)
    conn.row_factory = sqlite3.Row  # Esto permite acceder a las columnas por nombre
    return conn

@app.route('/')
def index():
    return send_from_directory('../frontend', 'index.html')

@app.route('/styles.css')
def styles():
    return send_from_directory('../frontend', 'styles.css')

@app.route('/script.js')
def script():
    return send_from_directory('../frontend', 'script.js')

@app.route('/licitaciones/overall', methods=['GET'])
def get_licitaciones_overall():
    conn = get_db_connection()

    query = "SELECT COUNT(*) AS total_licitaciones, SUM(Importe_adjudicacion_sin_impuestos_lote) AS total_ganado FROM licitaciones"
    result = conn.execute(query).fetchone()
    unique_adjudicatarios = conn.execute("SELECT COUNT(DISTINCT Identificador_adjudicatario_lote) AS unique_adjudicatarios FROM licitaciones").fetchone()['unique_adjudicatarios']

    total_ganado = result['total_ganado']
    total_records = result['total_licitaciones']

    conn.close()

    return jsonify({
        'total_ganado': total_ganado,
        'total_registros': total_records,
        'adjudicatarios': unique_adjudicatarios
    })

@app.route('/licitaciones', methods=['GET'])
def get_all_licitaciones():
    conn = get_db_connection()
    page = request.args.get('page', default=1, type=int)
    page_size = request.args.get('page_size', default=100, type=int)

    offset = (page - 1) * page_size
    licitaciones = conn.execute(
        'SELECT * FROM licitaciones LIMIT ? OFFSET ?', (page_size, offset)
    ).fetchall()

    total_rows = conn.execute('SELECT COUNT(*) FROM licitaciones').fetchone()[0]
    conn.close()

    return jsonify({
        'total_rows': total_rows,
        'page': page,
        'page_size': page_size,
        'data': [dict(row) for row in licitaciones]
    })
@app.route('/licitaciones/<search_term>', methods=['GET'])
def search_licitaciones(search_term):
    conn = get_db_connection()
    query = """
        SELECT * FROM licitaciones 
        WHERE CPV LIKE ? 
        OR Identificador LIKE ? 
        OR Adjudicatario_lote LIKE ? 
        OR Identificador_Adjudicatario_lote LIKE ? 
        OR Organo_Contratacion LIKE ? 
        OR Codigo_postal LIKE ? 
        OR Estado LIKE ?
        OR NIF_OC LIKE ?
    """
    search_term = '%' + search_term + '%'
    licitaciones = conn.execute(query, (search_term, search_term, search_term, search_term, search_term, search_term, search_term, search_term)).fetchall()
    conn.close()

    # Eliminar duplicados
    unique_licitaciones = {row['Identificador']: dict(row) for row in licitaciones}.values()

    # Ordenar por fecha
    unique_licitaciones = sorted(unique_licitaciones, key=lambda x: x['Fecha_actualizacion'], reverse=True)

    return jsonify(list(unique_licitaciones))

if __name__ == '__main__':
    app.run(debug=True)