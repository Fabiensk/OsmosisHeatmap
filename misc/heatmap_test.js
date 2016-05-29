// Weight use for all the points.
var POINT_WEIGHT = 1;

var mymap = null;

document.addEventListener('DOMContentLoaded', function () {
  var min_lat = 90;
  var min_lon = 180;
  var max_lat = -90;
  var max_lon = -180;
  for (var idx in heatmap_data) {
    heatmap_data[idx][2] = POINT_WEIGHT;
    min_lat = Math.min(min_lat, heatmap_data[idx][0]);
    max_lat = Math.max(max_lat, heatmap_data[idx][0]);
    min_lon = Math.min(min_lon, heatmap_data[idx][1]);
    max_lon = Math.max(max_lon, heatmap_data[idx][1]);
  }
  center_lat = (min_lat+max_lat)/2;
  center_lon = (min_lon+max_lon)/2;

  mymap = L.map('mapid').setView([center_lat, center_lon], 15);
  L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
  }).addTo(mymap);
  
  var heat = L.heatLayer(heatmap_data, {radius: 25}).addTo(mymap);    
});
