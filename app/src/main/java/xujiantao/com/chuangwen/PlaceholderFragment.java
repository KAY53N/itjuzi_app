package xujiantao.com.chuangwen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PlaceholderFragment extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null)
        {
            getFragmentManager().beginTransaction().add(R.id.container, new SignUpActivity()).commit();
        }

    }
}
