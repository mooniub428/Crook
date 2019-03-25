<?php
//Headers
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Access-Control-Allow-Headers,Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

include_once '../../config/Database.php';
include_once '../../models/User.php';

//Instantiate DB & connect
$database = new Database();
$db = $database->connect();

//Instantiate user object
$user = new User($db);

//Get raw posted data
//$data = json_decode(file_get_contents("php://input"));

//Get email and password
$insertedEmail = isset($_GET['email']) ? $_GET['email'] : die();
$insertedPass = isset($_GET['password']) ? $_GET['password'] : die();
$user->email = $insertedEmail;

$result = $user->checkLogin();
$rows = $result->rowCount();

if($rows == 1){
    $row = $result->fetch(PDO::FETCH_ASSOC);
    extract($row);

    $user_item = array(
    'id' => $id,
    'firstname' => $firstname,
    'email' => $email,
    'password' => $password
    );

    if($insertedPass == $user_item['password']){
        $response = array(
            'message' => 'User was found',
            'firstname' => $user_item['firstname'],
            'success' => 1
        );
        echo json_encode($response);
         
    } else {
    
        $response = array(
            'message' => 'Invalid password',
            'success' => 0
        );
        echo json_encode($response);
    }


} else if(rows < 1) {
    echo json_encode(
        array('message' => 'No users returned')
    );
} else if(rows > 1){
    echo json_encode(
        array(
            'message' => 'More than one users returned',
            'success' => 0
        )
    );
}




