import os
import glob
from flask import Flask, jsonify, request, send_from_directory
from flask_cors import CORS
import mysql.connector
from dotenv import load_dotenv

app = Flask(__name__)
CORS(app)

# Load environment variables
load_dotenv()

# Function to get database connection
def get_db_connection():
    conn = mysql.connector.connect(
        host=os.getenv('database_url'),
        user=os.getenv('database_user'),
        password=os.getenv('database_password'),
        database=os.getenv('database_name'),
        charset='utf8mb4',
        collation='utf8mb4_unicode_ci'
    )
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
    cursor = conn.cursor(dictionary=True)

    query = "SELECT COUNT(*) AS total_licitaciones, SUM(Importe_adjudicacion_sin_impuestos_lote) AS total_ganado FROM licitaciones"
    cursor.execute(query)
    result = cursor.fetchone()
    
    cursor.execute("SELECT COUNT(DISTINCT Identificador_adjudicatario_lote) AS unique_adjudicatarios FROM licitaciones")
    unique_adjudicatarios = cursor.fetchone()['unique_adjudicatarios']

    total_ganado = result['total_ganado']
    total_records = result['total_licitaciones']

    cursor.close()
    conn.close()

    return jsonify({
        'total_ganado': total_ganado,
        'total_registros': total_records,
        'adjudicatarios': unique_adjudicatarios
    })

@app.route('/licitaciones', methods=['GET'])
def get_all_licitaciones():
    conn = get_db_connection()
    cursor = conn.cursor(dictionary=True)
    page = request.args.get('page', default=1, type=int)
    page_size = request.args.get('page_size', default=100, type=int)

    offset = (page - 1) * page_size
    cursor.execute(
        'SELECT * FROM licitaciones LIMIT %s OFFSET %s', (page_size, offset)
    )
    licitaciones = cursor.fetchall()

    cursor.execute('SELECT COUNT(*) FROM licitaciones')
    total_rows = cursor.fetchone()['COUNT(*)']
    
    cursor.close()
    conn.close()

    return jsonify({
        'total_rows': total_rows,
        'page': page,
        'page_size': page_size,
        'data': licitaciones
    })

@app.route('/licitaciones/<search_term>', methods=['GET'])
def search_licitaciones(search_term):
    conn = get_db_connection()
    cursor = conn.cursor(dictionary=True)
    query = """
        SELECT * FROM licitaciones 
        WHERE CPV LIKE %s 
        OR Identificador LIKE %s 
        OR Adjudicatario_lote LIKE %s 
        OR Identificador_Adjudicatario_lote LIKE %s 
        OR Organo_Contratacion LIKE %s 
        OR Codigo_postal LIKE %s 
        OR Estado LIKE %s
        OR NIF_OC LIKE %s
    """
    search_term = '%' + search_term + '%'
    cursor.execute(query, (search_term,) * 8)
    licitaciones = cursor.fetchall()
    
    cursor.close()
    conn.close()

    # Eliminar duplicados
    unique_licitaciones = {row['Identificador']: dict(row) for row in licitaciones}.values()

    # Ordenar por fecha
    unique_licitaciones = sorted(unique_licitaciones, key=lambda x: x['Fecha_actualizacion'], reverse=True)

    return jsonify(list(unique_licitaciones))

if __name__ == '__main__':
    app.run(debug=True)