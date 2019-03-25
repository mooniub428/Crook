<?php
//Headers
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

include_once '../../config/Database.php';
include_once '../../models/Order.php';

//Instantiate db & connect
$database = new Database();
$db = $database->connect();

//Instantiate order object
$order = new Order($db);

//read orders
$result = $order->read();

//get num of rows of result
$rows = $result->rowCount();

if($rows > 0){
    $orders_arr = array();
    $orders_arr['data'] = array();

    while($row = $result->fetch(PDO::FETCH_ASSOC)){
        extract($row);

        $order_item = array(
            'id' => $id,
            'user_id' => $user_id
        );

        //Push to "data"
        array_push($orders_arr['data'], $order_item);  
        
    }

    //Turn to JSON & output
    echo json_encode($orders_arr);
} else {
    //No posts
    echo json_encode(
        array('message' => 'No Orders Found')
    );
}



