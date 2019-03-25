<?php
//Headers
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Access-Control-Allow-Headers,Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

include_once '../../config/Database.php';
include_once '../../models/OrderItem.php';

//Instantiate db connections
$database = new Database();
$db = $database->connect();

//Instantiate orderitem object
$orderitem = new OrderItem($db);

//Get data input
$input = json_decode(file_get_contents("php://input"));

//Initialize orderitem properties
$orderitem->order_id = $input->order_id;
$orderitem->product_id = $input->product_id;
$orderitem->quantity = $input->quantity;


//Create orderitem
if($orderitem->create()){
    echo json_encode(
        array('message' => 'Orderitem was successfully added!')
    );
} else {
    echo json_encode(
        array ('message' => 'Orderitem was NOT successfully added!')
    );
}
?>