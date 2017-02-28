package xujiantao.com.chuangwen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.Search;
import security.DES;

public class SearchActivity extends Activity
{

    private EditText eSearch;
    private ImageView ivDeleteText;
    private ListView mListView;

    private SimpleAdapter adapter;

    private Handler myhandler = new Handler();

    private  List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();

    private DES desCrypt = new DES();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        eSearch = (EditText) findViewById(R.id.etSearch);

        mListView = (ListView) findViewById(R.id.mListView);

        set_ivDeleteText_OnClick();
        set_eSearch_TextChanged();

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SearchActivity.this.finish();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ListView listView = (ListView)parent;
                HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
                String itemId = map.get("id");

                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                intent.putExtra("id", itemId);
                startActivity(intent);
            }
        });

        eSearch.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    //修改回车键功能
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(
                                    SearchActivity.this.getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS
                            );
                    myhandler.post(eChanged);
                }
                return false;
            }
        });
    }

    /**
     * 设置搜索框的文本更改时的监听器
     */
    private void set_eSearch_TextChanged()
    {
        eSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                //这个应该是在改变的时候会做的动作吧，具体还没用到过。
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
                //这是文本框改变之前会执行的动作
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                /**这是文本框改变之后 会执行的动作
                 * 因为我们要做的就是，在文本框改变的同时，我们的listview的数据也进行相应的变动，并且如一的显示在界面上。
                 * 所以这里我们就需要加上数据的修改的动作了。
                 */
                if(s.length() == 0){
                    ivDeleteText.setVisibility(View.GONE);//当文本框为空时，则叉叉消失
                }
                else {
                    ivDeleteText.setVisibility(View.VISIBLE);//当文本框不为空时，出现叉叉
                }
            }
        });

    }



    Runnable eChanged = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            String word = eSearch.getText().toString();

            listems.clear();//先要清空，不然会叠加

            try {
                getmDataSub(word);//获取更新数据
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 获得根据搜索框的数据data来从元数据筛选，筛选出来的数据放入mDataSubs里
     * @param word
     */
    private void getmDataSub(String word) throws UnsupportedEncodingException
    {
        if(word.trim().equals(""))
        {
            return;
        }

        String url = "http://api.xujiantao.com/company/search?keyword=" + java.net.URLEncoder.encode(word.trim(), "utf-8");
        System.out.println(url);
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {

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

                Search search = JSON.parseObject(jsonString, Search.class);
                Log.i("xxxxxxxxxxxxxxx ", search.getCompanyListData().size()+"");

                if(search.getCompanyListData().size() > 0)
                {
                    listems = new ArrayList<Map<String, Object>>();
                    for(int i=0; i<search.getCompanyListData().size(); i++)
                    {
                        Map<String, Object> listem = new HashMap<String, Object>();
                        listem.put("id", search.getCompanyListData().get(i).getId());
                        listem.put("title", search.getCompanyListData().get(i).getTitle());
                        listems.add(listem);
                    }

                    adapter = new SimpleAdapter(
                        SearchActivity.this,
                        listems,
                        R.layout.search_list_item,
                        new String[]{"id", "title"},
                        new int[]{R.id.id, R.id.title}
                    );

                    mListView.setAdapter(adapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
            }
        });

        Volley.newRequestQueue(SearchActivity.this).add(request);
    }

    /**
     * 设置叉叉的点击事件，即清空功能
     */
    private void set_ivDeleteText_OnClick()
    {
        ivDeleteText = (ImageView) findViewById(R.id.ivDeleteText);
        ivDeleteText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                eSearch.setText("");
            }
        });
    }
}
