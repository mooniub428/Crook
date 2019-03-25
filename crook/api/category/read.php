<?php
//Headers
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

include_once '../../config/Database.php';
include_once '../../models/Category.php';

//Instantiate DB & connect
$database = new Database();
$db = $database->connect();

//Instantiate category object
$category = new Category($db);

$thumbsDir = 'http://' . $database->get_host_ip() . '/crook/Storage/thumbnails/categories/';

//Read categories
$result = $category->read();

//Get num of rows of result
$rows = $result->rowCount();

//Check if there are any categories in db
if($rows > 0){
    $response = array();
    $response['categories'] = array();

    while($row = $result->fetch(PDO::FETCH_ASSOC)){
        extract($row);

        //echo $thumbsDir . '/n';
        //echo $category_item['thumbnail'] . '/n';
        
        
        $thumbPath = $thumbsDir . $id . '.jpg';
        //$thumb = file_get_contents($thumbPath);

        $category_item = array(
            'id' => $id,
            'name' => $name,
            'desc' => $description,
            'thumb' => $thumbPath
        );

        
        //printf("Path for category %s is %s\n", $category_item['name'], $thumbPath);
        //$thumbName = preg_replace('{\$}', '', $thumbName);

        //echo $thumbName . '/n';
        
        //header('Content-Type: image/jpeg');
        //$thumb = glob("$thumbName*.{png,jpeg,jpg,gif}", GLOB_BRACE);
        //$thumb = '<img src="' . $thumbName . '" border="0">';

        //echo $thumb;

        array_push($response['categories'], $category_item);
        //array_push($response['thumbnails'], $encodedThumb);
        //$response[] = $thumb;
        //array_push($response['thumbnails'], $thumb);
    }

    echo json_encode($response);
}
?>