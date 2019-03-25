package com.example.stath.crookclient.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.stath.crookclient.Connection.Connect;
import com.example.stath.crookclient.Connection.Get_Request_Handler;
import com.example.stath.crookclient.R;
import com.example.stath.crookclient.UserStatus;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;
    private Button btnSignIn;
    private TextView goToSignUp;

    private String email;
    private String password;
    private String firstname;

    private ProgressDialog pDialog;

    private String url_check_login;
    private String server_message = "Error";

    private static final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        inputEmail = findViewById(R.id.inputEmailSignIn);
        inputPassword = findViewById(R.id.inputPasswordSignIn);
        btnSignIn = findViewById(R.id.btnSignIn);
        goToSignUp = findViewById(R.id.goToSignUp);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();

                if(validInputs())
                    new isLoginValid().execute();
            }
        });

        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), SignUpActivity.class);
                finish();
                startActivity(i);
            }
        });

    }

    /**
     *  Check if User's inputs are valid
     */
    private boolean validInputs(){

        boolean valid = true;

        if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            inputEmail.setError("enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if(password.isEmpty() || password.length() < 3){
            inputPassword.setError("passwords consist of at least 3 characters");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        return valid;
    }


    /**
     * Check if login is Valid and return the firstname
     */
    class isLoginValid extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignInActivity.this);
            pDialog.setMessage("Authenticating...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority(Connect.host)
                    .appendPath("crook")
                    .appendPath("api")
                    .appendPath("user")
                    .appendPath("check_login.php")
                    .appendQueryParameter("email", email)
                    .appendQueryParameter("password", password);

            url_check_login = builder.build().toString();
            //url_check_login = "http://192.168.1.66/crook/api/user/check_login.php?email=stathiskaps@gmail.com&password=123";
            try{

                Get_Request_Handler get = new Get_Request_Handler();
                String response = get.performGetCall(url_check_login);

                Log.d(TAG, "HELLOOOOOOOOOOO " + response);

                //Convert result string to jsonObject
                if(response != null && !response.isEmpty()){
                    JSONObject jsonObject = new JSONObject(response);
                    firstname = jsonObject.getString("firstname");
                    server_message = jsonObject.getString("message");

                    Log.d(TAG, server_message);

                    return firstname;

                }

            }  catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();

            /* Login */
            if(firstname != null && !firstname.isEmpty()) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                UserStatus.setUserEmail(SignInActivity.this, email);
                UserStatus.setUserFirstname(SignInActivity.this, firstname);
                finish();
                startActivity(i);
            }

            Log.d(TAG, "onPostExecute: " + server_message);

        }


    }



}
