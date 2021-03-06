package com.example.hoot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RequestContactPageActivity extends AppCompatActivity {

    private ImageView IVpictureRCPage;
    private TextView TVprofileNameRCPage;
    private TextView TVwiseOrYoungRCPage;
    private TextView TVAboutMeRCPage;
    private DatabaseReference databaseReference;
    private TextView ETinteretsListRCPage;
    private List<String> interestList;
    private ImageButton BTNProfileRCP;
    private ImageButton BTNFeedRCP;
    private ImageButton BTNLogoutRCP;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requestcontactpage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TVprofileNameRCPage = findViewById(R.id.TVprofileNameRCPage);
        TVAboutMeRCPage = findViewById(R.id.TVAboutMeRCPage);
        TVwiseOrYoungRCPage = findViewById(R.id.TVwiseOrYoungRCPage);
        IVpictureRCPage = findViewById(R.id.IVpictureRCPage);
        ETinteretsListRCPage = findViewById(R.id.ETinteretsListRCPage);
        mAuth = FirebaseAuth.getInstance();


        BTNProfileRCP = findViewById(R.id.BTNProfileRCP);
        BTNProfileRCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation= AnimationUtils.loadAnimation(RequestContactPageActivity.this,R.anim.fadein);
                BTNProfileRCP.startAnimation(animation);
                startActivity(new Intent(RequestContactPageActivity.this, ProfileActivity.class));
            }
        });

        BTNFeedRCP = findViewById(R.id.BTNFeedRCP);
        BTNFeedRCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation= AnimationUtils.loadAnimation(RequestContactPageActivity.this,R.anim.fadein);
                BTNFeedRCP.startAnimation(animation);
                startActivity(new Intent(RequestContactPageActivity.this, FeedActivity.class));

            }
        });

        BTNLogoutRCP = findViewById(R.id.BTNLogoutRCP);
        BTNLogoutRCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation= AnimationUtils.loadAnimation(RequestContactPageActivity.this,R.anim.fadein);
                BTNLogoutRCP.startAnimation(animation);
                mAuth.signOut();
                startActivity(new Intent(RequestContactPageActivity.this, MainActivity.class));
            }
        });

        if (getIntent().hasExtra("userid")) {
            String userid = getIntent().getStringExtra("userid");
            String wiseoryoung = getIntent().getStringExtra("wiseoryoung");

            databaseReference = FirebaseDatabase.getInstance().getReference("users/" + wiseoryoung + "/" + userid);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    interestList = new ArrayList<>();
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String aboutMe = dataSnapshot.child("aboutme").getValue(String.class);
                    String image = dataSnapshot.child("image").getValue(String.class);
                    String wiseoryoung = getIntent().getStringExtra("wiseoryoung");
                    TVprofileNameRCPage.setText(name);
                    TVAboutMeRCPage.setText(aboutMe);
                    TVwiseOrYoungRCPage.setText(wiseoryoung);
                    getAllUserInterests(dataSnapshot);
                    addInterestsToView();
                    GlideApp.with(RequestContactPageActivity.this).load(image).into(IVpictureRCPage);

                }

                private void addInterestsToView() {
                    int numberOfInterests = interestList.size();
                    for (int i = 0; i < numberOfInterests; i++) {
                        ETinteretsListRCPage.append(interestList.get(i));
                    }
                }
                private void getAllUserInterests(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("Interests").child("BoardGames").exists())
                        interestList.add("Board Games\n");
                    if (dataSnapshot.child("Interests").child("Books").exists())
                        interestList.add("Books\n");
                    if (dataSnapshot.child("Interests").child("CardGames").exists())
                        interestList.add("Card Games\n");
                    if (dataSnapshot.child("Interests").child("CurrentAffairs").exists())
                        interestList.add("Current Affairs\n");
                    if (dataSnapshot.child("Interests").child("Films").exists())
                        interestList.add("Films\n");
                    if (dataSnapshot.child("Interests").child("Knitting").exists())
                        interestList.add("Knitting\n");
                    if (dataSnapshot.child("Interests").child("Music").exists())
                        interestList.add("Music\n");
                    if (dataSnapshot.child("Interests").child("Photography").exists())
                        interestList.add("Photography\n");
                    if (dataSnapshot.child("Interests").child("Puzzles").exists())
                        interestList.add("Puzzles\n");
                    if (dataSnapshot.child("Interests").child("Sport").exists())
                        interestList.add("Sport\n");
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        FloatingActionButton fab = findViewById(R.id.FABchatButtonRCPage);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequestContactPageActivity.this, EmailActivity.class);
                intent.putExtra("userid", getIntent().getStringExtra("userid"));
                intent.putExtra("wiseoryoung", getIntent().getStringExtra("wiseoryoung"));
                startActivity(intent);
            }
        });


    }

}
