<?php
//Headers
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

include_once '../../config/Database.php';
include_once '../../models/CartItem.php';
include_once '../../models/Cart.php';

//DB stuff & connect
$database = new Database();
$db = $database->connect();

//Instantiate new cartitem object
$cartitem = new CartItem($db);
$cart = new Cart($db);

/* First get the cart's id using the user's id */


$result = $cartitem->read_using_user_id();

$num = $result->rowCount();

if($num > 0){
    $response = array();
    $response['cartitems'] = array();

    while($row = $result->fetch(PDO::FETCH_ASSOC)){
        extract($row);

        $item = array(
            'cart_id' => $cart_id,
            'product_id' => $product_id,
            'product_name' => $product_name,
            'quantity' => $quantity,
            'cart_id' => $cart_id
        );

        array_push($response['cartitems'], $item);
    }

    $response['message'] = 'Cart of user was retrieved successfully';
    $response['success'] = 1;

} else {
    $response['message'] = 'Not even one cartitem was found';
    $response['success'] = 0;
}