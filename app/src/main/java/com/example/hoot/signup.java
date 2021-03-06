package com.example.hoot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import android.widget.RadioButton;

import android.widget.ImageButton;

import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class signup extends AppCompatActivity {

    private Button BTNsignup;
    private Button BTNsignin;
    private EditText ETemail;
    private EditText ETfirstname;
    private EditText ETpassword;
    private FirebaseAuth mAuth;
    private RadioButton RBwise;
    private RadioButton RByoung;
    private EditText ETaboutme;
    private Button BTNchooseImageSignUp;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public static String capitalise(String value) {

        char[] array = value.toCharArray();
        array[0] = Character.toUpperCase(array[0]);
        return new String(array);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        BTNsignup = findViewById(R.id.BTNsignup);
        BTNsignin = findViewById(R.id.BTNsignin);
        ETfirstname = findViewById( R.id.ETfirstname);
        ETemail = findViewById(R.id.ETemail);
        ETpassword = findViewById(R.id.ETpassword);
        ETaboutme = findViewById(R.id.ETaboutme);
        BTNchooseImageSignUp = findViewById(R.id.BTNchooseImageSignUp);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        RBwise = findViewById(R.id.RBwise);
        RByoung = findViewById(R.id.RByoung);

        BTNsignin = findViewById(R.id.BTNsignin);
        BTNsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this, MainActivity.class));
            }
        });


        BTNchooseImageSignUp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation= AnimationUtils.loadAnimation(signup.this,R.anim.fadein);
                BTNchooseImageSignUp.startAnimation(animation);
                chooseImage();
            }
        });

        BTNsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation= AnimationUtils.loadAnimation(signup.this,R.anim.fadein);
                BTNsignup.startAnimation(animation);
                mAuth = FirebaseAuth.getInstance();

                mAuth.createUserWithEmailAndPassword(ETemail.getText().toString(), ETpassword.getText().toString())
                        .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(signup.this, "Successful", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String userid = user.getUid();
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                                    DatabaseReference myRef = database.getReference().child("users").child(RBwise.isChecked() ? "Wise" : "Young").child(userid);
                                    myRef.child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    myRef.child("name").setValue(capitalise(ETfirstname.getText().toString()));
                                    myRef.child("aboutme").setValue(ETaboutme.getText().toString());
                                    uploadImage(userid);
                                    startActivity(new Intent(signup.this, InterestsActivity.class));
                            } else {
                                    Toast.makeText(signup.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                                }

            };
        });
            };


        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
        }
    }

    private void uploadImage(String userid) {

        if(filePath != null)
        {
            StorageReference ref = storageReference.child("images").child(userid);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.child("images/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    System.out.println(uri);
                                    FirebaseDatabase.getInstance().getReference().child("users").child(RBwise.isChecked() ? "Wise" : "Young").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("image").setValue(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(signup.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                        }
                    });
        }
        else if (RBwise.isChecked()) {
            storageReference.child("DefaultImages").child("WiseOwlImage.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    FirebaseDatabase.getInstance().getReference().child("users").child(RBwise.isChecked() ? "Wise" : "Young").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("image").setValue(uri.toString());
                }
            });
        }
        else {
            storageReference.child("DefaultImages").child("YoungOwlImage.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    FirebaseDatabase.getInstance().getReference().child("users").child(RBwise.isChecked() ? "Wise" : "Young").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("image").setValue(uri.toString());

                }
            });


        }
    }

}
