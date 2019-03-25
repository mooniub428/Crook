<?php

class Product{
    //DB stuff
    private $conn;
    private $table = 'products';

    //Product properties
    public $id;
    public $name;
    public $description;
    public $price;
    public $category_id;
    public $thumbnail;
    public $created_at;
    public $updated_at;

    //Constructor
    public function __construct($db){
        $this->conn = $db;
    }

    //Get Products
    public function read(){
        //Create query
        $query = 'SELECT
                c.name as category_name,
                p.id,
                p.name,
                p.description,
                p.price,
                p.category_id,
                p.created_at
            FROM
                ' . $this->table . ' p
            LEFT JOIN
                categories c ON p.category_id = c.id';
        
        //Prepare statement
        $stmt = $this->conn->prepare($query);

        //Execute query
        $stmt->execute();

        return $stmt;
    }

    public function read_using_category_id(){
        //Create query
        $query = 'SELECT
                c.name as category_name,
                p.id,
                p.name,
                p.description,
                p.price,
                p.category_id,
                p.created_at
            FROM
                ' . $this->table . ' p
            LEFT JOIN
                categories c ON p.category_id = c.id
            WHERE
                category_id = :category_id';

        //Prepare statement
        $stmt = $this->conn->prepare($query);

        //Bind data
        $stmt->bindParam(':category_id', $this->category_id);

        $stmt->execute();

        //$result = $stmt->get_result();
        //echo json_encode($result);

        return $stmt;
    }

    public function read_using_multiple_ids(){
        
        //$ids = str_repeat('?,', count($this->id) - 1) . '?';
        $query = 'SELECT
                    c.name as category_name,
                    p.id,
                    p.name,
                    p.description,
                    p.price,
                    p.category_id,
                    p.created_at
                FROM ' . $this->table . ' p
                LEFT JOIN categories c 
                ON p.category_id = c.id
                WHERE FIND_IN_SET(p.id, :array)';

        $stmt = $this->conn->prepare($query);

        $ids_string = implode(',', $this->id); // WITHOUT WHITESPACES BEFORE AND AFTER THE COMMA
        $stmt->bindParam('array', $ids_string);

        //$stmt->bindParam(':id', $id);
        $stmt->execute();

        return $stmt;
    }

    //Create product
    public function create(){
        
        //Create query
        $query = 'INSERT INTO ' .$this->table . '
                SET
                    name = :name,
                    description = :description,
                    price = :price,
                    category_id = :category_id';
        
        //Prepare statement
        $stmt = $this->conn->prepare($query);

        //Clean data
        $this->name = htmlspecialchars(strip_tags($this->name));
        $this->description = htmlspecialchars(strip_tags($this->description));
        $this->price = htmlspecialchars(strip_tags($this->price));
        $this->category_id = htmlspecialchars(strip_tags($this->category_id));
        //Bind data
        $stmt->bindParam(':name', $this->name);
        $stmt->bindParam(':description', $this->description);
        $stmt->bindParam(':price', $this->price);
        $stmt->bindParam(':category_id', $this->category_id);

        if($stmt->execute()){
            return true;
        }

        printf('Error: %s', $stmt->error);

        return false;

    }

    public function delete(){
        //Create query
        $query = 'DELETE FROM ' . $this->table . ' WHERE id= :id';

        //Prepare statement
        $stmt = $this->conn->prepare($query);

        //Clean data
        $this->id = htmlspecialchars(strip_tags($this->id));

        //Bind data
        $stmt->bindParam(':id', $this->id);

        if($stmt->execute()){
            return true;
        }

        return false;
    }

    public function productExists(){
        //Create query
        $query = 'SELECT * FROM ' . $this->table . ' WHERE name = :name';

        //Prepare statement
        $stmt = $this->conn->prepare($query);

        $this->name = htmlspecialchars(strip_tags($this->name));
        $stmt->bindParam(':name', $this->name);

        $stmt->execute();

        $num = $stmt->rowCount();

        if($num > 0){
            return true;
        }

        return false;
    }

    public function getLastId(){
        //Create query
        $query = 'SELECT id FROM ' . $this->table . ' ORDER BY id DESC LIMIT 1';

        $stmt = $this->conn->prepare($query);

        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        $lastId = $row['id'];

        return $lastId;
    }
}