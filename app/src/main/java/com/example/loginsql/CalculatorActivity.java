package com.example.loginsql;

import static android.Manifest.permission.CAMERA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class CalculatorActivity extends AppCompatActivity {

    TextView used_unit,total_cost,days_used;
    Button scan,submit,detect;
    ImageView kseb,online;
    EditText current_unit,previous_unit,days_use;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap;
    int cost_1 =0,cost =0;
    float uses;
    static int unit_cost,cost_one = 0,fixed_charge = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        //image_views
        online = findViewById(R.id.online);
//        kseb = findViewById(R.id.c_imageView);
        //text_views
        used_unit = findViewById(R.id.used_unit);
        total_cost = findViewById(R.id.total_cost);
        days_used = findViewById(R.id.days);
        //Buttons
        scan = findViewById(R.id.btn_scan);
        submit = findViewById(R.id.btn_submit);
        detect = findViewById(R.id.btn_detect);
        //Edit_texts
        current_unit = findViewById(R.id.c_unit);
        previous_unit = findViewById(R.id.p_unit);
        days_use = findViewById(R.id.days);



        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://wss.kseb.in/selfservices/quickpay");
            }

            private void gotoUrl(String s) {
                Uri uri = Uri.parse(s);
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curren = current_unit.getText().toString();
                String previou = previous_unit.getText().toString();
                String days_used = days_use.getText().toString();

                if (curren.equals("") || previou.equals("")) {
                    // Toast.makeText(MainActivity.this, "Fill Both Fields", Toast.LENGTH_LONG).show();
                } else {
                    float current = Float.parseFloat(curren);
                    float previous = Float.parseFloat(previou);
                    float dayss = Float.parseFloat(days_used);

                    float uses = current - previous;
                    TextView used_units = findViewById(R.id.used_unit);
                    used_units.setText("Used Units :  " + uses);
                    TextView total_cost = findViewById(R.id.total_cost);


                    float one_day = uses / dayss;

                    float days_rem = dayss - 30;
                    float used_one = one_day * 30;
                    float balance = days_rem * one_day;
                    int telescopic, final_cost;
                    float duty;

                    // Main poit=new Main();


                    if (used_one > 250) {
                        telescopic = 0;
                    } else {
                        telescopic = 1;
                    }

                    if (telescopic == 1) {
                        tele(used_one);
                    } else {
                        non(used_one);
                    }

                    if (telescopic == 1) {
                        tele(balance);
                    } else {
                        non(balance);
                    }



                    fixed(uses);
                    final_cost = unit_cost + cost_one;
                    duty = 0.1f * final_cost;
                    total_cost.setText( "\n Fixed Charge :\t" + fixed_charge + "\n Meter Rent :\t35\nEnergy Charges :\t"+ final_cost+"\nDuty :\t"+duty);


                }

            }


        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkpermission()){
                    captureImage();


                }
                else{
                    requestpermission();
                }


            }
        });
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectText();
            }
        });
    }
     private boolean checkpermission(){
        int camerapermission = ContextCompat.checkSelfPermission(getApplicationContext(),CAMERA);
        return camerapermission == PackageManager.PERMISSION_GRANTED;

     }
     private void requestpermission(){
        int PERMISSION_CODE =200;
         ActivityCompat.requestPermissions(this,new String[]{CAMERA},PERMISSION_CODE);

     }

     private void captureImage(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePicture,REQUEST_IMAGE_CAPTURE);
        }

     }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0){
            boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (cameraPermission){
                Toast.makeText(this, "Permission Granted...", Toast.LENGTH_SHORT).show();
                captureImage();
            }
            else{
                Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE || resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

        }
    }
    private void detectText() {
        //TextRecognitionOptions.DEFAULT_OPTIONS
        InputImage image = InputImage.fromBitmap(imageBitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                current_unit = findViewById(R.id.c_unit);

                StringBuilder result = new StringBuilder();
                for (Text.TextBlock block : text.getTextBlocks()) {
                    String blocktext = block.getText();
                    Point[] blockCornerPoint = block.getCornerPoints();
                    Rect blockFrame = block.getBoundingBox();
                    for (Text.Line line : block.getLines()) {
                        String lineText = line.getText();
                        Point[] lineCornerPoint = line.getCornerPoints();
                        Rect linRect = line.getBoundingBox();
                        for (Text.Element element : line.getElements()) {
                            String elementText = element.getText();
                            result.append(elementText);
                        }
                        current_unit.setText(blocktext);


                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CalculatorActivity.this, "Failed to Detect Text from Image.. Try Again" + e.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });
    }
        public static void tele ( float used_one){

        int a = 0, b = 0, c = 0, d = 0, f = 0;
        if (used_one - 50 >= 0) {
        a = (int) (50 * 3.15);
        used_one = used_one - 50;
        } else {
        a = (int) (used_one * 3.15);
        used_one = 0;
        }
        if (used_one - 50 >= 0) {
        b = (int) (50 * 3.70);
        used_one = used_one - 50;
        } else {
        b = (int) (used_one * 3.70);
        used_one = 0;
        }
        if (used_one - 50 >= 0) {
        c = (int) (50 * 4.80);
        used_one = used_one - 50;
        } else {
        c = (int) (used_one * 3.15);
        used_one = 0;
        }
        if (used_one - 50 >= 0) {
        d = (int) (50 * 6.40);
        used_one = used_one - 50;
        } else {
        d = (int) (used_one * 6.40);
        used_one = 0;
        }
        if (used_one - 50 >= 0) {
        f = (int) (50 * 7.60);
        used_one = used_one - 50;
        } else {
        f = (int) (used_one * 7.60);
        used_one = 0;
        }

        cost_one = (int) cost_one + a + b + c + d + f;


        }

        public static void fixed ( float num){

            if (num <= 200) {
                fixed_charge = 180;
            } else if (num > 200 & num <= 500) {
                fixed_charge = 200;
            } else if (num > 500 & num <= 700) {
                fixed_charge = 220;
            } else if (num > 700 & num <= 800) {
                fixed_charge = 240;
            } else if (num > 800 & num <= 1000) {
                fixed_charge = 260;
            } else {
                fixed_charge = 300;
            }
            System.out.println("fixed charge is :" + fixed_charge);

        }

        public static void non ( float used_one){

            if (used_one < 300) {
                unit_cost = (int) (used_one * 5.80);
            } else if (used_one < 350) {
                unit_cost = (int) (used_one * 6.60);
            } else if (used_one < 400) {
                unit_cost = (int) (used_one * 6.90);
            } else if (used_one < 500) {
                unit_cost = (int) (used_one * 7.10);
            } else {
                unit_cost = (int) (used_one * 7.90);
            }
        }



    }
