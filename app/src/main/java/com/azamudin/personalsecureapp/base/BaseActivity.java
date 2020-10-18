package com.azamudin.personalsecureapp.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Window;

import androidx.fragment.app.FragmentActivity;

import com.azamudin.personalsecureapp.app.AppManager;

/**
 * Activity
 * Base class to solve code reusability
 * Created by Azamudin.
 */

public class BaseActivity extends FragmentActivity
{

    private static final String TAG = "BaseActivity";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        AppManager.getAppManager().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    public void openDialog()
    {
        if (progressDialog == null)
        {
            progressDialog.setMessage("Downloading Music :) ");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
        }
    }

    public void closeDialog()
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
