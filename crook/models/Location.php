<?php

class Location{
    //DB stuff
    private $conn;
    private $table = 'locations';

    //Product properties
    public $id;
    public $user_id;
    public $latitude;
    public $longitude;
    public $speed;
    public $time_remain;

    /* Unipi Coords */
    const storeLatitude = 37.941737;
    const storeLongitude = 23.652762;

    //Constructor
    public function __construct($db){
        $this->conn = $db;
    }

    public function read(){
        //Create query
        $query = 'SELECT * 
                FROM ' . $this->table . '
                ORDER BY user_id ASC';

        //Prepare statement
        $stmt = $this->conn->prepare($query);

        $stmt->execute();

        return $stmt;
    }

    public function read_asc_time(){
        //Create query
        $query = 'SELECT u.id uid, u.email, u.firstname, u.lastname, l.id loc_id, time_remain
                 FROM ' . $this->table . ' l
                 INNER JOIN users u
                 ON l.user_id = u.id
                 ORDER BY time_remain ASC';

        $stmt = $this->conn->prepare($query);

        $stmt->execute();

        return $stmt;
    }

    public function create(){
        //Create query
        $query = 'INSERT INTO ' .$this->table . '
                SET
                    user_id = :user_id,
                    latitude = :latitude,
                    longitude = :longitude,
                    speed = :speed,
                    time_remain = :time_remain';
        
        //Prepare statement
        $stmt = $this->conn->prepare($query);

        //Clean data (not really needed)
        $this->latitude = htmlspecialchars(strip_tags($this->latitude));
        $this->longitude = htmlspecialchars(strip_tags($this->longitude));
        $this->speed = htmlspecialchars(strip_tags($this->speed));

        //Bind data
        $stmt->bindParam(':user_id', $this->user_id);
        $stmt->bindParam(':latitude', $this->latitude);
        $stmt->bindParam(':longitude', $this->longitude);
        $stmt->bindParam(':speed', $this->speed);
        $stmt->bindParam(':time_remain', $this->time_remain);

        if($stmt->execute()){
            return true;
        }

        printf('Error: %s', $stmt->error);

        return false;
        
    }

    public function update_using_userId(){
        //Create query
        $query = 'UPDATE ' . $this->table . '
                SET 
                    latitude = :latitude,
                    longitude = :longitude,
                    speed = :speed,
                    time_remain = :time_remain
                WHERE
                    user_id = :user_id';
        
        $stmt = $this->conn->prepare($query);
        
        $stmt->bindParam(':user_id', $this->user_id);
        $stmt->bindParam(':latitude', $this->latitude);
        $stmt->bindParam(':longitude', $this->longitude);
        $stmt->bindParam(':speed', $this->speed);
        $stmt->bindParam(':time_remain', $this->time_remain);

        if($stmt->execute()){
            return true;
        }

        printf('Error: %s', $stmt->error);

        return false;
    }

    public function locationExists(){
        //Create query
        $query = 'SELECT * FROM ' . $this->table . ' WHERE user_id = :user_id';

        //Prepare statement
        $stmt = $this->conn->prepare($query);

        $stmt->bindParam(':user_id', $this->user_id);

        if($stmt->execute());

        $num = $stmt->rowCount();

        if($num > 0){
            return true;
        }

        return false;
    }

    public function calc_distance($unit) {

        $theta = $this->longitude - self::storeLongitude;
        $dist = sin(deg2rad($this->latitude)) * sin(deg2rad(self::storeLatitude)) +  cos(deg2rad($this->latitude)) * cos(deg2rad(self::storeLatitude)) * cos(deg2rad($theta));
        $dist = acos($dist);
        $dist = rad2deg($dist);
        $miles = $dist * 60 * 1.1515;
        $unit = strtoupper($unit);
      
        if ($unit == "K") {
            return ($miles * 1.609344);
        } else if ($unit == "N") {
            return ($miles * 0.8684);
        } else {
            return $miles;
        }
      }
}

?>