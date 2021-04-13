let map;
let min_diff = 0.05;

function initMap() {
map = new google.maps.Map(document.getElementById("map"), {
    center: { lat: 25.4358, lng: 81.8463 },
    zoom: 5,
});
callCoordinates();
// // const myLatLng = { lat: 25.4358, lng: 81.8463 };
// createMarker(myLatLng);
}

function callCoordinates(){
    console.log("Fetching coordinates");
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            var resp = JSON.parse(this.responseText);
            for(i=0;i<resp.length;i++){
                coordinates_data = resp[i]
                let latitude = parseFloat(coordinates_data['latitude']);
                let longitude = parseFloat(coordinates_data['longitude']);
                let city_name = coordinates_data['city_name'];
                let location_type = coordinates_data['location_type'];
                // const myLatLng = { lat : parseFloat(latitude), lng : parseFloat(longitude) };
                // createMarker(myLatLng, city_name, location_type);
                createRectangle(latitude, longitude, city_name, location_type);
            }
            
        }
    };
    xhttp.open("GET","http://localhost:3000/api/coordinates", true);
    xhttp.send();
}

// function createMarker(myLatLng, city_name, location_type){
    // new google.maps.Marker({
    //     position: myLatLng,
    //     map,
    //     icon: {
    //         path: google.maps.SymbolPath.CIRCLE,
    //         scale: 3,
    //         strokeColor: "red"
    //       },
    //     title: city_name,
    //   });
// }

function createRectangle(latitude, longitude, city_name, location_type){
    let fillColor = '#FF0000';
    if(location_type=="L1"){
        fillColor = '#0000FF';
    }
    const rectangle = new google.maps.Rectangle({
        strokeColor: fillColor,
        strokeOpacity: 0.8,
        strokeWeight: 2,
        fillColor: fillColor,
        fillOpacity: 0.35,
        map,
        bounds: {
        north: latitude - min_diff,
        south: latitude + min_diff,
        east: longitude + min_diff,
        west: longitude - min_diff,
        },
    });
}