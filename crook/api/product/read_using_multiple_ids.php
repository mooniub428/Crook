<?php
//Headers
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

include_once '../../config/Database.php';
include_once '../../models/Product.php';

//DB stuff & connect
$database = new Database();
$db = $database->connect();

//Instantiate new product object
$product = new Product($db);

$thumbsDir = 'http://' . $database->get_host_ip() . '/crook/Storage/thumbnails/products/';

if(isset($_GET['product_id']))
    $product->id = $_GET['product_id'];
else {
    echo json_encode(
        array('message' => 'product ids weren\'t passed via GET parameters')
    );
    die();
}

//Read products
$result = $product->read_using_multiple_ids();

//Get row count
$num = $result->rowCount();

if($num > 0){
    $response = array();
    $response['products'] = array();

    while($row = $result->fetch(PDO::FETCH_ASSOC)){
        extract($row);

        $thumbPath = $thumbsDir . $id . '.jpg';

        $product_item = array(
            'id' => $id,
            'name' => $name,
            'desc' => $description,
            'price' => $price,
            'category_id' => $category_id,
            'category_name' => $category_name,
            'thumb' => $thumbPath
        );

        //Push to "response"
        array_push($response['products'], $product_item);
    }

    $response['message'] = 'Products were retrieved successfully';
    $response['success'] = 1;
} else {
    /* No products were found with these ids */
    $response['message'] = 'No products were found with these ids';
    $response['success'] = 0;
}

echo json_encode($response);