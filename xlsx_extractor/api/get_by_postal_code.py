@app.route('/licitaciones/codigo_postal/<codigo_postal>', methods=['GET'])
def get_licitacion_by_codigo_postal(codigo_postal):
    conn = get_db_connection()
    query = "SELECT * FROM licitaciones WHERE Codigo_postal LIKE ?"
    licitaciones = conn.execute(query, ('%' + codigo_postal + '%',)).fetchall()
    conn.close()
    return jsonify([dict(row) for row in licitaciones])
