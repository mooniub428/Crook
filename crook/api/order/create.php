<?php
//Headers
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Access-Control-Allow-Headers,Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

include_once '../../config/Database.php';
include_once '../../models/Order.php';

//Instantiate DB & connect
$database = new Database();
$db = $database->connect();

//Instantiate order object
$order = new Order($db);

//Get data input
$input = json_decode(file_get_contents("php://input"));

//Initialize order properties
$order->user_id = $input->user_id;

//Create user
if($order->create()){
    echo json_encode(
        array('message' => 'Order was successfully added')
    );
} else {
    echo json_encode(
        array('message' => 'Order was NOT successfully added')
    );
}
