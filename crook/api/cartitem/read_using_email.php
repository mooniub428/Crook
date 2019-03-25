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
$userEmail = isset($_GET['email']) ? $_GET['email'] : die();

/* First get the cart's id using the user's email */
$cart->read_using_email($userEmail);

$cartitem->cart_id = $cart->id;

//Get result
$result = $cartitem->read_using_cart_id();

//Get num of rows
$num = $result->rowCount();

if($num > 0){
    $response = array();
    $response['cartitems'] = array();

    while($row = $result->fetch(PDO::FETCH_ASSOC)){
        extract($row);

        $item = array(
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

echo json_encode($response);