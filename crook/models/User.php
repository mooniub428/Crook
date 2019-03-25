<?php

class User{

    //db stuff
    private $conn;
    private $table = 'users';

    //user properties
    public $id;
    public $email;
    public $firstname;
    public $lastname;
    public $password;
    public $created_at;
    public $updated_at;

    //Constructor with db
    public function __construct($db){
        $this->conn = $db;
    }

    //Get all Users
    public function read(){
        //Create query
        $query = 'SELECT 
                id, 
                email, 
                firstname, 
                lastname, 
                created_at 
            FROM 
            ' . $this->table . ' 
            ORDER BY 
                created_at DESC';

        //Prepare statement
        $stmt = $this->conn->prepare($query);

        //Execute query
        $stmt->execute();

        return $stmt;
    }

    //Get single user
    public function read_user_using_ID(){
        //Create query
        $query = 'SELECT
                id,
                email,
                firstname,
                lastname,
                created_at
            FROM
            ' . $this->table . '
            WHERE 
                id = :id
            LIMIT 0,1';

        //Prepare statement
        $stmt = $this->conn->prepare($query);

        //Bind ID
        $stmt->bindParam(':id', $this->id);

        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        //Set properties
        $this->email = $row['email'];
        $this->firstname = $row['firstname'];
        $this->lastname = $row['lastname'];
        $this->created_at = $row['created_at'];

    }

    //Check for login
    public function read_using_email(){
        //Create query
        $query = 'SELECT
                id,
                email,
                firstname,
                lastname,
                created_at
            FROM
            ' . $this->table . '
            WHERE 
                email = :email
            LIMIT 0,1';

        //Prepare statement
        $stmt = $this->conn->prepare($query);

        //Bind email
        $stmt->bindParam(':email', $this->email);

        $stmt->execute();

        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        //Set properties
        $this->id = $row['id'];
        $this->email = $row['email'];
        $this->firstname = $row['firstname'];
        $this->lastname = $row['lastname'];
        $this->created_at = $row['created_at'];
        
    }

    //fetching user by email
    public function checkLogin(){
        
        $query = 'SELECT 
                    id,
                    firstname,
                    email,
                    password 
                FROM 
                ' . $this->table . ' 
                WHERE 
                    email = :email';
        
        $stmt = $this->conn->prepare($query);

        $stmt->bindParam(':email', $this->email);

        $stmt->execute();

        return $stmt;

        
    }

    //update user
    public function update_using_email(){
        //Create query
        $query = 'UPDATE ' . $this->table . '
                SET 
                    firstname = :firstname,
                    lastname = :lastname
                WHERE
                    email = :email';
        
        //Prepare statement
        $stmt = $this->conn->prepare($query);

        $stmt->bindParam(':email', $this->email);
        $stmt->bindParam(':firstname', $this->firstname);
        $stmt->bindParam(':lastname', $this->lastname);

        if($stmt->execute()){
            return true;
        }

        printf("Error: %s", $stmt->error);

        return false;

    }

    //create new user
    public function create(){

        $query = 'INSERT INTO 
                    ' . $this->table . '
                SET
                    email = :email,
                    firstname = :firstname,
                    lastname = :lastname,
                    password = :password';

        //Prepare statement
        $stmt = $this->conn->prepare($query);

        //Clean data
        $this->email = htmlspecialchars(strip_tags($this->email));
        $this->firstname = htmlspecialchars(strip_tags($this->firstname));
        $this->lastname = htmlspecialchars(strip_tags($this->lastname));
        $this->password = htmlspecialchars(strip_tags($this->password));

        //Bind data
        $stmt->bindParam(':email', $this->email);
        $stmt->bindParam(':firstname', $this->firstname);
        $stmt->bindParam(':lastname', $this->lastname);
        $stmt->bindParam(':password', $this->password);

        //Execute query
        if($stmt->execute()){
            return true;
        }

        //print error if something goes wrong
        printf("Error: %s. \n", $stmt->error);

        return false;
                    
    }



}