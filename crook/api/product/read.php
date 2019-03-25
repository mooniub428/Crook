<?php
//Headers
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

include_once '../../config/Database.php';
include_once '../../models/Product.php';

//Instantiate DB & connect
$database = new Database();
$db = $database->connect();

//Instantiate product object
$product = new Product($db);

$thumbsDir = 'http://' . $database->get_host_ip() . '/crook/Storage/thumbnails/products/';

//read products
$result = $product->read();
//get row count
$num = $result->rowCount();

//Check if any posts
if($num > 0){
    //Product array
    $response = array();
    $response['products'] = array();

    while($row = $result->fetch(PDO::FETCH_ASSOC)){
        extract($row);
        
        /* set thumbpath */
        $thumbPath = $thumbsDir . $id . '.jpg';

        /* set product item */
        $product_item = array(
            'id' => $id,
            'name' => $name,
            'desc' => $description,
            'price' => $price,
            'category_id' => $category_id,
            'category_name' => $category_name,
            'thumb' => $thumbPath
        );

        /* Push product item to response */
        array_push($response['products'], $product_item);

    }

    //success
    $response["success"] = 1;

    //Turn to JSON & output
    echo json_encode($response);

} else {
    //No posts
    $response["success"] = 0;
    echo json_encode($response);
}