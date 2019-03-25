<?php

class Database{
    //db params
    private $host_ip = '192.168.1.65';
    private $host = 'localhost';
    private $db_name = 'crook_db';
    private $username = 'root';
    private $password = '';
    private $conn;

    //db connect
    public function connect(){
        $this->conn = null;

        try{

            $this->conn = new PDO('mysql:host=' . $this->host . ';dbname=' . $this->db_name,
            $this->username, $this->password);
            $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        } catch(PDOException $e){
            echo 'Connection error: ' . $e->getMessage();
        }
        return $this->conn;
    }

    /**
     * Android device needs a real IP and not localhost 
     * in order to retrieve the images from server
     */ 
    public function get_host_ip(){
        return $this->host_ip;
    }
}
?>