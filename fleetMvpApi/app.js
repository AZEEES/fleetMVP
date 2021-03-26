//importing modules 
var express = require('express');
var mongoose = require('mongoose');
var cors = require('cors');
var bodyparser = require('body-parser');
var path = require('path');

//routing files
const route_owner = require('./routes/route_owner.js');
const route_driver = require('./routes/route_driver.js');
const route_location = require('./routes/route_location.js');

//test for trial

var app = express();
//port no
const port = 3000;

//adding middleware cors and bodyparser
app.use(cors());
app.use(bodyparser.json()); 
app.use(express.urlencoded());

//routes
app.use('/api/owner', route_owner);
app.use('/api/driver', route_driver);
app.use('/api/location', route_location);


const uri = "mongodb+srv://fleetAdmin:ZZpro@981@cluster0.rvujb.mongodb.net/fleetDb?retryWrites=true&w=majority";
mongoose.connect(uri, { useNewUrlParser: true });

//on connection
mongoose.connection.on('connected', () => {
    console.log('Connected to database mongodb');
});

//on error 
mongoose.connection.on('error', (err) => {
    if(err){
        console.log('Error occured while connecting to mongodb' + err);
    }
});

//testing server 
app.get('/', (req, res) => {
    res.send('foobar');
})



//static files
app.use(express.static(path.join(__dirname, 'public')));


app.get('/', (req, res)=>{
    res.send("Server up and running"); 
})
.listen(process.env.PORT || 3000)