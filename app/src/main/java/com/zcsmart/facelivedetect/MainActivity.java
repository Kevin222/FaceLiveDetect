package com.zcsmart.facelivedetect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hisign.CTID.facelivedetection.CTIDLiveDetectActivity;

/**
 * Created by caokai on 2017/8/2.
 */


public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
    }

    public void clickButton(View view){
        switch (view.getId()){
            case R.id.btn:
                Intent intent = new Intent(this, CTIDLiveDetectActivity.class);
                startActivity(intent);
                break;
        }
    }
}
