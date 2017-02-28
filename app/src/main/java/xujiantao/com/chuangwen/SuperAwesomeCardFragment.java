package xujiantao.com.chuangwen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import bean.CompanyList;
import bean.CacheData;
import common.Function;
import common.SpecialAdapter;
import security.DES;

public class SuperAwesomeCardFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;

    private PullToRefreshListView lv;

    private  List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();

    private int page = 0;

    private String url = "http://api.xujiantao.com/company/company_list";

    private ViewGroup container;

    private LinearLayout progressBar;

    public static final String FIRST_DATA = "firstData";

    private DES desCrypt = new DES();

    public static SuperAwesomeCardFragment newInstance(int position)
    {
        SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        container = (ViewGroup) container;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        FrameLayout fl = new FrameLayout(getActivity());
        fl.setLayoutParams(params);

        View root = inflater.inflate(R.layout.data, container, false);

        progressBar = (LinearLayout) root.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        lv = (PullToRefreshListView) root.findViewById(R.id.myLv);

        lv.setMode(PullToRefreshBase.Mode.BOTH);
        lv.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多...");
        lv.getLoadingLayoutProxy(false, true).setReleaseLabel("放开加载更多...");
        lv.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");

        lv.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        lv.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
        lv.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新...");

        lv.getRefreshableView().setDividerHeight(0);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ListView listView = (ListView)parent;
                HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
                String itemId = map.get("id");

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("id", itemId);
                startActivity(intent);
            }
        });

        resetData();

        lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>(){

            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Toast.makeText(getActivity(), "下拉刷新", Toast.LENGTH_SHORT).show();

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    protected void onPostExecute(Void result)
                    {
                        resetData();
                        lv.onRefreshComplete();
                    }

                }.execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Toast.makeText(getActivity(), "上拉加载更多", Toast.LENGTH_SHORT).show();

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params)
                    {
                        try
                        {
                            Thread.sleep(500);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    protected void onPostExecute(Void result)
                    {
                        loadData();
                        lv.onRefreshComplete();
                    }

                }.execute();
            }
        });

        fl.addView(root);

        return fl;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    public void resetData()
    {
        Function.checkNetworkMsg(getActivity(), container);

        page = 1;

        String newUrl = url + "?page=" + page;
        if(position > 0)
        {
            newUrl = url + "?page=" + page + "&stage=" + position;
        }

        SharedPreferences sp = getActivity().getSharedPreferences(FIRST_DATA, Context.MODE_APPEND);
        String firsstLoadData = sp.getString("indexData" + position, "");

        if(firsstLoadData != "")
        {
            System.out.println("===================== 解析缓存 JSON: " + firsstLoadData);
            List<CacheData> data = new ArrayList<CacheData>(JSON.parseArray(firsstLoadData, CacheData.class));

            listems = null;
            listems = new ArrayList<Map<String, Object>>();
            for(int i=0; i<data.size(); i++)
            {
                Map<String, Object> listem = new HashMap<String, Object>();
                listem.put("id", data.get(i).getId());
                listem.put("title", data.get(i).getTitle());
                listem.put("category", data.get(i).getCategory());
                listem.put("financing", data.get(i).getFinancing());
                listem.put("money", data.get(i).getMoney());
                listem.put("corporation", data.get(i).getCorporation());
                System.out.println("::::::::::::::::::::::: TITLE: " + data.get(i).getTitle());
                listems.add(listem);
            }

            SimpleAdapter adapter = new SpecialAdapter(
                    getActivity(),
                    listems,
                    R.layout.custom_list_item,
                    new String[] { "id", "title", "category", "financing", "money", "corporation" },
                    new int[] {R.id.id, R.id.title, R.id.category, R.id.financing, R.id.money, R.id.corporation}
            );

            lv.setAdapter(adapter);

            progressBar.setVisibility(View.GONE);

            lv.getRefreshableView();

            System.out.println("===================== 使用缓存Array " + data.size() + " position: " + position);
        }
        else
        {
            StringRequest request = new StringRequest(newUrl, new Response.Listener<String>() {
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

                    System.out.println("##########" + url + "?page=" + page + "&stage=" + position);
                    CompanyList companyList = JSON.parseObject(jsonString, CompanyList.class);

                    listems = null;
                    listems = new ArrayList<Map<String, Object>>();

                    for(int i=0; i< companyList.getCompanyListData().size(); i++)
                    {
                        Map<String, Object> listem = new HashMap<String, Object>();
                        listem.put("id", companyList.getCompanyListData().get(i).getId());
                        listem.put("title", companyList.getCompanyListData().get(i).getTitle());
                        listem.put("category", companyList.getCompanyListData().get(i).getCategory());
                        listem.put("financing", companyList.getCompanyListData().get(i).getRank());
                        listem.put("money", companyList.getCompanyListData().get(i).getMoney());
                        listem.put("corporation", companyList.getCompanyListData().get(i).getOrganization());
                        listems.add(listem);
                    }

                    SharedPreferences sp = getActivity().getSharedPreferences(FIRST_DATA, Context.MODE_APPEND);
                    if(sp.getString("indexData", "") == "")
                    {
                        String indexDataJson = JSON.toJSONString(listems);
                        System.out.println("============= JSON STR " + indexDataJson);
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(FIRST_DATA, Context.MODE_PRIVATE).edit();
                        editor.putString("indexData" + position, indexDataJson);
                        editor.commit();

                        System.out.println("===================== 现在没有缓存，增加首页数据缓存, position: " + position);
                    }

                    SimpleAdapter adapter = new SpecialAdapter(
                            getActivity(),
                            listems,
                            R.layout.custom_list_item,
                            new String[] { "id", "title", "category", "financing", "money", "corporation" },
                            new int[] {R.id.id, R.id.title, R.id.category, R.id.financing, R.id.money, R.id.corporation}
                    );

                    lv.setAdapter(adapter);

                    progressBar.setVisibility(View.GONE);

                    lv.getRefreshableView().setSelection(((page - 1) * companyList.getCompanyListData().size()) - 3);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError)
                {
                }
            });

            Volley.newRequestQueue(getActivity().getApplicationContext()).add(request);
        }
    }



    public void loadData()
    {
        Function.checkNetworkMsg(getActivity(), container);
        if(!Function.isNetworkConnected(getActivity()))
        {
            return;
        }

        page = page+1;

        String newUrl = url + "?page=" + page;
        if(position > 0)
        {
            newUrl = url + "?page=" + page + "&stage=" + position;
        }

        StringRequest request = new StringRequest(newUrl, new Response.Listener<String>() {
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

                System.out.println("##########" + url + "?page=" + page + "&stage=" + position);
                CompanyList companyList = JSON.parseObject(jsonString, CompanyList.class);

                for(int i=0; i< companyList.getCompanyListData().size(); i++)
                {
                    Map<String, Object> listem = new HashMap<String, Object>();
                    listem.put("id", companyList.getCompanyListData().get(i).getId());
                    listem.put("title", companyList.getCompanyListData().get(i).getTitle());
                    listem.put("category", companyList.getCompanyListData().get(i).getCategory());
                    listem.put("financing", companyList.getCompanyListData().get(i).getRank());
                    listem.put("money", companyList.getCompanyListData().get(i).getMoney());
                    listem.put("corporation", companyList.getCompanyListData().get(i).getOrganization());
                    listems.add(listem);
                }

                SimpleAdapter adapter = new SpecialAdapter(
                        getActivity(),
                        listems,
                        R.layout.custom_list_item, new String[] { "id", "title", "category", "financing", "money", "corporation" },
                        new int[] {R.id.id, R.id.title, R.id.category, R.id.financing, R.id.money, R.id.corporation}
                );

                lv.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);

                lv.getRefreshableView().setSelection(((page-1)* companyList.getCompanyListData().size())-3);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
            }
        });

        Volley.newRequestQueue(getActivity().getApplicationContext()).add(request);
    }
}