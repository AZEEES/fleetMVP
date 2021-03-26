const mongoose = require('mongoose');

const LocationSchema = mongoose.Schema({
    driver_id : {
        type : String,
        required : true
    },
    latitude : {
        type : String, 
        required : true
    },
    longitude : {
        type : String, 
        required : true
    },
    timestamp :{
        type : String,
        required : true
    }

});

const Location = module.exports = mongoose.model('Location_Mvp', LocationSchema);