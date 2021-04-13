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

router.get('/untagged', (req, res, next)=>{
    Location.find({
        city_name : "NA"
    }, (err, locations) =>{
        if(err){
            res.json("Error")
        }
        else{
            res.json(locations);
        }
    })
})


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

//updating structures
router.post('/update',(req, res, next)=>{
    // console.log("Update function called");
    let _id = req.body.id;
    let location_data = req.body.location;
    let location = JSON.parse(location_data);
    Location.findByIdAndUpdate(_id, location, (err, result)=>{
        if(err){
            res.json("Error : " + err);
        }
        else{
            res.json("success");
        }
    })
    // res.json("success");
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

router.post('/get_last_location', (req, res, next)=>{
    var driver_ids = JSON.parse(req.body.driver_ids);
    driver_ids = driver_ids.map(String);
    Location.aggregate(
        [
            {$match : {driver_id : { $in : driver_ids }}},
            {$group: {
                "_id": "$driver_id",
                "driver_id" : {$last : "$driver_id"},
                "latitude" : {$last : "$latitude"},
                "longitude" : {$last : "$longitude"},
                "timestamp" : {$last : "$timestamp"},
                "city_name" : {$last : "$city_name"}
            }}
        ],(err, result)=>{
            if(err){
                res.json("Error : " + err);
            }
            else{
                res.json(result);
            }
        }
    ) ;
})



module.exports = router;