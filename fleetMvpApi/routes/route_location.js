const express = require('express');
const extend = require('extend');
const router = express.Router();

const Location = require('../models/location_mvp');

// getting locations
router.get('/', (req, res, next)=>{
    Location.find((err, locations)=>{
        res.json(locations);
    })
});

//adding locations
router.post('/',(req, res, next)=>{
    let newLocation = new Location(req.body);
    newLocation.save((err, location)=>{
        if(err){
            //console.log(err);
            res.json("Error occured in saving : " + err);
        }
        else{
            res.json(location);
        }
    })
})

//deleting locations
router.delete('/',(req, res, next)=>{
    var _id = req.param("id");
    //console.log(_id);
    Location.remove({_id : _id}, (err, result)=>{
        if(err){
            res.json("Error : " + err);
        }
        else{
            res.json("Succesfully deleted");
        }
    } );
})

//Fetch all entries by regex 
router.get('/get_like', (req, res, next)=>{
    var name = req.body.name;
    Location.find({
        first_name : {$regex: "^" + name, $options:"i"}
    }, (err, locations) =>{
        if(err){
            res.json("Error")
        }
        else{
            res.json(locations);
        }
    })
})

router.delete('/delete_all', (req, res, next)=>{
    Location.remove({}, (err, result)=>{
        if(err){
            res.json("Error : " + err);
        }
        else{
            res.json("Succesfully deleted");
        }
    } );
})



module.exports = router;