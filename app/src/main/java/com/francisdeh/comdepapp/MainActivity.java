package com.francisdeh.comdepapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tapadoo.alerter.Alerter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;
    ImageButton imageButton;
    EditText nameEditText;
    Button uploadBtn, uploadedFilesBtn;
    private ProgressDialog progressDialog;
    Spinner levelSpinner;
    Uri croppedImageUri;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;


    private final int PICK_IMAGE_TAG = 1;
    private static final int MY_PERMISSION = 225;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Comdep App");
        setSupportActionBar(toolbar);

        imageButton = (ImageButton)findViewById(R.id.imageBtn);
        nameEditText = (EditText)findViewById(R.id.name_edit_text);

        uploadBtn = (Button)findViewById(R.id.uploadBtn);
        uploadedFilesBtn = findViewById(R.id.viewUploadedFilesBtn);
        //only display to admins
        uploadedFilesBtn.setVisibility(View.INVISIBLE);
        progressDialog = new ProgressDialog(this);

        levelSpinner = findViewById(R.id.level_spinner);

        uploadBtn.setVisibility(View.GONE);

        setupSpinnerData();

        firebaseAuth =  FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Members");

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent intent = new Intent(MainActivity.this, Register.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    String email = firebaseAuth.getCurrentUser().getEmail();
                    if (email != null) {
                        if (email.equals(getString(R.string.comdep_admin_email))) {
                            //if email equals the admin, show additional button buttons

                            uploadedFilesBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        };

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validate to ensure the edit text fields are not empty
                //image should not be null
                if(TextUtils.isEmpty(nameEditText.getText().toString().trim()) ||  imageButton == null || TextUtils.isEmpty(String.valueOf(levelSpinner.getSelectedItem())) ){
                    Alerter.create(MainActivity.this)
                            .setTitle("Error:")
                            .setText("Provide all the necessary details")
                            .setBackgroundColorInt(Color.RED)
                            .enableSwipeToDismiss()
                            .show();



                } else {



                    progressDialog.setMessage("Uploading information..");
                    progressDialog.show();

                        StorageReference filePath = storageReference.child(String.valueOf(levelSpinner.getSelectedItem())).child(nameEditText.getText().toString().trim());
                        filePath.putFile(croppedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUri = taskSnapshot.getDownloadUrl();
                                DatabaseReference uploadReference = databaseReference.push();
                                uploadReference.child("Name").setValue(nameEditText.getText().toString().trim());
                                uploadReference.child("Image").setValue(downloadUri.toString());
                                uploadReference.child("Level").setValue(String.valueOf(levelSpinner.getSelectedItem()));

                                progressDialog.dismiss();

                                Alerter.create(MainActivity.this)
                                        .setTitle("Success:")
                                        .setText("Your information has been uploaded successfully! You may log out!")
                                        .setBackgroundColorInt(Color.BLUE)
                                        .enableSwipeToDismiss()
                                        .show();

                                uploadBtn.setVisibility(View.GONE);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                progressDialog.dismiss();

                                Alerter.create(MainActivity.this)
                                        .setTitle("Error:")
                                        .setText("Please Retry Submission. " + e.getMessage())
                                        .setBackgroundColorInt(Color.RED)
                                        .enableSwipeToDismiss()
                                        .show();

                                uploadBtn.setVisibility(View.VISIBLE);

                            }
                        });

                    }


            }
        });

        uploadedFilesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open another activity showing the files
                startActivity(new Intent(MainActivity.this, Images.class));
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:set permission for Android version 6.0 and greater
                //TODO:set permission in Android Manifest file

               checkPermission();

            }
        });


        //TODO:receive selected image and upload to fire base


    }

    private void setupSpinnerData() {

        List<String> list  = new ArrayList<>();
        list.add("100");
        list.add("200");
        list.add("300");
        list.add("400");
        list.add("500");
        list.add("600");
        list.add("700");
        list.add("800");
        list.add("Alumni");
        list.add("Patron");
        list.add("None");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        levelSpinner.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.logout){
            firebaseAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_TAG && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
                //enable image crop
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setMaxCropResultSize(1500, 1500)
                    .start(this);

        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){

                 croppedImageUri = activityResult.getUri();
                imageButton.setImageURI(croppedImageUri);
                
                //set the upload button visible
                uploadBtn.setVisibility(View.VISIBLE);

            } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = activityResult.getError();
                Alerter.create(MainActivity.this)
                        .setTitle("Error:")
                        .setText(error.getMessage())
                        .setBackgroundColorInt(Color.RED)
                        .enableSwipeToDismiss()
                        .show();
            }
        }


    }

    //this function checks for permission
    private void checkPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION);
            } else {
                pickImage();
            }
        } else {
            pickImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         if(requestCode == MY_PERMISSION){
             if(grantResults.length > 0){
                 if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                     pickImage();
                 } else {
                     Alerter.create(this)
                             .setTitle("Info:")
                             .setText("You have denied permission to select a file. Accept permission and you can select a file. ")
                             .setBackgroundColorInt(Color.GRAY)
                             .enableSwipeToDismiss()
                             .show();
                 }
             }
         }
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_TAG);
    }
}
