package com.azamudin.personalsecureapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.azamudin.personalsecureapp.anim.ViewAnimation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

public class DataActivity extends AppCompatActivity implements IPickResult {

    private FloatingActionButton fabPhoto , fabSave , fabAdd;
    private boolean isRotate = false;
    private TextView activity_type;
    private ImageView imageView;
    private String activityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initIntent();
        init();
    }

    private void initIntent(){
        Bundle bundle = getIntent().getExtras();
        activityType = bundle.getString("activity_type");
    }

    private void init(){
        fabPhoto = findViewById(R.id.fabPhoto);
        fabSave = findViewById(R.id.fabSave);
        fabAdd = findViewById(R.id.fabAdd);
        activity_type = findViewById(R.id.activity_type);
        activity_type.setText(activityType);

        ViewAnimation.init(fabPhoto);
        ViewAnimation.init(fabSave);


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRotate = ViewAnimation.rotateFab(v, !isRotate);
                if(isRotate){
                    ViewAnimation.showIn(fabPhoto);
                    ViewAnimation.showIn(fabSave);
                }else{
                    ViewAnimation.showOut(fabPhoto);
                    ViewAnimation.showOut(fabSave);
                }
            }
        });

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DataActivity.this, "Save", Toast.LENGTH_SHORT).show();
            }
        });

        fabPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup()).show(DataActivity.this);

                Toast.makeText(DataActivity.this, "Photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            //getImageView().setImageURI(null);

            //Setting the real returned image.
            //getImageView().setImageURI(r.getUri());

            //If you want the Bitmap.
            getImageView().setImageBitmap(r.getBitmap());

            //Image path
            //r.getPath();
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public ImageView getImageView() {
        return imageView;
    }
}