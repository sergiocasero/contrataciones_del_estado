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

@app.route('/licitaciones/cpv/<cpv>', methods=['GET'])
def get_licitacion_by_cpv(cpv):
    conn = get_db_connection()
    query = "SELECT * FROM licitaciones WHERE CPV LIKE ?"
    licitaciones = conn.execute(query, ('%' + cpv + '%',)).fetchall()
    conn.close()
    return jsonify([dict(row) for row in licitaciones])

@app.route('/licitaciones/<identificador>', methods=['GET'])
def get_licitacion_by_id(identificador):
    conn = get_db_connection()
    query = "SELECT * FROM licitaciones WHERE Identificador LIKE ?"
    licitacion = conn.execute(query, ('%' + identificador + '%',)).fetchall()
    conn.close()
    if not licitacion:
        return jsonify({'error': 'Licitación no encontrada'}), 404
    
    total_earn = sum(row['Importe_adjudicacion_sin_impuestos_lote'] for row in licitacion)

    return jsonify({
        'data': [dict(row) for row in licitacion],
        'total_rows': len(licitacion),
        'total_earn': total_earn
    })

@app.route('/licitaciones/adjudicatario/<identificador>', methods=['GET'])
def get_licitacion_by_adjudicatario(identificador):
    conn = get_db_connection()
    query = "SELECT * FROM licitaciones WHERE Adjudicatario_lote LIKE ? OR Identificador_Adjudicatario_lote LIKE ?"
    licitaciones = conn.execute(query, ('%' + identificador + '%', '%' + identificador + '%')).fetchall()
    conn.close()


    total_earn = sum(row['Importe_adjudicacion_sin_impuestos_lote'] for row in licitaciones)

    return jsonify({
        'total_registros': len(licitaciones),
        'total_ganado': total_earn,
        'datos': [dict(row) for row in licitaciones]
    })
    

@app.route('/licitaciones/organo/<organo>', methods=['GET'])
def get_licitacion_by_organo_contratacion(organo):
    conn = get_db_connection()
    query = "SELECT * FROM licitaciones WHERE Organo_Contratacion LIKE ?"
    licitaciones = conn.execute(query, ('%' + organo + '%',)).fetchall()
    conn.close()
    return jsonify([dict(row) for row in licitaciones])

@app.route('/licitaciones/codigo_postal/<codigo_postal>', methods=['GET'])
def get_licitacion_by_codigo_postal(codigo_postal):
    conn = get_db_connection()
    query = "SELECT * FROM licitaciones WHERE Codigo_postal LIKE ?"
    licitaciones = conn.execute(query, ('%' + codigo_postal + '%',)).fetchall()
    conn.close()
    return jsonify([dict(row) for row in licitaciones])

@app.route('/licitaciones/estado/<estado>', methods=['GET'])
def get_licitacion_by_estado(estado):
    conn = get_db_connection()
    query = "SELECT * FROM licitaciones WHERE Estado LIKE ?"
    licitaciones = conn.execute(query, ('%' + estado + '%',)).fetchall()
    conn.close()
    return jsonify([dict(row) for row in licitaciones])


if __name__ == '__main__':
    app.run(debug=True)