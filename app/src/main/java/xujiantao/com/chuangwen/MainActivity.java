package xujiantao.com.chuangwen;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null)
        {
            SignInActivity signin = new SignInActivity();
            getFragmentManager().beginTransaction().add(R.id.container, signin).commit();
        }
    }

}
