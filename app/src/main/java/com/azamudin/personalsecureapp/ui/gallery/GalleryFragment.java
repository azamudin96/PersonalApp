package com.azamudin.personalsecureapp.ui.gallery;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.azamudin.personalsecureapp.DataActivity;
import com.azamudin.personalsecureapp.DataModel;
import com.azamudin.personalsecureapp.MainActivity;
import com.azamudin.personalsecureapp.R;
import com.azamudin.personalsecureapp.entity.Category;
import com.azamudin.personalsecureapp.entity.RealmHelper;
import com.azamudin.personalsecureapp.entity.ReceiptItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.UUID;

import io.realm.Realm;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    Realm mRealm;
    private static final String TAG = "GalleryFragment";
    ArrayList<String> category;
    ListView listView;
    ArrayAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        final FloatingActionButton fab = root.findViewById(R.id.fab);
        listView = root.findViewById(R.id.listView);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                saveLocalImage();
                displayInputDialog();
            }
        });

        mRealm = Realm.getDefaultInstance();

        //RETRIEVE
        RealmHelper helper=new RealmHelper(mRealm);
        category = helper.retrieve();

        adapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,category);
        listView.setAdapter(adapter);

        //ITEM CLICKS
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent i = new Intent(getContext(), DataActivity.class);
               i.putExtra("activity_type" , category.get(position));
               startActivity(i);
            }
        });

        //press long to delete
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                ShowDeleteDialog(category.get(pos) , pos);
                Toast.makeText(getContext(),category.get(pos),Toast.LENGTH_SHORT).show();

                Log.v("long clicked","pos: " + pos);

                return true;
            }
        });

        return root;
    }

    //DISPLAY INPUT DIALOG
    private void displayInputDialog()
    {
        Dialog d=new Dialog(getContext());
        d.setTitle("Save to Realm");
        d.setContentView(R.layout.input_dialog);
        final EditText nameeditTxt= (EditText) d.findViewById(R.id.nameEditText);
        Button saveBtn= (Button) d.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GET DATA
                Category s=new Category();
                s.setName(nameeditTxt.getText().toString());
                //SAVE
                RealmHelper helper=new RealmHelper(mRealm);
                helper.save(s);
                nameeditTxt.setText("");
                //RETRIEVE
                category=helper.retrieve();
                adapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,category);
                listView.setAdapter(adapter);
            }
        });
        d.show();
    }

    private void ShowDeleteDialog(final String tv , final int position) {
        AlertDialog.Builder al=new AlertDialog.Builder(getContext());
        View view=getLayoutInflater().inflate(R.layout.delete_dialog,null);
        al.setView(view);

        final TextView data_id=view.findViewById(R.id.data_id);
        data_id.setText(tv);
        Button delete=view.findViewById(R.id.delete);
        final AlertDialog alertDialog=al.show();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                long id= Long.parseLong(data_id.getText().toString());
                final Category category1=mRealm.where(Category.class).equalTo("name",tv).findFirst();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alertDialog.dismiss();
                        category1.deleteFromRealm();
                        adapter.remove(category.get(position));
                    }
                });
            }
        });
    }

//    private void saveLocalImage(final String data, final String image)
//    {
//
//        mRealm.executeTransactionAsync(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//
//                //data is the primary key (ref_no)
//                Category results = realm.where(Category.class).equalTo("orderNo", data).findFirst();
//
//                if (results == null){
//                    results = realm.createObject(Category.class, data);
//                }
//                results.setName(image);
//
//            }
//        }, new Realm.Transaction.OnSuccess() {
//            @Override
//            public void onSuccess() {
//                Log.d(TAG, "onSuccess: ");
//            }
//        }, new Realm.Transaction.OnError() {
//            @Override
//            public void onError(Throwable error) {
//                Log.d(TAG, "onError: " + error.getMessage());
//            }
//        });
//    }
}