// mysql methods

const { Sequelize, DataTypes } = require('sequelize');
const Tuple = require('./models');

const sequelize = new Sequelize('licitaciones', 'licitaciones', 'licitaciones', {
  host: 'localhost', // Cambia esto al host de tu base de datos MySQL si es diferente
  dialect: 'mysql',
  dialectModule: require('mysql2'),
});

const getTypes = async () => {
    try {
        const types = await Tuple.findAll({
            attributes: ['TipoContrato'],
            group: ['TipoContrato'],
        });
        return types;
    } catch (error) {
        throw error;
    }
}

const getTuplesByTipoContrato = async (tipoContrato) => {
    try {
        const tuples = await Tuple.findAll({
            where: { TipoContrato: tipoContrato },
        });
        return tuples;
    } catch (error) {
        throw error;
    }
}

const getStatuses = async () => {
    try {
        const statuses = await Tuple.findAll({
            attributes: ['Estado'],
            group: ['Estado'],
        });
        return statuses;
    } catch (error) {
        throw error;
    }
}

const getTuplesByEstado = async (estado) => {
    try {
        const tuples = await Tuple.findAll({
            where: { Estado: estado },
        });
        return tuples;
    } catch (error) {
        throw error;
    }
}

module.exports = {
    getTypes,
    getTuplesByTipoContrato,
}