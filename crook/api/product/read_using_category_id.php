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

if(isset($_GET['category_id']))
    $product->category_id = $_GET['category_id'];
else {
    echo json_encode(
        array('message' => 'category_id wasn\'t passed via GET parameters')
    );
    die();
}

//Read products using inserted category id
$result = $product->read_using_category_id();

$rows = $result->rowCount();

if($rows > 0){
    $response = array();
    $response['products'] = array();

    while($row = $result->fetch(PDO::FETCH_ASSOC)){
        extract($row);

        $thumbName = $id;

        $thumbPath = $thumbsDir . $thumbName . '.jpg';

        $product_item = array(
            'id' => $id,
            'name' => $name,
            'description' => $description,
            'price' => $price,
            'category_id' => $category_id,
            'category_name' => $category_name,
            'thumb' => $thumbPath
        );

        //Push to "response"
        array_push($response['products'], $product_item);
    }

    //success
    $response['success'] = 1;

    //Turn to JSON & output
    echo json_encode($response);

} else {
    //fail
    $response['success'] = 0;

    echo json_encode($response);
}
