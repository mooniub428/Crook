<?php

class Category{
    //DB stuff
    private $conn;
    private $table = 'categories';

    //Product properties
    public $id;
    public $name;
    public $description;

    //Constructor
    public function __construct($db){
        $this->conn = $db;
    }

    public function read(){
        
        $query = 'SELECT id, name, description
                FROM
                ' . $this->table;
    
        $stmt = $this->conn->prepare($query);

        $stmt->execute();

        return $stmt;
    }

    public function create(){

        $query = 'INSERT INTO
                FROM
                ' . $this->table . '
                SET
                    name = :name,
                    description = :description';

        //Prepare statement
        $stmt = $this->conn->prepare($query);

        //Clean data
        $this->name = htmlspecialchars(strip_tags($this->name));
        $this->description = htmlspecialchars(strip_tags($this->description));

        //Bind data
        $stmt->bindParam(':name', $this->name);
        $stmt->bindParam(':description', $this->description);

        if($stmt->execute()){
            return true;
        }

        printf('Error: %s', $stmt->error);

        return false;

    }

    
}

?>