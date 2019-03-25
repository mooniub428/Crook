package com.example.stath.crookclient.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stath.crookclient.Connection.Connect;
import com.example.stath.crookclient.Connection.Post_Request_Handler;
import com.example.stath.crookclient.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private EditText inputFirstname;
    private EditText inputLastname;
    private EditText inputEmail;
    private EditText inputPassword;
    private Button btnSignUp;

    private ProgressDialog pDialog;

    private static final String url_create_user = "http://" + Connect.host + "/crook/api/user/create.php";
    private String server_message = "default";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        inputFirstname = findViewById(R.id.inputFirstname);
        inputLastname = findViewById(R.id.inputLastname);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });


    }

    public void signUp(){

        if(!validate()){
            onSignUpFailed();
            return;
        }

        btnSignUp.setEnabled(false);
        new RegisterUser().execute();

    }

    public void onSignUpSuccess(){
        btnSignUp.setEnabled(true);
        setResult(RESULT_OK, null);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(intent);


    }
    public void onSignUpFailed(){
        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
        btnSignUp.setEnabled(true);

    }


    public boolean validate(){

        boolean valid = true;

        String firstname = inputFirstname.getText().toString();
        String lastname = inputLastname.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if(firstname.isEmpty() || firstname.length() < 3){
            inputFirstname.setError("at least 3 characters");
            valid = false;
        } else {
            inputFirstname.setError(null);
        }

        if(lastname.isEmpty() || lastname.length() < 3){
            inputLastname.setError("at least 3 characters");
            valid = false;
        } else {
            inputLastname.setError(null);
        }

        if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            inputEmail.setError("enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if(password.isEmpty() || password.length() < 3 || password.length() > 10){
            inputPassword.setError("between 3 and 10 alphanumeric characters");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        return valid;

    }



    class RegisterUser extends AsyncTask<String, Void, String>{

        private int SUCCESSFULL_SIGNUP = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(SignUpActivity.this, R.style.AppTheme);
            pDialog.setMessage("Creating account. Please wait...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(SUCCESSFULL_SIGNUP == 1)
                        onSignUpSuccess();
                    else
                        onSignUpFailed();

                    Toast.makeText(getApplicationContext(), server_message, Toast.LENGTH_SHORT).show();

                }
            });



        }

        @Override
        protected String doInBackground(String... strings) {

            String email = inputEmail.getText().toString();
            String firstname = inputFirstname.getText().toString();
            String lastname = inputLastname.getText().toString();
            String password = inputPassword.getText().toString();

            SUCCESSFULL_SIGNUP = createUser(email, firstname, lastname, password);

            return null;
        }

        /**
         * Register user on database
         * props[0]=>email, props[1]=>firstname, props[2]=>lastname, props[3]=>password
         * @param props
         * @return server_message
         */
        public int createUser(String... props){

            JSONObject jsonObject = null;
            String response = "";
            int success = 0;


            HashMap<String, String> params = new HashMap<>();
            params.put("email", props[0]);
            params.put("firstname", props[1]);
            params.put("lastname", props[2]);
            params.put("password", props[3]);

            Post_Request_Handler post = new Post_Request_Handler();
            response = post.performPostCall(url_create_user, params, false, null);

            Log.d(TAG, "createUser: " + response);

            if(response != null && !response.isEmpty()){

                try {
                    jsonObject = new JSONObject(response);
                    success = jsonObject.getInt("success");
                    server_message = jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getApplicationContext(), "No response from server", Toast.LENGTH_SHORT).show();
            }


            return success;

        }
    }


}
