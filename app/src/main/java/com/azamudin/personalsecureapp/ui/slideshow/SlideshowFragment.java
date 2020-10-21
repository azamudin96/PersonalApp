package com.azamudin.personalsecureapp.ui.slideshow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.azamudin.personalsecureapp.R;
import com.azamudin.personalsecureapp.entity.ReceiptItem;
import com.azamudin.personalsecureapp.entity.UserProfile;
import com.azamudin.personalsecureapp.util.SharedPreferenceUtil;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import io.realm.Realm;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;

    private ImageView edt_profile , saveProfile;
    private CircularImageView userPhoto;
    private EditText edt_name , tv_address , edt_email , edt_phone , edt_company , edt_fb;
    private Realm mRealm;
    String encodedImage, encImage;
    private static final int OPEN_CAM = 1;
    private Bitmap imageBitmap = null;
    private static final String TAG = "SlideshowFragment";
    Drawable userphoto ;

    private String userID = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        mRealm = Realm.getDefaultInstance();

        userID = "1";
        checkIfUploaded(userID);


        edt_profile = root.findViewById(R.id.edt_profile);
        userPhoto = root.findViewById(R.id.userPhoto);
        edt_name = root.findViewById(R.id.edt_name);
        tv_address = root.findViewById(R.id.tv_address);
        edt_email = root.findViewById(R.id.edt_email);
        edt_phone = root.findViewById(R.id.edt_phone);
        edt_company = root.findViewById(R.id.edt_company);
        edt_fb = root.findViewById(R.id.edt_fb);
        saveProfile = root.findViewById(R.id.saveProfile);

        userPhoto.setEnabled(false);

        edt_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Edit mode", Toast.LENGTH_SHORT).show();
                userPhoto.setEnabled(true);
                edt_name.setFocusableInTouchMode(true);
                edt_name.setClickable(true);
                tv_address.setFocusableInTouchMode(true);
                tv_address.setClickable(true);
                edt_email.setFocusableInTouchMode(true);
                edt_email.setClickable(true);
                edt_phone.setFocusableInTouchMode(true);
                edt_phone.setClickable(true);
                edt_company.setFocusableInTouchMode(true);
                edt_company.setClickable(true);
                edt_fb.setFocusableInTouchMode(true);
                edt_fb.setClickable(true);
                edt_name.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                edt_profile.setVisibility(View.GONE);
                saveProfile.setVisibility(View.VISIBLE);
            }
        });

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (encodedImage != null){
                    encodedImage = SharedPreferenceUtil.get("encodedImage" , "").toString();
                    saveUserProfile("1", encodedImage);
                    edt_profile.setVisibility(View.VISIBLE);
                    saveProfile.setVisibility(View.GONE);
                    edt_name.setFocusableInTouchMode(false);
                    edt_name.setClickable(false);
                    tv_address.setFocusableInTouchMode(false);
                    tv_address.setClickable(false);
                    edt_email.setFocusableInTouchMode(false);
                    edt_email.setClickable(false);
                    edt_phone.setFocusableInTouchMode(false);
                    edt_phone.setClickable(false);
                    edt_company.setFocusableInTouchMode(false);
                    edt_company.setClickable(false);
                    edt_fb.setFocusableInTouchMode(false);
                    edt_fb.setClickable(false);
                    edt_name.clearFocus();
                    edt_profile.clearFocus();
                    tv_address.clearFocus();
                    edt_email.clearFocus();
                    edt_phone.clearFocus();
                    edt_company.clearFocus();
                    edt_fb.clearFocus();
                    SharedPreferenceUtil.put("userPhone" , edt_phone.getText().toString());
                    userPhoto.setEnabled(false);
                    Toast.makeText(getContext(), "Profile Save Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please upload an image!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeTakePhoto();
            }
        });
//        final TextView textView = root.findViewById(R.id.text_slideshow);
//        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    private void saveUserProfile(final String data, final String image)
    {

        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                //data is the primary key (ref_no)
                UserProfile results = realm.where(UserProfile.class).equalTo("id", data).findFirst();

                if (results == null){
                    results = realm.createObject(UserProfile.class, data);
                }
                results.setUserImage(image);
                results.setUserFullName(edt_name.getText().toString());
                results.setUserLocation(tv_address.getText().toString());
                results.setUserEmail(edt_email.getText().toString());
                results.setUserPhone(edt_phone.getText().toString());
                results.setUserWorkPlace(edt_company.getText().toString());
                results.setUserFacebook(edt_fb.getText().toString());
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
                    //captured image set in imageview
                    userPhoto.setImageBitmap(imageBitmap);
                    SharedPreferenceUtil.put("encodedImage" , encodedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }


    private void checkIfUploaded(String refNo)
    {
        if (refNo!=null)
        {
            retrieveLocalImage(refNo);
        }

    }

    private void retrieveLocalImage(final String data) {
        final ArrayList<UserProfile> arrayList = new ArrayList<>();

        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UserProfile results;
                results = realm.where(UserProfile.class).equalTo("id", data).findFirst();
                arrayList.add(realm.copyFromRealm(results));

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                byte[] decodedString = Base64.decode(arrayList.get(0).getUserImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                userPhoto.setImageBitmap(decodedByte);
                edt_name.setText(arrayList.get(0).getUserFullName());
                Log.e(TAG , "huhu " + arrayList.get(0).getUserFullName() );
                tv_address.setText(arrayList.get(0).getUserLocation());
                edt_email.setText(arrayList.get(0).getUserEmail());
                edt_phone.setText(arrayList.get(0).getUserPhone());
                edt_company.setText(arrayList.get(0).getUserWorkPlace());
                edt_fb.setText(arrayList.get(0).getUserFacebook());
//                edt_username.setText(arrayList.get(0).getUserName());
//                edt_password.setText(arrayList.get(0).getPassWord());
//                if (!arrayList.get(0).getImageString().equals("")){
//                    llPhoto.setVisibility(View.VISIBLE);
//                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {

            }
        });
    }
}