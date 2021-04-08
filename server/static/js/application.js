$(document).ready(function(){
    //connect to the socket server.
    var socket = io.connect('http://' + document.domain + ':' + location.port + '/test');
    var alerts_received = [];
    var markers = []
       // Styles a map in night mode.
       var map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 46.00, lng: 2.00},
        zoom: 3,
        styles: [
          {elementType: 'geometry', stylers: [{color: '#242f3e'}]},
          {elementType: 'labels.text.stroke', stylers: [{color: '#242f3e'}]},
          {elementType: 'labels.text.fill', stylers: [{color: '#746855'}]},
          {
            featureType: 'administrative.locality',
            elementType: 'labels.text.fill',
            stylers: [{color: '#d59563'}]
          },
          {
            featureType: 'poi',
            elementType: 'labels.text.fill',
            stylers: [{color: '#d59563'}]
          },
          {
            featureType: 'poi.park',
            elementType: 'geometry',
            stylers: [{color: '#263c3f'}]
          },
          {
            featureType: 'poi.park',
            elementType: 'labels.text.fill',
            stylers: [{color: '#6b9a76'}]
          },
          {
            featureType: 'road',
            elementType: 'geometry',
            stylers: [{color: '#38414e'}]
          },
          {
            featureType: 'road',
            elementType: 'geometry.stroke',
            stylers: [{color: '#212a37'}]
          },
          {
            featureType: 'road',
            elementType: 'labels.text.fill',
            stylers: [{color: '#9ca5b3'}]
          },
          {
            featureType: 'road.highway',
            elementType: 'geometry',
            stylers: [{color: '#746855'}]
          },
          {
            featureType: 'road.highway',
            elementType: 'geometry.stroke',
            stylers: [{color: '#1f2835'}]
          },
          {
            featureType: 'road.highway',
            elementType: 'labels.text.fill',
            stylers: [{color: '#f3d19c'}]
          },
          {
            featureType: 'transit',
            elementType: 'geometry',
            stylers: [{color: '#2f3948'}]
          },
          {
            featureType: 'transit.station',
            elementType: 'labels.text.fill',
            stylers: [{color: '#d59563'}]
          },
          {
            featureType: 'water',
            elementType: 'geometry',
            stylers: [{color: '#17263c'}]
          },
          {
            featureType: 'water',
            elementType: 'labels.text.fill',
            stylers: [{color: '#515c6d'}]
          },
          {
            featureType: 'water',
            elementType: 'labels.text.stroke',
            stylers: [{color: '#17263c'}]
          }
        ]
      });
    
    //receive details from server
    socket.on('alert', function(msg) {
        alerts_received.push(msg.alerts);
        alert_string = '';
        for (var i = 0; i < alerts_received.length; i++){
        var contentString = '<div id="content">'+
            '<div id="siteNotice">'+
            '</div>'+
            '<h1 id="firstHeading" class="firstHeading">'+ 'Drone number : ' + alerts_received[i]['DroneID'] +'</h1>'+
            '<div id="bodyContent">'+
            '<p>'+ 'Drone ID : ' + alerts_received[i]['DroneID'] + '</br>'
            + 'Record : ' + alerts_received[i]['Record'] + '</br>'
            + 'Citizen : ' + alerts_received[i]['Citizen'] + '</br>'
            + 'Message : ' + alerts_received[i]['Message'] + '</br>'
            + 'PeaceScore : ' + alerts_received[i]['PeaceScore'] + '</br>'
            + 'Country : ' + alerts_received[i]['Country'] + '</br>'
            + 'City : ' + alerts_received[i]['City'] + '</br>'
            + 'Latitude : ' + alerts_received[i]['Latitude'] + '</br>'
            + 'Longitude : ' + alerts_received[i]['Longitude'] + '</br>'
            + 'Battery : ' + alerts_received[i]['Battery'] + '</br>'
            + 'Alert : ' + alerts_received[i]['Alert'] + '</br>'
            +'</p>'+
            '</div>'+
            '</div>';

            var marker = new google.maps.Marker({
                position: {lat: alerts_received[i]['Latitude'], lng: alerts_received[i]['Longitude']},
                map: map,
            });
            var infowindow = new google.maps.InfoWindow();
            google.maps.event.addListener(marker, 'click', function() {

    
            infowindow.setContent(contentString);
            infowindow.open(map, marker);
            });
    
            alert_string = alert_string +
            '<tr>'+
              '<td>'+i+'</td>'+
              '<td>'+alerts_received[i]["DroneID"]+'</td>'
              +'<td>'+alerts_received[i]['Record']+'</td>'
              +'<td>'+alerts_received[i]['Citizen']+'</td>'
              +'<td>'+alerts_received[i]['Message']+'</td>'
              +'<td>'+alerts_received[i]['PeaceScore']+'</td>'
              +'<td>'+alerts_received[i]['Country']+'</td>'
              +'<td>'+alerts_received[i]['City']+'</td>'
              +'<td>'+alerts_received[i]['Latitude']+'</td>'
              +'<td>'+alerts_received[i]['Longitude']+'</td>'
              +'<td>'+alerts_received[i]['Battery']+'</td>'
              +'<td>'+alerts_received[i]['Alert']+'</td>'
            +'</tr>'
        }
        $('#alerts').html(alert_string);
    });
});