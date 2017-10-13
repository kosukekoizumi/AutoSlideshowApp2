package jp.techacademy.kousuke.koizumi.autoslideshowapp2;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;

import static jp.techacademy.kousuke.koizumi.autoslideshowapp2.R.id.start_button;
import static jp.techacademy.kousuke.koizumi.autoslideshowapp2.R.id.rewind_button;
import static jp.techacademy.kousuke.koizumi.autoslideshowapp2.R.id.forward_button;

public class MainActivity extends AppCompatActivity {

    Timer mTimer;
    Handler mHandler = new Handler();

    Button mStartButton;
    Button mRewindButton;
    Button mForwardButton;

    Cursor mCursor;

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            getContentsInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                } else {
                    Toast.makeText(this, "パーミッションが拒否されたので、アプリを終了します。", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }


    private void setImageView() {
        int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = mCursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
        imageVIew.setImageURI(imageUri);
    }


    private void getContentsInfo() {

        ContentResolver resolver = getContentResolver();
        mCursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (mCursor.moveToNext()) {

        } else {
            mCursor.moveToFirst();
        }
        setImageView();

        mStartButton = (Button) findViewById(R.id.start_button);
        mStartButton.setText("  自動再生  ");
        mRewindButton = (Button) findViewById(R.id.rewind_button);
        mRewindButton.setText(" ＜＜ 戻る  ");
        mForwardButton = (Button) findViewById(forward_button);
        mForwardButton.setText(" 進む ＞＞  ");


        mStartButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {   //★自動再生・再生停止ボタンクリック

                if (mTimer == null) {
                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mCursor.moveToNext()) {

                                    } else {
                                        mCursor.moveToFirst();
                                    }
                                    setImageView();

                                }
                            });
                        }
                    }, 2000, 2000);

                    mStartButton.setText("  再生停止  ");
                    mForwardButton.setEnabled(false);
                    mRewindButton.setEnabled(false);

                } else {
                    mStartButton.setText("  自動再生  ");
                    mForwardButton.setEnabled(true);
                    mRewindButton.setEnabled(true);

                    mTimer.cancel();
                    mTimer = null;

                }

            }
        });

        mRewindButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {   //★戻るボタンクリック

                if (mCursor.moveToPrevious()) {

                } else {
                    mCursor.moveToLast();
                }

                setImageView();

            }
        });

        mForwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {   //★進むボタンクリック


                if (mCursor.moveToNext()) {

                } else {
                    mCursor.moveToFirst();
                }

                setImageView();

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCursor.close();
    }

}