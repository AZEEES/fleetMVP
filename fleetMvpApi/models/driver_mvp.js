const mongoose = require('mongoose');

const DriverSchema = mongoose.Schema({
    name : {
        type : String,
        required : true
    },
    contact : {
        type : String, 
        required : true,
        unique : true
    },
    owner_contact : {
        type : String, 
        required : true
    },
    access : {
        type : String,
        required : true
    }

});

const Driver = module.exports = mongoose.model('Driver_Mvp', DriverSchema);