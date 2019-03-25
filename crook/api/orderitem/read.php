<?php
//Headers
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

//includes
include_once '../../config/Database.php';
include_once '../../models/OrderItem.php';

//Instantiate DB & connect
$database = new Database();
$db = $database->connect();

//Instantiate new orderitem object
$orderitem = new OrderItem($db);

//Get result
$result = $orderitem->read();

$rows = $result->rowCount();

if($rows > 0){
    $orderitem_arr = array();
    $orderitem_arr['data'] = array();

    while($row = $result->fetch(PDO::FETCH_ASSOC)){
        extract($row);

        $orderitem_item = array(
            'id' => $id,
            'order_id' => $order_id,
            'product_id' => $product_id,
            'quantity' => $quantity
        );

        array_push($orderitem_arr['data'], $orderitem_item);
    }

    //Turn to JSON & output
    echo json_encode($orderitem_arr);
} else {
    echo json_encode(
        array('message' => 'No orderitems found!')
    );
}

?>