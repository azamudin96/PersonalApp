package com.azamudin.personalsecureapp.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.azamudin.personalsecureapp.R;
import com.azamudin.personalsecureapp.entity.UserProfile;
import com.azamudin.personalsecureapp.util.SharedPreferenceUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Button btn_upload;
    HomeFragment bi;

    private CircularImageView user_photo;
    private TextView user_name , user_email , welcome , phone;
    private Realm mRealm;

    ImageView qrCode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        qrCode = root.findViewById(R.id.qr_code);
        user_photo = root.findViewById(R.id.user_photo);
        user_name = root.findViewById(R.id.user_name);
        user_email = root.findViewById(R.id.user_email);
        welcome = root.findViewById(R.id.welcome);
        phone = root.findViewById(R.id.phone);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

//        btn_upload = root.findViewById(R.id.btn_upload);
//
//        btn_upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getContext(),UploadPhoto.class);
//                startActivity(i);
//            }
//        });

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            welcome.setText("Hello , Good Morning!");
//            Toast.makeText(getContext(), "Good Morning", Toast.LENGTH_SHORT).show();
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            welcome.setText("Hello , Good Afternoon!");
//            Toast.makeText(getContext(), "Good Afternoon", Toast.LENGTH_SHORT).show();
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            welcome.setText("Hello , Good Evening!");
//            Toast.makeText(getContext(), "Good Evening", Toast.LENGTH_SHORT).show();
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            welcome.setText("Hello , Good Night!");
//            Toast.makeText(getContext(), "Good Night", Toast.LENGTH_SHORT).show();
        }


        try {

            qrcode();

        } catch(Exception e) {

            e.printStackTrace();

        }

        mRealm = Realm.getDefaultInstance();

        retriveLocalInfo("1");

        return root;
    }

    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

    public void qrcode() throws WriterException {

        String phone_number = SharedPreferenceUtil.get("userPhone" , "").toString();

        BitMatrix bitMatrix = multiFormatWriter.encode((String) phone_number, BarcodeFormat.QR_CODE, 300, 250);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
        qrCode.setImageBitmap(bitmap);

        if (phone_number != null) {
            phone.setVisibility(View.VISIBLE);
        }

    }

    private void retriveLocalInfo(final String data) {
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
                if (arrayList.get(0).getUserImage() != null){
                    user_photo.setVisibility(View.VISIBLE);
                }
                byte[] decodedString = Base64.decode(arrayList.get(0).getUserImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                user_photo.setImageBitmap(decodedByte);
                user_name.setText(arrayList.get(0).getUserFullName());
                user_email.setText(arrayList.get(0).getUserEmail());
//                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {

            }
        });
    }

}