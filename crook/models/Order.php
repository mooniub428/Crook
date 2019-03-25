<?php

class Order{
    //Db stuff
    private $conn;
    private $table = 'orders';

    //Order properties
    public $id;
    public $user_id;

    //Constructor
    public function __construct($db){
        $this->conn = $db;
    }

    public function read(){
        
        $query = 'SELECT
                    id,
                    user_id
                FROM
                ' . $this->table . '
                ORDER BY
                    user_id ASC';

        $stmt = $this->conn->prepare($query);

        $stmt->execute();

        return $stmt;
    }

    public function create(){

        $query = 'INSERT INTO 
                    ' . $this->table . '
                SET
                    user_id = :user_id';
        
        //Prepare statement
        $stmt = $this->conn->prepare($query);

        //Clean data
        $this->user_id = htmlspecialchars(strip_tags($this->user_id));

        //Bind data
        $stmt->bindParam(':user_id', $this->user_id);

        if($stmt->execute()){
            return true;
        }

        //print error if something goes wrong
        printf("Error: %s. \n", $stmt->error);

        return false;


    }
}