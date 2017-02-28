package xujiantao.com.chuangwen;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeActivity extends Activity
{

    private boolean isFirstIn = false;
    private static final int TIME = 2500;
    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case GO_HOME:
                    goHome();
                    break;

                case GO_GUIDE:
                    goGuide();
                    break;
            }

        };
    };

    private void init()
    {
        SharedPreferences perPreferences = getSharedPreferences("guideInfo", MODE_PRIVATE);
        isFirstIn = perPreferences.getBoolean("isFirstIn", true);
        if (!isFirstIn) {
            mHandler.sendEmptyMessageDelayed(GO_HOME, TIME);
        }else{
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, TIME);
            SharedPreferences.Editor editor = perPreferences.edit();
            editor.putBoolean("isFirstIn", false);
            editor.commit();
        }
    }

    private void goHome()
    {
        Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void goGuide()
    {
        Intent i = new Intent(WelcomeActivity.this, GuideActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        init();
    }
}
