// Select all buttons
const buttons = document.querySelectorAll('.search-button');
const searchBox = document.querySelector('.search-box');

const endpoint = 'http://127.0.0.1:5000';

// Add event listeners to each button
buttons.forEach(button => {
    button.addEventListener('click', () => {
        const searchText = searchBox.value;

        showProgress();
        
        switch(button.textContent) {
            case 'Buscar por CPV':
                searchByCPV(searchText);
                break;
            case 'Buscar por id': 
                searchById(searchText);
                break;
            case 'Buscar por Adjudicatario':
                searchByContractor(searchText);
                break;
            case 'Buscar por órgano de contratación':
                searchByContractingBody(searchText);
                break;
            case 'Buscar por código postal':
                searchByPostalCode(searchText);
                break;
            case 'Buscar por estado':
                searchByStatus(searchText);
                break;
        }
    });
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

// Search functions for each type
async function searchByCPV(text) {
    try {
        const response = await fetch(`${endpoint}/licitaciones/cpv/${text}`);
        const data = await response.json();
        displayResults(data);
    } catch (error) {
        console.error('Error searching by CPV:', error);
    }
}

async function searchById(text) {
    try {
        const response = await fetch(`${endpoint}/licitaciones/${text}`);
        const data = await response.json();
        displayResults(data);
    } catch (error) {
        console.error('Error searching by ID:', error);
    }
}

async function searchByContractor(text) {
    try {
        const response = await fetch(`${endpoint}/licitaciones/adjudicatario/${text}`);
        const data = await response.json();
        displayResults(data);
    } catch (error) {
        console.error('Error searching by contractor:', error);
    }
}

async function searchByContractingBody(text) {
    try {
        const response = await fetch(`${endpoint}/licitaciones/organo/${text}`);
        const data = await response.json();
        displayResults(data);
    } catch (error) {
        console.error('Error searching by contracting body:', error);
    }
}

async function searchByPostalCode(text) {
    try {
        const response = await fetch(`${endpoint}/licitaciones/codigo_postal/${text}`);
        const data = await response.json();
        displayResults(data);
    } catch (error) {
        console.error('Error searching by postal code:', error);
    }
}

async function searchByStatus(text) {
    try {
        const response = await fetch(`${endpoint}/licitaciones/estado/${text}`);
        const data = await response.json();
        displayResults(data);
    } catch (error) {
        console.error('Error searching by status:', error);
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

        const linkButton = document.createElement('a');
        linkButton.href = item.Link_licitacion;
        linkButton.target = '_blank';
        linkButton.className = 'link-button';
        linkButton.innerHTML = '<svg width="24" height="24" viewBox="0 0 24 24"><path d="M14,3V5H17.59L7.76,14.83L9.17,16.24L19,6.41V10H21V3M19,19H5V5H12V3H5C3.89,3 3,3.9 3,5V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V12H19V19Z"/></svg>';

        const title = document.createElement('h3');
        title.textContent = item.Objeto_licitacion_lote;

        const date = document.createElement('p');
        date.className = 'date';
        date.textContent = `Fecha: ${item.Fecha_actualizacion}`;

        const organ = document.createElement('p');
        organ.className = 'organ';
        organ.textContent = `Órgano: ${item.Organo_Contratacion}`;

        const amount = document.createElement('p');
        amount.className = 'amount';
        amount.textContent = `Importe: ${item.Importe_adjudicacion_sin_impuestos_lote.toLocaleString('es-ES', {style: 'currency', currency: 'EUR'})}`;

        const contractor = document.createElement('p');
        contractor.className = 'contractor';
        contractor.textContent = `Adjudicatario: ${item.Adjudicatario_lote}`;

        const contractorId = document.createElement('p');
        contractorId.className = 'contractor-id';
        contractorId.textContent = `NIF Adjudicatario: ${item.Identificador_Adjudicatario_lote}`;

        card.appendChild(linkButton);
        card.appendChild(title);
        card.appendChild(date);
        card.appendChild(organ);
        card.appendChild(amount);
        card.appendChild(contractor);
        card.appendChild(contractorId);

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
