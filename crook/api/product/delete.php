<?php
//Headers
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');
header('Access-Control-Allow-Methods: DELETE');
header('Access-Control-Allow-Headers: Access-Control-Allow-Headers,Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

include_once '../../config/Database.php';
include_once '../../models/Product.php';

//Instantiate DB & connect
$database = new Database();
$db = $database->connect();

//Instantiate product class object
$product = new Product($db);

$data = json_decode(file_get_contents("php://input"));
$product->id = $data->id;

//Get DELETE data
//$product->id = $_DELETE['id'];

//Delete product
if($product->delete()){
    $response = array(
        'message' => 'Product was deleted successfully',
        'success' => 1
    );
    
} else {
    $response = array(
        'message' => 'Product was NOT deleted successfully',
        'success' => 0
    );
}

echo json_encode($response);

