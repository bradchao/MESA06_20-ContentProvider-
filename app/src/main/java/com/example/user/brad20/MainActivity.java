package com.example.user.brad20;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private TelephonyManager tmgr;
    private AccountManager amgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.READ_CONTACTS},
                    123);
        }else{
            init();
        }
    }

    private void init(){
        tmgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        Log.v("brad", "機馬" +tmgr.getDeviceId());
        Log.v("brad", "ｓｉｍ" +tmgr.getSubscriberId());
        tmgr.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);

        amgr = (AccountManager)getSystemService(ACCOUNT_SERVICE);
        Account[] as = amgr.getAccounts();
        for (Account a: as){
            Log.v("brad", a.name + ":" +a.type );
        }

        getContact();


    }

    private void getContact(){
        Log.v("brad", "OK");
        ContentResolver contentResolver = getContentResolver();
        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

        Log.v("brad", "count: " + cursor.getCount());

        while ( cursor.moveToNext()){
            String name = cursor.getString(0);
            String tel = cursor.getString(1);
            Log.v("brad", name + ":" + tel);
        }




    }


    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.v("brad", incomingNumber);
                    break;
            }

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }
}
