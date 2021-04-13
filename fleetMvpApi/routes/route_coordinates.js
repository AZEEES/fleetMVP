const express = require('express');
const extend = require('extend');
const router = express.Router();

const Coordinates = require('../models/coordinates_mvp');

router.get('/', (req, res, next)=>{
    Coordinates.find((err, locations)=>{
        res.json(locations);
    })
});

//adding locations
router.post('/',(req, res, next)=>{
    let newCoordinate = new Coordinates(req.body);
    newCoordinate.save((err, coordinate)=>{
        if(err){
            //console.log(err);
            res.json("Error occured in saving : " + err);
        }
        else{
            res.json(coordinate);
        }
    })
})

//deleting coordinates
router.delete('/',(req, res, next)=>{
    var _id = req.param("id");
    //console.log(_id);
    Coordinates.remove({_id : _id}, (err, result)=>{
        if(err){
            res.json("Error : " + err);
        }
        else{
            res.json("Succesfully deleted");
        }
    } );
})

//Fetch all entries by regex 
router.post('/find_all_cities', (req, res, next)=>{
    let latitude = parseFloat(req.body.latitude);
    let longitude = parseFloat(req.body.longitude);
    let diff = parseFloat(req.body.diff);
    let lat1 = latitude - diff;
    let lat2 = latitude + diff;
    let long1 = longitude - diff;
    let long2 = longitude + diff;
    Coordinates.find({
        latitude : { $gt : lat1, $lt : lat2 },
        longitude : { $gt : long1, $lt : long2 }
    }, (err, coordinates) =>{
        if(err){
            res.json("Error")
        }
        else{
            res.json(coordinates);
        }
    })
})

module.exports = router;