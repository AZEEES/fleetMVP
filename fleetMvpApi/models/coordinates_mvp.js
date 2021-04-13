const mongoose = require('mongoose');

const CoordinatesSchema = mongoose.Schema({
    latitude : {
        type : String, 
        required : false
    },
    longitude : {
        type : String, 
        required : false
    },
    city_name : {
        type : String, 
        required : false
    },
    location_type : {
        type : String,
        required : true
    }

});

const Coordinates = module.exports = mongoose.model('Coordinates_Mvp', CoordinatesSchema);