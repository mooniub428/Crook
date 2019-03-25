<?php
//Headers
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Access-Control-Allow-Headers,Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

include_once '../../config/Database.php';
include_once '../../models/Product.php';

//DB stuff & connect
$database = new Database();
$db = $database->connect();

//Instantiate product object
$product = new Product($db);

$thumbsDir = 'http://' . $database->get_host_ip() . '/crook/Storage/thumbnails/products/';

$relativeDir = '../../Storage/thumbnails/products/';

//Get data input
// $input = json_decode(file_get_contents("php://input"));

// $product->name = $input->name;
// $product->description = $input->description;
// $product->price = $input->price;
// $product->category_id = $input->category_id;
// $encodedThumb = $input->encodedThumb;

//Get POST parameters
$product->name = $_POST['name'];
$product->description = $_POST['desc'];
$product->price = $_POST['price'];
$product->category_id = $_POST['category_id'];
$encodedThumb = $_POST['encodedThumb'];


if($product->productExists()){
    $response = array(
        'message' => 'Product already exists',
        'success' => 0
    );

} else {

    if($product->create()){
        
        //Get last inserted product's id
        $lastId = $product->getLastId();
        $thumbPath = $relativeDir . $lastId . '.jpg';
        file_put_contents($thumbPath,base64_decode($encodedThumb));

        $response = array(
            'message' => 'Product was added successfully',
            'success' => 1
        );
    
    } else {
        $response = array(
            'message' => 'Error occured while trying to add product to database',
            'success' => 0
        );
    }

}

echo json_encode($response);


