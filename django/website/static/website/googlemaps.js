var rectangle;
var map;
var irelandBoundingBox = {
	north: 55.45,
	south: 51.40,
	east:  -5.35,
	west: -10.60
};

function initMap() {
	var bounds = {
		north: 55.45,
		south: 51.40,
		east:  -5.35,
		west: -10.60
	};

	map = new google.maps.Map(document.getElementById('map'), {
		streetViewControl: false,
		center: {
			lat: (bounds.north-bounds.south)*0.5+bounds.south, 
			lng: (bounds.west-bounds.east)*0.5+bounds.east
		},
		zoom: 6
	});

	rectangle = new google.maps.Rectangle({
		map: map,
		bounds: bounds,
		editable: true,
		draggable: true,
		strokeColor: "#cc0000",
		strokeOpacity: 0.8,
		strokeWeight: 2,
		fillColor: "#000000",
		fillOpacity: 0.1
	});
}

function getMapBoundingBox(){
	var bounds = rectangle.getBounds().toJSON();
	bounds.north = +bounds.north.toFixed(4);
	bounds.south = +bounds.south.toFixed(4);
	bounds.east = +bounds.east.toFixed(4);
	bounds.west = +bounds.west.toFixed(4);
	return bounds;
}

function createNewMap(lat, lng, zoom){
	map = new google.maps.Map(document.getElementById('map'), {
		streetViewControl: false,
		center: {
			lat: lat, 
			lng: lng
		},
		zoom: zoom
	});
}

function placeMapMarker(lat, lng){
	var marker = new google.maps.Marker({
		position: {lat:lat, lng:lng},
		map: map
	});
}