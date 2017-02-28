package xujiantao.com.chuangwen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import security.DES;

public class SettingEditActivity extends Activity
{
    private DES desCrypt = new DES();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_edit);

        TextView tv = (TextView) findViewById(R.id.label);
        EditText et = (EditText) findViewById(R.id.editText);
        TextView title = (TextView) findViewById(R.id.titleText);

        tv.setText(getIntent().getStringExtra("label").toString());
        et.setText(getIntent().getStringExtra("value").toString());
        title.setText(getIntent().getStringExtra("label").toString());


        findViewById(R.id.btnBack).getBackground().setColorFilter(new LightingColorFilter(0xefbe48, 0xefbe48));

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SettingEditActivity.this.finish();
            }
        });


        if(getIntent().getStringExtra("type").toString() == "password")
        {
            et.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        }

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final String value = ((EditText) findViewById(R.id.editText)).getText().toString();
                final SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                String url = "http://api.xujiantao.com/account/account_edit";
                Log.i("修改信息  --------------------- ", url);
                Log.i("Token ------------------------- ", sp.getString("token", ""));

                StringRequest request = new StringRequest(Request.Method.POST, url, new ResponseListener(), new ResponseErrorListener()){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError
                    {
                        Map<String, String> map = new HashMap<String, String>();
                        try {
                            map.put("type", getIntent().getStringExtra("type"));
                            map.put("val", java.net.URLEncoder.encode(value, "utf-8"));
                            map.put("email", java.net.URLEncoder.encode(sp.getString("account_email", "")));
                            map.put("token", java.net.URLEncoder.encode(sp.getString("token", "")));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        return map;
                    }
                };
                Volley.newRequestQueue(getApplication()).add(request);
            }
        });

    }


    private class ResponseListener implements Response.Listener<String>
    {

        @Override
        public void onResponse(String response)
        {
            Log.i("decode PRE ------------ ", response);
            String jsonString = null;
            try {
                jsonString = desCrypt.decrypt(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("decode +++++++++++++++++ ", jsonString);

            try {
                JSONObject json = new JSONObject(jsonString);
                Boolean returnStatus = (boolean) json.get("status");
                String returnMessage = json.get("message").toString();
                String value = ((EditText) findViewById(R.id.editText)).getText().toString();

                if(returnStatus == true)
                {
                    SharedPreferences.Editor editor = getSharedPreferences("userInfo", Context.MODE_PRIVATE).edit();
                    editor.putString(getIntent().getStringExtra("type"), value);
                    editor.commit();

                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.setClass(SettingEditActivity.this, SettingActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(SettingEditActivity.this, returnMessage, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ResponseErrorListener implements Response.ErrorListener
    {
        @Override
        public void onErrorResponse(VolleyError error)
        {
            Log.e("TAG", error.getMessage(), error);
        }
    }
}
