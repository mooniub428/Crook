<?php
class CartItem{

    //db stuff
    private $conn;
    private $table = 'cartitems';

    //CartItem properties
    public $id;
    public $cart_id;
    public $product_id;
    public $quantity;

    //Constructor with db
    public function __construct($db){
        $this->conn = $db;
    }

    //Get cart using user's email
    public function read(){
        //Create query
        $query = 'SELECT *
                FROM ' . $this->table . '
                ORDER BY created_at DESC';

        //Prepare statement
        $stmt = $this->conn->prepare();

        //Execute query
        $stmt->execute();

        return $stmt;
    }

    //Get cartitems
    public function read_using_cart_id(){
        //Create query
        $query = 'SELECT ci.id, ci.product_id, p.name as product_name , ci.quantity, ci.cart_id
                FROM ' . $this->table . ' ci
                INNER JOIN products p 
                ON ci.product_id = p.id
                WHERE cart_id = :cart_id';

        //Prepare statement
        $stmt = $this->conn->prepare($query);

        $stmt->bindParam(':cart_id', $this->cart_id);

        //Execute query
        $stmt->execute();

        return $stmt;
    }

    /**
     * IMPORTANT: Cart items table has an UNIQUE restriction on two columns, cart_id and product_id
     * cause it's not possible to have two rows with same cart_id and product_id
     * So when we refer to "index" we mean these two columns.
     */
    public function delete_using_index(){

        $query = 'DELETE 
                FROM ' . $this->table . '
                WHERE cart_id = :cart_id AND product_id = :product_id';

        //Prepare statement
        $stmt = $this->conn->prepare($query);

        $stmt->bindParam(':cart_id', $this->cart_id);
        $stmt->bindParam(':product_id', $this->product_id);

        $stmt->execute();

        $success = $stmt->rowCount() > 0 ? true : false;
        return $success;
    }

    public function create_using_index(){

        $query = 'INSERT INTO ' . $this->table . '
                SET
                    cart_id = :cart_id,
                    product_id = :product_id,
                    quantity = :quantity';

        $stmt = $this->conn->prepare($query);

        $stmt->bindParam(':cart_id', $this->cart_id);
        $stmt->bindParam(':product_id', $this->product_id);
        $stmt->bindParam(':quantity', $this->quantity);

        $stmt->execute();

        $success = $stmt->rowCount() > 0 ? true : false;
        return $success;
    }

    public function update_using_index(){
        //Create query
        $query = 'UPDATE ' . $this->table . '
                SET 
                    quantity = :quantity
                WHERE
                    cart_id = :cart_id AND product_id = :product_id';
        
        $stmt = $this->conn->prepare($query);
        
        $stmt->bindParam(':cart_id', $this->cart_id);
        $stmt->bindParam(':product_id', $this->product_id);
        $stmt->bindParam(':quantity', $this->quantity);

        $stmt->execute();

        

        $success = $stmt->rowCount() > 0 ? true : false;
        return $success;
        
    }

}
?>