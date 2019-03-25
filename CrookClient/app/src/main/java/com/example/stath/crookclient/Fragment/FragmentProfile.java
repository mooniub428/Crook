package com.example.stath.crookclient.Fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stath.crookclient.Activity.MainActivity;
import com.example.stath.crookclient.Connection.Connect;
import com.example.stath.crookclient.Connection.Get_Request_Handler;
import com.example.stath.crookclient.Connection.Post_Request_Handler;
import com.example.stath.crookclient.R;
import com.example.stath.crookclient.UserStatus;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

public class FragmentProfile extends Fragment {

    private static final String TAG = "FragmentProfile";
    private String url_get_user;
    private static final String url_update_profile = "http://" + Connect.host + "/crook/api/user/update_using_email.php";

    private View view;

    private ProgressDialog pDialog;

    private String firstname, lastname;
    private EditText profileFirstname, profileLastname;
    private TextView profileEmail;
    private Button updateProfile;

    private String server_message = "default";

    public FragmentProfile(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment, container, false);

        profileFirstname = view.findViewById(R.id.profileFirstname);
        profileLastname = view.findViewById(R.id.profileLastname);
        profileEmail = view.findViewById(R.id.profileEmail);
        updateProfile = view.findViewById(R.id.updateProfile);

        new LoadUser().execute(UserStatus.getUserEmail(getActivity()));

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateUser().execute();
            }
        });

        return view;
    }

    class LoadUser extends AsyncTask<String, Void , Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading product categories. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            pDialog.dismiss();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    profileEmail.setText(UserStatus.getUserEmail(getActivity()));
                    profileFirstname.setText(firstname);
                    profileLastname.setText(lastname);
                }
            });

        }

        @Override
        protected Boolean doInBackground(String... strings) {

            String email = strings[0];
            boolean success = false;

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority(Connect.host)
                    .appendPath("crook")
                    .appendPath("api")
                    .appendPath("user")
                    .appendPath("read_using_email.php")
                    .appendQueryParameter("email", email);

            Get_Request_Handler get = new Get_Request_Handler();
            String result = get.performGetCall(builder.build().toString());

            Log.d(TAG, "result=" + result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                firstname = jsonObject.getString("firstname");
                lastname = jsonObject.getString("lastname");


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class UpdateUser extends AsyncTask<Void, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Updating profile...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), server_message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        protected String doInBackground(Void... voids) {

            firstname = profileFirstname.getText().toString();
            lastname = profileLastname.getText().toString();

            HashMap<String, String > params = new HashMap<>();
            params.put("email", UserStatus.getUserEmail(getActivity()));
            params.put("firstname", firstname);
            params.put("lastname", lastname);

            Post_Request_Handler post = new Post_Request_Handler();
            String result = post.performPostCall(url_update_profile, params, false, null);

            try {
                JSONObject jsonObject = new JSONObject(result);

                int success = jsonObject.getInt("success");
                server_message = jsonObject.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;

        }
    }
}
