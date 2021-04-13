const express = require('express');
const extend = require('extend');
const router = express.Router();

const Driver = require('../models/driver_mvp');

// getting drivers
router.get('/', (req, res, next)=>{
    Driver.find((err, drivers)=>{
        res.json(drivers);
    })
});

// getting datas
router.post('/owned_by', (req, res, next)=>{
    var owner_contact = req.body.owner_contact;
    Driver.find({owner_contact : owner_contact}
    ,(err, drivers)=>{
        res.json(drivers);
    })
});

router.post('/active_clients', (req, res, next)=>{
    var owner_contact = req.body.owner_contact;
    Driver.find({
        owner_contact : owner_contact,
        access : "yes"
    }
    ,(err, drivers)=>{
        res.json(drivers);
    })
});


router.post('/check_access', (req, res, next)=>{
    var contact = req.body.driver_id;
    Driver.find ({ contact : contact }
    ,(err, drivers)=>{
        res.json(drivers);
    })
})

//adding drivers
router.post('/',(req, res, next)=>{
    let newDriver = new Driver(req.body);
    newDriver.save((err, driver)=>{
        if(err){
            //console.log(err);
            res.json("Error occured in saving : " + err);
        }
        else{
            res.json(driver);
        }
    })
})

//updating structures
router.post('/update',(req, res, next)=>{
    // console.log("Update function called");
    let _id = req.body.id;
    let driver_data = req.body.driver;
    let driver = JSON.parse(driver_data);

    Driver.find({ _id : _id }, (err, result)=>{
        if(err){
            res.json("Error : " + err);
        }
        else { 
            let access = result[0].access;
            if(access == "revoked"){
                res.json({ "status" : "revoked" });
            }
            else{
                Driver.findByIdAndUpdate(_id, driver, (err, result)=>{
                    if(err){
                        res.json("Error : " + err);
                    }
                    else{
                        res.json({ "status" : "success" });
                    }
                })
            }
        }
    })
})

//deleting drivers
router.delete('/',(req, res, next)=>{
    var _id = req.param("id");
    //console.log(_id);
    Driver.remove({_id : _id}, (err, result)=>{
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
    Driver.find({
        first_name : {$regex: "^" + name, $options:"i"}
    }, (err, drivers) =>{
        if(err){
            res.json("Error")
        }
        else{
            res.json(drivers);
        }
    })
})

router.delete('/delete_all', (req, res, next)=>{
    Driver.remove({}, (err, result)=>{
        if(err){
            res.json("Error : " + err);
        }
        else{
            res.json("Succesfully deleted");
        }
    } );
})



module.exports = router;