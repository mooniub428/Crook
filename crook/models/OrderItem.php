<?php

class OrderItem{
    //DB & conn stuff
    private $conn;
    private $table = 'orderitems';

    //Order item properties
    public $id;
    public $order_id;
    public $product_id;
    public $quantity;

    //Constructor
    public function __construct($db){
        $this->conn = $db;
    }

    public function read(){
        $query = 'SELECT *
                FROM
                ' . $this->table . '
                ';

        //prepare statement
        $stmt = $this->conn->prepare($query);

        //execute statement
        $stmt->execute();

        return $stmt;
    }

    public function create(){
        $query = 'INSERT INTO 
                ' . $this->table . '
                SET
                    order_id = :order_id,
                    product_id = :product_id,
                    quantity = :quantity';

        $stmt = $this->conn->prepare($query);

        //Clean data
        $this->order_id = htmlspecialchars(strip_tags($this->order_id));
        $this->product_id = htmlspecialchars(strip_tags($this->product_id));
        $this->quantity = htmlspecialchars(strip_tags($this->quantity));

        //Bind data
        $stmt->bindParam(':order_id', $this->order_id);
        $stmt->bindParam(':product_id', $this->product_id);
        $stmt->bindParam(':quantity', $this->quantity);

        if($stmt->execute()){
            return true;
        }

        printf('Error: %s', $stmt->error);

        return false;
    }
}

?>