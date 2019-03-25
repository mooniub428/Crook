<?php

header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Access-Control-Allow-Headers,Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

include_once '../../config/Database.php';
include_once '../../models/Cart.php';
include_once '../../models/CartItem.php';

//Instantiate DB & connect
$database = new Database();
$db = $database->connect();

//Instantiate cart object
$cart = new Cart($db);
$cartitem = new CartItem($db);

//Get raw posted data
$data = json_decode(file_get_contents("php://input"));

$userEmail = $data->email;
$cartitems = $data->cartitems;

//print_r($data);
// print_r($userEmail);
// print_r($cartitems);

// $userEmail = isset($_POST['email']) ? $_POST['email'] : die();
// $cartItems = isset($_POST['cartitems']) ? $_POST['cartitems'] : die();

$newcart = array();
foreach($cartitems as $item){

    $newcart[$item->product_id] = $item->quantity;

}

/* First we need to retrieve cart by using email */
$cart->read_using_email($userEmail);

/* Then using cart_id we need to retrieve cartitems (oldcart) */
$cartitem->cart_id = $cart->id;
$result = $cartitem->read_using_cart_id();

$num = $result->rowCount();

if($num > 0){

    /* First push the product_ids & quantities in oldcart array */ 
    $oldcart = array();
    while($row = $result->fetch(PDO::FETCH_ASSOC)){
        extract($row);

        $oldcart[$product_id] = $quantity;
    }
    /* Old cart example : key = product_id, value = quantity
    (
        [5] => 3
        [2] => 1
        [7] => 1
        [1] => 1
        [3] => 2
    )
    */
    /* cartitems with product_ids which belong in oldcart and not in newcart */
    $cartitems_todelete = array_diff_key($oldcart, $newcart); 

    /* cartitems with product_ids which belong in new cart and not in oldcart */
    $cartitems_toadd = array_diff_key($newcart, $oldcart);

    /**
     * One way to get which cartitems to update, is to find the cartitems with same
     * product id and then search for different values. The problem is that array_diff doesnt work
     * properly for some reason. So what we are going to do is we will manually search
     * for items of newcart with same keys (product_ids) but different values(quantities)
     * with old cart.
     * Note: in_array function needs the third patameterer true for type checking as well.
     * Without this third parameter it will not work properly
     */
    $cartitems_toupdate = array();
    foreach($newcart as $key=>$value){
        if((isset($oldcart[$key]) || array_key_exists($key, $oldcart)) && $oldcart[$key] != $value){
            $cartitems_toupdate[$key] = $value;
        }
    }

    /* Print arrays */
    /*print_r($oldcart);
    print_r($newcart);
    print_r($cartitems_todelete);
    print_r($cartitems_toadd);
    print_r($cartitems_toupdate);*/

    $cartitem->cart_id = $cart->id;

    /* Delete the cartitems_todelete from database */
    foreach($cartitems_todelete as $key=>$value){
        $cartitem->product_id = $key;
        if(!$cartitem->delete_using_index()){
            $response = array(
                'message' => 'Product id ' . $key . ' was not deleted successfully',
                'success' => 0
            );
            echo json_encode($response);
            die();
        }
        echo 'Product with id ' . $key . ' was deleted successfully.\n';
    }
    
    /* Add the cartitems_toadd to database */
    foreach($cartitems_toadd as $key=>$value){
        $cartitem->product_id = $key;
        $cartitem->quantity = $value;
        if(!$cartitem->create_using_index()){
            $response = array(
                'message' => 'Product id ' . $key . ' was not added successfully',
                'success' => 0
            );
            echo json_encode($response);
            die();
        }
        echo 'Product with id ' . $key . ' was added successfully.\n';
    }
    
    /* Update the cartitems_toupdate to database */
    foreach($cartitems_toupdate as $key=>$value){
        $cartitem->product_id = $key;
        $cartitem->quantity = $value;
        if(!$cartitem->update_using_index()){
            $response = array(
                'message' => 'Product id ' . $key . ' was not updated successfully',
                'success' => 0
            );
            echo json_encode($response);
            die();
        }
        echo 'Product with id ' . $key . ' was updated successfully.\n';
    }

    $response = array(
        'message' => 'Cart was published successfully',
        'success' => 1
    );
    echo json_encode($response);
    

} else {
    $response = array(
        'message' => 'No cart for user with email ' . $userEmail . 'was found',
        'success' => 0
    );
    echo json_encode($response);
}

?>