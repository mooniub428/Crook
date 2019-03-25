<?php
//Headers
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Access-Control-Allow-Headers,Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

include_once '../../config/Database.php';
include_once '../../models/Location.php';
include_once '../../models/User.php';

//DB stuff & connect
$database = new Database();
$db = $database->connect();

//Instantiate product object
$location = new Location($db);
$user = new User($db);

//$data = json_decode(file_get_contents('php://input'));

$user->email = isset($_POST['email']) ? $_POST['email'] : die();
$location->latitude = isset($_POST['latitude']) ? $_POST['latitude'] : die();
$location->longitude = isset($_POST['longitude']) ? $_POST['longitude'] : die();
$location->speed = isset($_POST['speed']) ? $_POST['speed'] : die();

/**
 * If velocity is zero, then we will make it pretty low, to avoid problems
 * in the future when we try to use it as a divider
 */

 if($location->speed == 0){
    $location->speed = 0.001;
 }

 /**
  * First we need to calculate the distance between the user and the store
  */
$distance = $location->calc_distance("K");

 /**
 * Now we have to compute the time remaining for user to reach the store
 */
$location->time_remain = $distance / $location->speed;


/**
 * No need to check if user email exists in database cause
 * user has to be already signed up and in to run this script
 */
$user->read_using_email();
$location->user_id = $user->id;

/* If location of user already exists update it, else create it */ 
if($location->locationExists()){
   $success =  $location->update_using_userId();
} else {
   $success =  $location->create(); //first time
}

$response = array(
   'success' => $success
);


echo json_encode($response);
