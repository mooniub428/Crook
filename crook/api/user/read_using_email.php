<?php
//Headers
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

include_once '../../config/Database.php';
include_once '../../models/User.php';

//Instantiate DB & connect
$database = new Database();
$db = $database->connect();

//Instantiate blog post object
$user = new User($db);

//Get email
$user->email = isset($_GET['email']) ? $_GET['email'] : die();

//Get user
$user->read_using_email();

//Create array
$user_arr = array(
    'id' => $user->id,
    'email' => $user->email,
    'firstname' => $user->firstname,
    'lastname' => $user->lastname,
    'created_at' => $user->created_at
);

//Make JSON
echo json_encode($user_arr);
//print_r(json_encode($post_arr));

?>