package com.zcsmart.facelivedetect;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcB;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fri.nfcidcard.NfcIdentify;
import com.hisign.CTID.facelivedetection.CTIDLiveDetectActivity;
import com.nfc.entity.ResultModel;
import com.nfc.impl.IDImpl;
import com.nfc.impl.RZMImpl;
import com.nfc.inter.RZM;
import com.nfc.tools.ByteUtil;

import java.util.Arrays;

/**
 * Created by caokai on 2017/8/2.
 */


public class MainActivity extends Activity {
    // 调试域
    private static final String TAG = "NfcIDCard";
    // 实例化
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private TextView TagInfo;
    // private Button mButton_ClearKey;
    // 变量
    boolean bEnDisReaderMode = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    public void clickButton(View view){
        switch (view.getId()){
            case R.id.btn:
                Intent intent = new Intent(this, CTIDLiveDetectActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
            disableReaderMode();
            bEnDisReaderMode = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled()) startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, NfcIdentify.FILTERS, NfcIdentify.TECHLISTS); // Enable
            enableReaderMode();
            bEnDisReaderMode = true;
        }
        Log.e("NFC----", NfcB.class.getName());
    }

    @TargetApi(19)
    private void enableReaderMode() {
        int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A
                | NfcAdapter.FLAG_READER_NFC_B
                | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
                | NfcAdapter.FLAG_READER_NFC_F | NfcAdapter.FLAG_READER_NFC_V
                | NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS;
        if (nfcAdapter != null) {
            nfcAdapter.enableReaderMode(this, new MyReaderCallback(),
                    READER_FLAGS, null);
            bEnDisReaderMode = true;
            Log.d(TAG, "enableReaderMode OK");
        }
    }

    @TargetApi(19)
    private void disableReaderMode() {

        if (nfcAdapter != null) {
            nfcAdapter.disableReaderMode(this);
            bEnDisReaderMode = false;
            Log.d(TAG, "disableReaderMode OK");
        }
    }

    @TargetApi(19)
    public class MyReaderCallback implements NfcAdapter.ReaderCallback {

        @Override
        public void onTagDiscovered(final Tag tag) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    IDImpl idImpl = new IDImpl();
                    byte[] SuiJiShuShuJu = { 65, 52, 48, 65, 49, 52, 55, 65,
                            68, 56, 55, 67, 65, 51, 68, 65, 0, 43, 48, 69, 2,
                            33, 0, -5, -82, -2, 37, -53, 125, 44, -2, -60, -30,
                            -6, 59, -96, -20, -123, -37, 102, 126, -81, 20,
                            -80, 102, -19, 57, 78, 27, 55, -78, 110, 47, -24,
                            -117, 2, 32, 98, -3, -116, 101, 22, 22, -7, -61,
                            -115, 80, -1, 109, 22, 10, 68, -43, 62, 68, 6, -69,
                            -36, 73, -63, -27, -15, -22, 16, -123, 110, 1, -84,
                            -89 };
                    short SuiJiShuShuJuChangDu = (short) SuiJiShuShuJu.length;
                    ResultModel result = idImpl.ID_YanZheng(tag,
                            SuiJiShuShuJuChangDu, SuiJiShuShuJu, 1);
                    TagInfo.setText("DN数据:" + result.getWZName() + "\n"
                            + "返回结果:" + result.getStatus() + "\n" + "副本路经:"
                            + result.getWZPath() + "\n" + "ID验证数据："
                            + Arrays.toString(result.getIDData()) + "\n");

                    switch (result.getStatus()) {
                        case 0:
                            Toast.makeText(MainActivity.this, "读取数据", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(MainActivity.this, "读卡失败", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(MainActivity.this, "未找到网上副本", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(MainActivity.this, "验签失败", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    RZM rzm = new RZMImpl();
                    byte[] RzmResult = rzm.RZM_ShuRu(SuiJiShuShuJuChangDu, SuiJiShuShuJu, "03110311".getBytes());
                    System.out.println("认证码结果：" + ByteUtil.getString(RzmResult));
                }
            });
        }
    }
}
