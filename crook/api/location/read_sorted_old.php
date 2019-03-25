<?php
//Headers
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

include_once '../../config/Database.php';
include_once '../../models/Location.php';

//Instantiate DB & connect
$database = new Database();
$db = $database->connect();

//Instantiate product object
$location = new Location($db);

/**
 * First we need to read locations and store them into an array
 */

 /**
  * NOTE: For keys of speeds array, locations & distances, we are not going to use location ids 
  * but user ids cause it will be faster later to use these user ids to search for users' data
  * and return them to store 
  */
$result = $location->read();
$num = $result->rowCount();

if($num > 0){
    $locations = array();
    $speeds = array();
    while($row = $result->fetch(PDO::FETCH_ASSOC)){
        extract($row);

        $item = array(
            'latitude' => $latitude,
            'longitude' => $longitude
        );
        
        $speeds[$user_id] = $speed;
        $locations[$user_id] = $item;
    }
}

/**
 * locations format:
 * locations = array(
 *     [$id] => array(user_id => $user_id, latitude => $latitude, longitude => $longitude),
 *     ...
 * );
 */

/**
 * Now we need to iterate through locations and for each location
 * to calculate the distance to the store and store it in another array.
 */
$distances = array();
foreach($locations as $key=>$value){
    $locationInfo = $value;

    $location->latitude = $locationInfo['latitude'];
    $location->longitude = $locationInfo['longitude'];
    $distanceKM = $location->calc_distance("K");

    $distances[$key] = $distanceKM; 
    
}

/**
 * Now that we have both distances and speeds arrays, it's time to product
 * the time remaining array.
 * CAUTION: We assume that user is moving with stable velocity something 
 * that's pretty rare to happen.
 */
$times = array();
 foreach($distances as $key=>$value){
     $time = $distances[$key] / $speeds[$key];
     $times[$key] = $time;
 }

print_r($locations);
print_r($distances);
print_r($speeds);
print_r($times);
