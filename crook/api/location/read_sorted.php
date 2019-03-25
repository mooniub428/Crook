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

$result = $location->read_asc_time();

$num = $result->rowCount();

if($num > 0){

    $response = array();
    $response['arrivals'] = array();
    while($row = $result->fetch(PDO::FETCH_ASSOC)){
        extract($row);

        
        /*We don't care for users that need more than 30 minutes to reach the store */
        if($time_remain < 100){
            $item = array(
                'user_id' => $uid,
                'email' => $email,
                'firstname' => $firstname,
                'lastname' => $lastname,
                'time_remain' => $time_remain
            ); 
        }

        array_push($response['arrivals'], $item);
        
    }
    $response['message'] = 'Imminent arrivals were retrieved';
    $response['success'] = 1;

} else {
    
    $response['message'] = 'There are no imminent arrivals';
    $response['success'] = 2;
}

echo json_encode($response);

