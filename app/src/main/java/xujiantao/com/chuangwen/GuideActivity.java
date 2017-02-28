package xujiantao.com.chuangwen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import common.ViewPagerAdapter;

public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener
{
    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;
    private ImageView[] dots;
    private int[] ids = {R.id.iv1, R.id.iv2, R.id.iv3};
    private Button startBtn;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        initViews();
        initDots();
    }

    private void initViews()
    {
        LayoutInflater inflater = LayoutInflater.from(this);

        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.one, null));
        views.add(inflater.inflate(R.layout.two, null));
        views.add(inflater.inflate(R.layout.three, null));

        vpAdapter = new ViewPagerAdapter(views, this);
        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);

        startBtn = (Button) views.get(2).findViewById(R.id.btnStart);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(GuideActivity.this, MainActivity.class));
            }
        });

        vp.setOnPageChangeListener(this);
    }

    private void initDots()
    {
        dots = new ImageView[views.size()];
        for (int i = 0; i < views.size(); i++)
        {
            dots[i] = (ImageView) findViewById(ids[i]);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2)
    {

    }

    @Override
    public void onPageSelected(int i)
    {
        for(int index = 0; index < ids.length; index++)
        {
            if(i == index)
            {
                dots[index].setImageResource(R.drawable.login_point_selected);
            }
            else
            {
                dots[index].setImageResource(R.drawable.login_point);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int i)
    {

    }
}