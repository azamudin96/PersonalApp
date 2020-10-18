package com.azamudin.personalsecureapp.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.azamudin.personalsecureapp.LoginActivity;
import com.azamudin.personalsecureapp.MainActivity;
import com.azamudin.personalsecureapp.R;
import com.azamudin.personalsecureapp.base.BaseActivity;
import com.azamudin.personalsecureapp.util.SharedPreferenceUtil;

public class AppStart extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);

        init();
    }

    private void init() {
        /**
         * Check login
         */
        if (isLogin()) {
            AppManager.getAppManager().ToOtherActivity(MainActivity.class);
        } else {
            AppManager.getAppManager().ToOtherActivity(LoginActivity.class);
        }
        finish();
    }

    public boolean isLogin() {
        String member_code = (String) SharedPreferenceUtil.get("member_code", "");
//        String status = (String) SharedPreferenceUtil.get("status", "");
        if (/*status.equals("") || */member_code.equals("")) {
            return false;
        }
        return true;
    }
}