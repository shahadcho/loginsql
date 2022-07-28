package com.example.loginsql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    EditText name,consumer_ID;
    Button btnlogin;
    DBHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText)findViewById(R.id.full_name);
        consumer_ID = (EditText)findViewById(R.id.consumer_no);

        btnlogin = (Button) findViewById(R.id.login);

        myDB = new DBHelper(this);


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = name.getText().toString();
                String id = consumer_ID.getText().toString();

                if (user.equals("") || id.equals(""))
                {
                    Toast.makeText(MainActivity.this, "Fill all the fields", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    boolean insert = myDB.insertData(user,id);
                    if(insert == true)
                    {
                        Toast.makeText(MainActivity.this, "Your Data added into Database", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),CalculatorActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Couldn't add your Data into Database", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
