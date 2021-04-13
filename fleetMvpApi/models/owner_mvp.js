const mongoose = require('mongoose');

const OwnerSchema = mongoose.Schema({
    name : {
        type : String,
        required : true
    },
    contact : {
        type : String, 
        required : true,
        unique : true
    },
    business_name : {
        type : String, 
        required : false
    },
    address1 : {
        type : String, 
        required : false
    },
    address2 : {
        type : String, 
        required : false
    },
    address3 : {
        type : String, 
        required : false
    },
    password : {
        type : String, 
        required : false
    },
    max_client : {
        type : String,
        required : false
    }

});

const Owner = module.exports = mongoose.model('Owner_Mvp', OwnerSchema);