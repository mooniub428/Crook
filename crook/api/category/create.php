<?php
//Headers
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Access-Control-Allow-Headers,Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

include_once '../../config/Database.php';
include_once '../../models/Category.php';

//DB stuff & connect
$database = new Database();
$db = $database->connect();

//Instantiate new category object
$category = new Category($db);

//Get data input
$input = json_decode(file_get_contents("php://input"));

//Set category properties
$category->name = $input->name;
$category->description = $input->description;

if($category->create()){
    echo json_encode(
        array('message' => 'Category was added successfully')
    );
} else {
    echo json_encode(
        array('message' => 'Category was NOT added successfully')
    );
}
?>