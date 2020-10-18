package com.azamudin.personalsecureapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.azamudin.personalsecureapp.base.BaseActivity;
import com.azamudin.personalsecureapp.entity.ReceiptItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

public class UploadPhoto extends BaseActivity {

    private static final String TAG = "UploadPhoto";

    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private static final int PERMISSION_ALL = 101;
    private static final int OPEN_CAM = 102;
    private Bitmap imageBitmap = null;

    ImageView imageUpload;
    Button upload;
    String order_id = "";
    private List<Map<String, Object>> data = new ArrayList<>();
    String encodedImage, filePath, payment_file_name, encImage;
    Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        mRealm = Realm.getDefaultInstance();
        initView();
        checkIfUploaded(order_id);

    }

    private void initView(){
        imageUpload = findViewById(R.id.image_upload);
        upload = findViewById(R.id.btn_upload);
        upload.setEnabled(false);

        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                selectImage();
                activeTakePhoto();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUpload!=null) {
                    saveLocalImage(order_id,encodedImage);
//                    uploadCustomerPicture(encImage, payment_file_name);
                }
                else
                {
                    Toast.makeText(UploadPhoto.this, "No image is selected", Toast.LENGTH_SHORT).show();
                }

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

    private void selectImage()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int  column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK) {
                final Uri imageUri = data.getData();

                filePath = getPath(imageUri);

                File file = new File(filePath);
                payment_file_name = file.getName();
                Log.d(TAG, "payment file name: " + payment_file_name);

                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                encodedImage = encodeImage(selectedImage);
                imageUpload.setImageURI(imageUri);
                upload.setEnabled(true);

            }
        } else if (requestCode == OPEN_CAM){
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            //to generate random file name
            String fileName = "tempimg.jpg";

            try {
                imageBitmap = (Bitmap) data.getExtras().get("data");
                encodedImage = encodeImage(imageBitmap);
                //captured image set in imageview
                imageUpload.setImageBitmap(imageBitmap);
                upload.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
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
                imageUpload.setImageBitmap(decodedByte);
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

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "onError: " + error.getMessage());
            }
        });
    }

    private void activeTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, OPEN_CAM);
    }

}