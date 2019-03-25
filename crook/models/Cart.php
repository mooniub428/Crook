<?php

class Cart{

    //db stuff
    private $conn;
    private $table = 'carts';

    //Cart properties
    public $id = 5;
    public $user_id;

    //Constructor with db
    public function __construct($db){
        $this->conn = $db;
    }

    //Get user's cart using his email
    public function read_using_email($email){
        //Create query
        $query = 'SELECT u.id user_id, u.email, c.id cart_id
                FROM carts c 
                LEFT JOIN users u 
                ON c.user_id = u.id
                WHERE email = :email';

        //Prepare statement
        $stmt = $this->conn->prepare($query);

        //Bind parameter
        $stmt->bindParam(':email', $email);

        //Execute query and fetch row
        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        $this->user_id = $row['user_id'];
        $this->id = $row['cart_id'];
        
    }

    

    


}