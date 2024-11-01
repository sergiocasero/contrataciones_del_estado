import React, { useEffect, useState } from 'react';

const Overall: React.FC = () => {
    const [data, setData] = useState<{ adjudicatarios: number; total_ganado: number; total_registros: number } | null>(null);

    useEffect(() => {
        fetch('http://127.0.0.1:5000/licitaciones/overall')
            .then(response => response.json())
            .then(data => setData(data))
            .catch(error => console.error('Error fetching data:', error));
    }, []);

    if (!data) {
        return <div>Loading...</div>;
    }

    return (
        <div className="max-w-sm mx-auto bg-white rounded-xl shadow-md overflow-hidden md:max-w-2xl">
            <div className="md:flex">
                <div className="p-8">
                    <p className="mt-2 text-gray-500">Adjudicatarios: {data.adjudicatarios}</p>
                    <p className="mt-2 text-gray-500">Total Ganado: ${data.total_ganado.toLocaleString()}</p>
                    <p className="mt-2 text-gray-500">Total Registros: {data.total_registros.toLocaleString()}</p>
                </div>
            </div>
        </div>
    );
};

export default Overall;