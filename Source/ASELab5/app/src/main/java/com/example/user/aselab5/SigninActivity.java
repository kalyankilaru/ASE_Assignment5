package com.example.user.aselab5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SigninActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
    }

    public void checkCredentials(View v)
    {
        EditText usernameCtrl = (EditText)findViewById(R.id.text_Username);
        EditText passwordCtrl = (EditText) findViewById(R.id.text_Password);
        TextView errorText = (TextView)findViewById(R.id.lbl_Error);
        String userName = usernameCtrl.getText().toString();
        String password = passwordCtrl.getText().toString();

        boolean validationFlag = false;
        //Verify if the username and password are not empty.
        if(!userName.isEmpty() && !password.isEmpty()) {
            if(userName.equals("Admin") && password.equals("Admin")) {
                validationFlag = true;

            }
        }
        if(!validationFlag)
        {
            errorText.setVisibility(View.VISIBLE);
        }
        else
        {
            //This code redirects  from login page to the home page.
            Intent redirect = new Intent(this, HomeActivity.class);
            startActivity(redirect);
        }

    }


    public void signUp(View v)
    {
        //This code redirects the from login page to the Sign Up page.
        Intent redirect = new Intent(this,MainActivity.class);
        startActivity(redirect);
    }
}
