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

//Instantiate blog post object
$user = new User($db);

//Get raw posted data
//$data = json_decode(file_get_contents("php://input"));

//Get POST parameters
$user->email = $_POST['email'];
$user->firstname = $_POST['firstname'];
$user->lastname = $_POST['lastname'];
$user->password = $_POST['password'];

// $user->email = $data->email;
// $user->firstname = $data->firstname;
// $user->lastname = $data->lastname;
// $user->password = $data->password;

//Create user
if($user->create()){
    $response = array(
        'message' => 'User\'s signup was successfull!',
        'success' => 1
    );
} else {
    $response = array(
        'message' => 'User\'s signup was NOT successfull!',
        'success' => 0
    );
}

echo json_encode($response);

?>
