package xujiantao.com.chuangwen;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import common.CategoryPageAdapter;

public class ItemListActivity extends FragmentActivity
{
    private final Handler handler = new Handler();

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private CategoryPageAdapter categoryAdapter;

    private int currentColor = 0xFF2161BC;

    private ImageView homeBtnIcon;
    private ImageView accountBtnIcon;

    private ResolveInfo homeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list);

        PackageManager pm = getPackageManager();

        homeInfo = pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);

        categoryTabs();

        findViewById(R.id.searchText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(ItemListActivity.this, SearchActivity.class));
            }
        });

        homeBtnIcon = (ImageView) findViewById(R.id.homeBtnIcon);
        accountBtnIcon = (ImageView) findViewById(R.id.accountBtnIcon);

        homeBtnIcon.getBackground().setColorFilter(new LightingColorFilter(0x000000, 0x000000));
        accountBtnIcon.getBackground().setColorFilter(new LightingColorFilter(0xCCCCCC, 0xCCCCCC));

        findViewById(R.id.accountBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(ItemListActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void categoryTabs()
    {
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        categoryAdapter = new CategoryPageAdapter(getSupportFragmentManager());

        pager.setAdapter(categoryAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setOffscreenPageLimit(5);

        tabs.setViewPager(pager);

        changeColor(currentColor);
    }

    private void goToIdle()
    {

        ActivityInfo ai = homeInfo.activityInfo;

        Intent startIntent = new Intent(Intent.ACTION_MAIN);

        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        startIntent.setComponent(new ComponentName(ai.packageName, ai.name));

        startActivitySafely(startIntent);
    }

    private void startActivitySafely(Intent intent)
    {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try
        {
            startActivity(intent);
        }
        catch(ActivityNotFoundException e)
        {
            Toast.makeText(this, "work wrongly", Toast.LENGTH_SHORT).show();

        }
        catch(SecurityException e)
        {
            Toast.makeText(this, "notsecurity",Toast.LENGTH_SHORT).show();
        }
    }

    private long exitTime = 0;
    //2.back键重写执行home键功能
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if((System.currentTimeMillis()-exitTime) > 2000)
            {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else
            {
                goToIdle();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //重写finish(),
    @Override
    public void finish()
    {
        super.finish();//activity永远不会自动退出了，而是处于后台。
        moveTaskToBack(true);
    }


    private void changeColor(int newColor)
    {
        tabs.setIndicatorColor(newColor);
        tabs.setIndicatorHeight(10);

        currentColor = newColor;

    }

    public void onColorClicked(View v)
    {
        int color = Color.parseColor(v.getTag().toString());
        changeColor(color);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        currentColor = savedInstanceState.getInt("currentColor");
        changeColor(currentColor);
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback()
    {
        @Override
        public void invalidateDrawable(Drawable who)
        {
            getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when)
        {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what)
        {
            handler.removeCallbacks(what);
        }
    };

}
