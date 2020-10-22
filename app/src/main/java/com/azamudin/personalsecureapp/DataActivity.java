package com.azamudin.personalsecureapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azamudin.personalsecureapp.anim.ViewAnimation;
import com.azamudin.personalsecureapp.entity.ReceiptItem;
import com.azamudin.personalsecureapp.util.AESUtils;
import com.azamudin.personalsecureapp.util.SharedPreferenceUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import io.realm.Realm;

public class DataActivity extends AppCompatActivity implements IPickResult {

    private FloatingActionButton fabPhoto , fabSave , fabAdd;
    private boolean isRotate = false;
    private TextView activity_type , edt_username , edt_password , edt_username1 , edt_password1 , my_password;
    private ImageView imageView;
    private String activityType;
    private ImageView image_upload;
    private static final int OPEN_CAM = 8345;
    private Bitmap imageBitmap = null;
    private Realm mRealm;
    private Button btn_save , show_my_password;

    private ImageView closeimg;

    private static final String TAG = "DataActivity";

    ImageView imageUpload , saveImg;
    String encodedImage, encImage;

    String order_id = "";

    AlertDialog alertDialog;

    private LinearLayout llPhoto;

    String encryptedPassword , decryptedPassword;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initIntent();
        init();

        mRealm = Realm.getDefaultInstance();
        checkIfUploaded(order_id);
    }

    private void initIntent(){
        Bundle bundle = getIntent().getExtras();
        activityType = bundle.getString("activity_type");
        order_id = bundle.getString("categoryName");
    }

    private void init(){
        fabPhoto = findViewById(R.id.fabPhoto);
        fabSave = findViewById(R.id.fabSave);
        fabAdd = findViewById(R.id.fabAdd);
        activity_type = findViewById(R.id.activity_type);
        activity_type.setText(activityType);
        saveImg = findViewById(R.id.saveImg);
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        closeimg = findViewById(R.id.closeimg);
        llPhoto = findViewById(R.id.llPhoto);
        show_my_password = findViewById(R.id.show_my_password);
        my_password = findViewById(R.id.my_password);

        ViewAnimation.init(fabPhoto);
        ViewAnimation.init(fabSave);


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRotate = ViewAnimation.rotateFab(v, !isRotate);
                if(isRotate){
//                    ViewAnimation.showIn(fabPhoto);
                    ViewAnimation.showIn(fabSave);
                }else{
//                    ViewAnimation.showOut(fabPhoto);
                    ViewAnimation.showOut(fabSave);
                }
            }
        });

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowSaveDialog();
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

        closeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llPhoto.setVisibility(View.GONE);
            }
        });

        show_my_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_password.getText().toString().length() != 0){
                executor = ContextCompat.getMainExecutor(DataActivity.this);
                biometricPrompt = new BiometricPrompt(DataActivity.this,
                        executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode,
                                                      @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(
                            @NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(getApplicationContext(),
                                "Authentication succeeded!", Toast.LENGTH_SHORT).show();

                        try {
                            decryptedPassword = AESUtils.decrypt(edt_password.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                        try {
//                            decrypted = AESUtils.decrypt(encrypted);
//                            Log.d("TEST", "decrypted:" + decrypted);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }

                        my_password.setVisibility(View.VISIBLE);
                        my_password.setText(decryptedPassword);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });

                promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Biometric checking for showing password")
                        .setSubtitle("Check in using your biometric credential")
                        .setNegativeButtonText("Use account password")
                        .build();

                biometricPrompt.authenticate(promptInfo);
                }else
                {
                    Toast.makeText(DataActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
                }
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

    private void ShowSaveDialog() {
        AlertDialog.Builder al=new AlertDialog.Builder(DataActivity.this);
        View view=getLayoutInflater().inflate(R.layout.layout_edit,null);
        al.setView(view);

//        final TextView data_id=view.findViewById(R.id.data_id);
        image_upload = view.findViewById(R.id.image_upload);
        edt_username1 = view.findViewById(R.id.edt_username1);
        edt_password1 = view.findViewById(R.id.edt_password1);
        btn_save=view.findViewById(R.id.btn_save);
        alertDialog=al.show();

        image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeTakePhoto();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (encodedImage!=null){
                    encodedImage = SharedPreferenceUtil.get("encodedImg" , "").toString();
                    saveLocalImage(order_id,encodedImage);
                } else {
                    Toast.makeText(DataActivity.this, "Please capture a photo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (order_id!=null)
        {
        final ArrayList<ReceiptItem> arrayList = new ArrayList<>();

        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ReceiptItem results;
                results = realm.where(ReceiptItem.class).equalTo("orderNo", order_id).findFirst();
                arrayList.add(realm.copyFromRealm(results));
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                byte[] decodedString = Base64.decode(arrayList.get(0).getImageString(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                image_upload.setImageBitmap(decodedByte);
                edt_username1.setText(arrayList.get(0).getUserName());
                edt_password1.setText(arrayList.get(0).getPassWord());
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {

            }
        });
        }

//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                long id= Long.parseLong(data_id.getText().toString());
//                final Category category1=mRealm.where(Category.class).equalTo("name",tv).findFirst();
//                mRealm.executeTransaction(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//                        alertDialog.dismiss();
//                        category1.deleteFromRealm();
//                        adapter.remove(category.get(position));
//                    }
//                });
//            }
//        });
    }

    private void activeTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, OPEN_CAM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_CAM){
            if (resultCode == Activity.RESULT_OK) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                //to generate random file name
                String fileName = "tempimg.jpg";

                try {
                    imageBitmap = (Bitmap) data.getExtras().get("data");
                    encodedImage = encodeImage(imageBitmap);
                    SharedPreferenceUtil.put("encodedImg" , encodedImage);
                    //captured image set in imageview
                    image_upload.setImageBitmap(imageBitmap);
                    btn_save.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    //converts image tp base64
    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    //get base64 image from realmDatabase (local database)
    private void retrieveLocalImage(final String data) {
        final ArrayList<ReceiptItem> arrayList = new ArrayList<>();

        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ReceiptItem results;
                results = realm.where(ReceiptItem.class).equalTo("orderNo", data).findFirst();
                arrayList.add(realm.copyFromRealm(results));

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                byte[] decodedString = Base64.decode(arrayList.get(0).getImageString(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                saveImg.setImageBitmap(decodedByte);
                edt_username.setText(arrayList.get(0).getUserName());
                edt_password.setText(arrayList.get(0).getPassWord());
                if (!arrayList.get(0).getImageString().equals("")){
                    llPhoto.setVisibility(View.VISIBLE);
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {

            }
        });
    }

    //save base64 image to realmDatabase (local database)
    private void saveLocalImage(final String data, final String image)
    {

        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                //data is the primary key (ref_no)
                ReceiptItem results = realm.where(ReceiptItem.class).equalTo("orderNo", data).findFirst();

                if (results == null){
                    results = realm.createObject(ReceiptItem.class, data);
                }
                results.setImageString(image);
                results.setUserName(edt_username1.getText().toString());

                try {
                    encryptedPassword = AESUtils.encrypt(edt_password1.getText().toString());
                    Log.d("TEST", "encrypted:" + encryptedPassword);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                results.setPassWord(encryptedPassword);

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                alertDialog.dismiss();
                retrieveLocalImage(data);
                Log.d(TAG, "onSuccess: ");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "onError: " + error.getMessage());
            }
        });
    }

    private void checkIfUploaded(String refNo)
    {
        if (refNo!=null)
        {
            retrieveLocalImage(refNo);
        }

    }
}