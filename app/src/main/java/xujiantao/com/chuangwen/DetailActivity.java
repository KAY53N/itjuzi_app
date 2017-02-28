package xujiantao.com.chuangwen;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.CompanyDetail;
import common.Function;
import common.SpecialAdapter;
import common.VolleyLoadPicture;
import security.DES;

public class DetailActivity extends Activity {

    private ImageView logoBg;
    private TextView title;
    private TextView company;
    private TextView stage_simplify;
    private TextView url;
    private TextView product_list;
    private TextView area;
    private TextView category;
    private TextView pic;
    private TextView des;
    private TextView found_date;

    private LinearLayout progressBar;

    private ListView financingView;

    private DES desCrypt = new DES();

    private List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            findViewById(R.id.scrollView).scrollTo(0, 30);// 改变滚动条的位置
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        logoBg = (ImageView) findViewById(R.id.logoBg);
        title = (TextView) findViewById(R.id.title);
        company = (TextView) findViewById(R.id.company);
        stage_simplify = (TextView) findViewById(R.id.stage_simplify);
        url = (TextView) findViewById(R.id.url);
        product_list = (TextView) findViewById(R.id.product_list);
        area = (TextView) findViewById(R.id.area);
        category = (TextView) findViewById(R.id.category);
        pic = (TextView) findViewById(R.id.pic);
        des = (TextView) findViewById(R.id.des);
        found_date = (TextView) findViewById(R.id.found_date);
        progressBar = (LinearLayout) findViewById(R.id.progressBar);

        String id = getIntent().getStringExtra("id");

        if(!Function.isNetworkConnected(DetailActivity.this))
        {
            Toast.makeText(DetailActivity.this, "当前没有网络", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        String requestUrl = "http://api.xujiantao.com/company/detail?id=" + id;

        Log.i("~~~~~~~~~~~~~~~ DETAIL_URL ", requestUrl);

        StringRequest request = new StringRequest(requestUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String result)
            {
                Log.i("decode PRE ------------ ", result);
                String jsonString = null;
                try {
                    jsonString = desCrypt.decrypt(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("decode +++++++++++++++++ ", jsonString);

                CompanyDetail detail = JSON.parseObject(jsonString, CompanyDetail.class);

                if(detail.getLogo().trim().length() > 0)
                {
                    Log.i("~~~~~~~~~~~~ LOGO ", detail.getLogo());
                    String logoUrl = "http://api.xujiantao.com/upload/images/logo/" + detail.getLogo();
                    VolleyLoadPicture vlp = new VolleyLoadPicture(getApplicationContext(), logoBg);
                    vlp.getmImageLoader().get(logoUrl, vlp.getOne_listener());
                }
                else
                {
                    Log.i("~~~~~~~~~~~~~~~ LOGO CHANGE ", "default");
                    logoBg.setBackgroundResource(R.drawable.logobg_default);
                }

                logoBg.setScaleType(ImageView.ScaleType.FIT_CENTER);

                title.setText(detail.getTitle().toString());
                company.setText(detail.getCompany().toString());
                stage_simplify.setText(detail.getStage_simplify().toString());
                url.setText(detail.getUrl().toString());
                product_list.setText(detail.getProduct_list().toString());
                area.setText(detail.getArea().toString());
                category.setText(detail.getCategory().toString());
                pic.setText(detail.getPic().toString());
                des.setText(detail.getDes().toString());
                found_date.setText(detail.getFound_date().toString());

                listems = new ArrayList<Map<String, Object>>();
                for(int index = 0; index < detail.getFinancing().size(); index++)
                {
                    Map<String, Object> listem = new HashMap<String, Object>();
                    listem.put("financing_date", detail.getFinancing().get(index).getFinancing_date());
                    listem.put("financing_rank", detail.getFinancing().get(index).getFinancing_rank());
                    listem.put("financing_money", detail.getFinancing().get(index).getFinancing_money());
                    listem.put("organization", detail.getFinancing().get(index).getOrganization());
                    listems.add(listem);
                }

                financingView = (ListView) findViewById(R.id.financingView);

                financingView.setAdapter(new SpecialAdapter(
                        DetailActivity.this,
                        listems,
                        R.layout.detail_financing, new String[] {"financing_date", "financing_rank", "financing_money", "organization" },
                        new int[] {R.id.financingDate, R.id.financingRank, R.id.financingMoney, R.id.organization}
                ));

                setListViewHeightBasedOnChildren(financingView);

                progressBar.setVisibility(View.GONE);

                Handler handler = new Handler();
                handler.postDelayed(runnable, 0);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                progressBar.setVisibility(View.GONE);
            }
        });

        Volley.newRequestQueue(DetailActivity.this).add(request);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DetailActivity.this.finish();
            }
        });
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 60;

        listView.setLayoutParams(params);
    }

}
