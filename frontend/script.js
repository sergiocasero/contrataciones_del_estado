// Select all buttons
const searchBox = document.querySelector('.search-box');

const endpoint = 'http://127.0.0.1:5000';

// Add event listeners to each button
searchBox.addEventListener('keypress', (event) => {
    if (event.key === 'Enter') {
        event.preventDefault();
        const searchText = searchBox.value;

        clearSearchBox();
        showProgress();
        
        searchLicitaciones(searchText);
    }
});

function showProgress() {
    const progress = document.getElementById('progress');
    progress.style.display = 'block';
}

function hideProgress() {
    const progress = document.getElementById('progress');
    progress.style.display = 'none';
}

function clearSearchBox() {
    const searchBox = document.querySelector('.search-box');
    searchBox.value = '';
    hideProgress();
    document.getElementById('summary').innerHTML = '';
    document.getElementById('results-container').innerHTML = '';
}

// Nueva función de búsqueda definida en app.py
async function searchLicitaciones(searchTerm) {
    try {
        const response = await fetch(`${endpoint}/licitaciones/${searchTerm}`);
        const data = await response.json();
        displayResults(data);
    } catch (error) {
        console.error('Error searching licitaciones:', error);
    }
}

// Helper function to display results
function displayResults(data) {
    const resultsDiv = document.getElementById('results-container');
    resultsDiv.innerHTML = '';

    // Handle both array and object responses
    const items = data.datos || data.data || data;
    
    if (!Array.isArray(items) || items.length === 0) {
        resultsDiv.innerHTML = '<p>No se han encontrado resultados</p>';
        return;
    }

    let totalAmount = 0;
    const contractorsSet = new Set();

    items.forEach(item => {
        totalAmount += item.Importe_adjudicacion_sin_impuestos_lote;
        contractorsSet.add(item.Adjudicatario_lote);

        const card = document.createElement('div');
        card.className = 'contract-card';

        const expediente = document.createElement('p');
        expediente.className = 'expediente';
        expediente.textContent = `Expediente: ${item.Numero_expediente}`;
        card.appendChild(expediente);

        const linkButton = document.createElement('a');
        linkButton.href = item.Link_licitacion;
        linkButton.target = '_blank';
        linkButton.className = 'link-button';
        linkButton.innerHTML = '<svg width="24" height="24" viewBox="0 0 24 24"><path d="M14,3V5H17.59L7.76,14.83L9.17,16.24L19,6.41V10H21V3M19,19H5V5H12V3H5C3.89,3 3,3.9 3,5V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V12H19V19Z"/></svg>';
        card.appendChild(linkButton);

        const title = document.createElement('h3');
        title.textContent = item.Objeto_contrato;
        card.appendChild(title);

        if (item.Objeto_licitacion_lote && item.Objeto_licitacion_lote !== item.Objeto_contrato) {
            const subtitle = document.createElement('h4');
            subtitle.textContent = item.Objeto_licitacion_lote;
            card.appendChild(subtitle);
        }

        const date = document.createElement('p');
        date.className = 'date';
        date.textContent = `Fecha: ${item.Fecha_actualizacion}`;
        card.appendChild(date);

        const organ = document.createElement('p');
        organ.className = 'organ';
        organ.textContent = `Órgano: ${item.Organo_Contratacion}`;
        card.appendChild(organ);

        const nifOC = document.createElement('p');
        nifOC.className = 'nif-oc';
        nifOC.textContent = `NIF Órgano: ${item.NIF_OC}`;
        card.appendChild(nifOC);

        const baseAmount = document.createElement('p');
        baseAmount.className = 'amount';
        baseAmount.textContent = `Presupuesto base sin impuestos: ${item.Presupuesto_base_sin_impuestos ? item.Presupuesto_base_sin_impuestos.toLocaleString('es-ES', {style: 'currency', currency: 'EUR'}) : '-€'}`;
        card.appendChild(baseAmount);

        if (item.Valor_estimado_contrato !== item.Presupuesto_base_sin_impuestos) {
            const estimatedValue = document.createElement('p');
            estimatedValue.className = 'amount';
            estimatedValue.textContent = `Valor estimado: ${item.Valor_estimado_contrato ? item.Valor_estimado_contrato.toLocaleString('es-ES', {style: 'currency', currency: 'EUR'}) : '-€'}`;
            card.appendChild(estimatedValue);
        }

        if (item.Importe_adjudicacion_sin_impuestos_lote) {
            const amount = document.createElement('p');
            amount.className = 'amount';
            amount.textContent = `Importe: ${item.Importe_adjudicacion_sin_impuestos_lote.toLocaleString('es-ES', {style: 'currency', currency: 'EUR'})}`;
            card.appendChild(amount);
        }


        if (item.Adjudicatario_lote) {
            const contractor = document.createElement('p');
            contractor.className = 'contractor';
            contractor.textContent = `Adjudicatario: ${item.Adjudicatario_lote}`;
            card.appendChild(contractor);
        }

        if (item.Identificador_Adjudicatario_lote) {
            const contractorId = document.createElement('p');
            contractorId.className = 'contractor-id';
            contractorId.textContent = `NIF Adjudicatario: ${item.Identificador_Adjudicatario_lote}`;
            card.appendChild(contractorId);
        }


        resultsDiv.appendChild(card);
    });

    const summaryDiv = document.querySelector('#summary');
    summaryDiv.innerHTML = '';
    const summary = document.createElement('p');
    summary.textContent = `${contractorsSet.size} adjudicatarios, por un importe de ${totalAmount.toLocaleString('es-ES', {style: 'currency', currency: 'EUR'})}`;
    summaryDiv.appendChild(summary);

    hideProgress();
}

async function getOverallData() {
    try {
        document.querySelector('.overall-stats').style.display = 'none';
        const response = await fetch('/licitaciones/overall');
        const data = await response.json();

        document.getElementById('total-contracts').textContent = data.total_registros;
        document.getElementById('total-contractors').textContent = data.adjudicatarios;
        document.getElementById('total-amount').textContent = data.total_ganado.toLocaleString('es-ES', {style: 'currency', currency: 'EUR'});
        document.querySelector('.overall-stats').style.display = 'block';
    } catch (error) {
        console.error('Error al obtener los datos generales:', error);
    }
}

document.addEventListener('DOMContentLoaded', getOverallData);
