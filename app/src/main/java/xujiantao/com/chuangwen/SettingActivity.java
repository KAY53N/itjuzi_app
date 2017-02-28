package xujiantao.com.chuangwen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import common.Function;
import common.VolleyLoadPicture;
import cz.msebera.android.httpclient.Header;
import security.DES;
import security.MD5Util;

public class SettingActivity extends Activity
{
    private ImageView homeBtnIcon;
    private ImageView accountBtnIcon;
    private TableRow avatarBtn;
    private TableRow logoutBtn;
    private TableRow cleanCacheBtn;
    private TableRow editPassword;

    private static int CAMERA_REQUEST_CODE = 1;
    private static int GALLERY_REQUEST_CODE = 2;
    private static int CROP_REQUEST_CODE = 3;

    private ResolveInfo homeInfo;

    private String DEVICE_ID;

    private DES desCrypt = new DES();

    private static final String TAG  = "设置界面 ============ ";

    View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(SettingActivity.this, SettingEditActivity.class);
            switch (v.getId())
            {
                case R.id.nicknameBtn:
                    TextView nickName = (TextView) findViewById(R.id.nicknameVal);

                    intent.putExtra("label", "昵称");
                    intent.putExtra("type", "nickname");
                    intent.putExtra("value", nickName.getText().toString());
                    startActivity(intent);
                    break;

                case R.id.phone:
                    TextView phone = (TextView) findViewById(R.id.phoneVal);

                    intent.putExtra("label", "手机号");
                    intent.putExtra("type", "phone");
                    intent.putExtra("value", phone.getText().toString());
                    startActivity(intent);
                    break;

                case R.id.email:
                    TextView email = (TextView) findViewById(R.id.emailVal);

                    intent.putExtra("label", "邮箱");
                    intent.putExtra("type", "account_email");
                    intent.putExtra("value", email.getText().toString());
                    startActivity(intent);
                    break;

                case R.id.wechat:
                    TextView wechat = (TextView) findViewById(R.id.wechatVal);

                    intent.putExtra("label", "微信");
                    intent.putExtra("type", "wechat");
                    intent.putExtra("value", wechat.getText().toString());
                    startActivity(intent);
                    break;

                case R.id.cleanCacheBtn:
                    Function.cleanCache(getApplicationContext(), true);
                    break;

                case R.id.editPassword:
                    modifyPassword();
                    break;
            }
        }
    };

    private void modifyPassword()
    {
        final EditText inputServer = new EditText(SettingActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("请输入原密码").setIcon(android.R.drawable.ic_dialog_info)
                .setView(inputServer)
                .setNegativeButton("Cancel", null);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
            {
                SharedPreferences sp = getSharedPreferences("userInfo", MODE_APPEND);
                String spPassword = sp.getString("password", "");
                String inputPassword = MD5Util.GetMD5Code(inputServer.getText().toString());

                if(inputPassword.equals(spPassword))
                {
                    Intent intent = new Intent(SettingActivity.this, SettingEditActivity.class);
                    intent.putExtra("label", "修改密码");
                    intent.putExtra("type", "password");
                    intent.putExtra("value", "");
                    startActivity(intent);
                }
                else
                {
/*                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "原始密码输入错误", Snackbar.LENGTH_LONG).setActionTextColor(Color.RED);
                    Function.setSnackbarMessageTextColor(snackbar,Color.GREEN);
                    snackbar.show();*/
                    Log.i("~~~~~~~~~~~~~", "Preferences PASS: " + spPassword + " ===== INPUT PASS: " + inputPassword);
                    new  AlertDialog.Builder(SettingActivity.this)
                            .setTitle("提示" )
                            .setMessage("原始密码输入错误" )
                            .setPositiveButton("确定",  null )
                            .show();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        DEVICE_ID = tm.getDeviceId();

        homeBtnIcon = (ImageView) findViewById(R.id.homeBtnIcon);
        accountBtnIcon = (ImageView) findViewById(R.id.accountBtnIcon);
        avatarBtn = (TableRow) findViewById(R.id.avatarBtn);
        logoutBtn = (TableRow) findViewById(R.id.logoutBtn);
        cleanCacheBtn = (TableRow) findViewById(R.id.cleanCacheBtn);
        editPassword = (TableRow) findViewById(R.id.editPassword);

        findViewById(R.id.nicknameBtn).setOnClickListener(clickListener);
        findViewById(R.id.phone).setOnClickListener(clickListener);
        findViewById(R.id.email).setOnClickListener(clickListener);
        findViewById(R.id.wechat).setOnClickListener(clickListener);
        findViewById(R.id.cleanCacheBtn).setOnClickListener(clickListener);
        findViewById(R.id.editPassword).setOnClickListener(clickListener);

        PackageManager pm = getPackageManager();

        homeBtnIcon.getBackground().setColorFilter(new LightingColorFilter(0xCCCCCC, 0xCCCCCC));
        accountBtnIcon.getBackground().setColorFilter(new LightingColorFilter(0x000000, 0x000000));

        ImageView avatar = (ImageView) findViewById(R.id.avatarVal);
        TextView nickname = (TextView) findViewById(R.id.nicknameVal);
        TextView phone = (TextView) findViewById(R.id.phoneVal);
        TextView email = (TextView) findViewById(R.id.emailVal);
        TextView wechat = (TextView) findViewById(R.id.wechatVal);

        final SharedPreferences sp = getSharedPreferences("userInfo", MODE_APPEND);
        nickname.setText(sp.getString("nickname", ""));
        phone.setText(sp.getString("phone", ""));
        email.setText(sp.getString("account_email", ""));
        wechat.setText(sp.getString("wechat", ""));

        findViewById(R.id.homeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(SettingActivity.this, ItemListActivity.class);
                startActivity(intent);
            }
        });

        avatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final String[] arrayFruit = new String[] { "摄像头", "图库"};
                Dialog alertDialog = new AlertDialog.Builder(SettingActivity.this).
                        setTitle("修改头像选项").
                        //setIcon(R.drawable.ic_launcher).
                        setItems(arrayFruit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if(which == 0)
                                {
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                                }
                                else if(which == 1)
                                {
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, GALLERY_REQUEST_CODE);
                                }
                            }
                        }).
                        setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // TODO Auto-generated method stub
                            }
                        }).
                        create();
                        alertDialog.show();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new AlertDialog.Builder(SettingActivity.this).setTitle("确认退出账户吗？")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            logoutFun(sp.getString("account_email", ""), sp.getString("token", ""));
                        }
                    })
                    .setNegativeButton("返回", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                        }
                    }).show();
            }
        });


        if(!Function.isNetworkConnected(SettingActivity.this))
        {
            Toast.makeText(SettingActivity.this, "当前没有网络", Toast.LENGTH_SHORT).show();
            return;
        }

        String avatarUrl = sp.getString("avatar", "");
        Log.i("*********************AVATAR ", avatarUrl);
        if(avatarUrl != "")
        {
            findViewById(R.id.avatarVal).setBackgroundResource(0);
            VolleyLoadPicture vlp = new VolleyLoadPicture(getApplicationContext(), avatar);
            vlp.getmImageLoader().get(avatarUrl, vlp.getOne_listener());
        }
    }

    private void logoutFun(final String email, final String token)
    {
        SharedPreferences.Editor editor = getSharedPreferences("userInfo", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();

        SharedPreferences.Editor cacheEditor = getSharedPreferences(SuperAwesomeCardFragment.FIRST_DATA, Context.MODE_PRIVATE).edit();
        cacheEditor.clear();
        cacheEditor.commit();

        Function.cleanCache(getApplicationContext(), false);

        String url = "http://api.xujiantao.com/account/logout";
        Log.i(TAG + " Token: ", token);
        Log.i(TAG + " Email: ", email);
        StringRequest request = new StringRequest(Request.Method.POST, url, new ResponseListener(), new ResponseErrorListener()){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> map = new HashMap<String, String>();
                map.put("email", email.toString());
                map.put("token", token.toString());
                return map;
            }
        };
        Volley.newRequestQueue(SettingActivity.this).add(request);


        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setClass(SettingActivity.this, MainActivity.class);
        startActivity(intent);

    }

    private class ResponseListener implements Response.Listener<String>
    {
        @Override
        public void onResponse(String response)
        {
            Log.i(TAG, "decode PRE ------------ " + response);
            String jsonString = null;
            try {
                jsonString = desCrypt.decrypt(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(TAG, "decode +++++++++++++++++ " + jsonString);

            try {
                JSONObject json = new JSONObject(jsonString);
                Boolean returnStatus = (boolean) json.get("status");
                String returnMessage = json.get("message").toString();

                Log.i(TAG, "退出操作 + " + returnMessage);

            } catch (JSONException e) {
                //e.printStackTrace();
            }
        }
    }

    private class ResponseErrorListener implements Response.ErrorListener
    {
        @Override
        public void onErrorResponse(VolleyError error)
        {
            Log.e(TAG, error.getMessage(), error);
        }
    }

    private Uri saveBitmap(Bitmap bm)
    {
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/com.xujiantao.root.avatar");
        if(!tmpDir.exists())
        {
            tmpDir.mkdir();
        }

        File img = new File(tmpDir.getAbsolutePath() + "avatar.png");

        try {
            FileOutputStream fos = new FileOutputStream(img);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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

    @Override
    public void finish()
    {
        super.finish();
        moveTaskToBack(true);
    }



    private Uri convertUri(Uri uri)
    {
        InputStream is = null;

        try {
            is = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return saveBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void startUriZoom(Uri uri)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    private void sendImage(Bitmap bm)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 60, stream);
        byte[] bytes = stream.toByteArray();
        String img = new String(Base64.encodeToString(bytes, Base64.DEFAULT));

        SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("img", img);
        params.add("email", sp.getString("account_email", ""));
        params.add("device_id", DEVICE_ID);
        params.add("token", sp.getString("token", ""));

        client.post("http://api.xujiantao.com/account/upload_avatar", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes)
            {
                String jsonString = null;
                try {
                    jsonString = desCrypt.decrypt(new String(bytes));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try
                {
                    JSONObject json = new JSONObject(jsonString);
                    Object message = json.getString("message");

                    if(json.has("url"))
                    {
                        Object avatar = json.getString("url");
                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~Success " + message.toString());

                        SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
                        editor.remove("avatar");
                        editor.commit();
                        editor.putString("avatar", avatar.toString());
                        editor.commit();

                        Log.i("~~~~~~~~~~~~~~~~~~~~ 新头像 ", avatar.toString());
                    }

                    Toast.makeText(SettingActivity.this, message.toString(), Toast.LENGTH_LONG).show();
                }
                catch (JSONException e)
                {
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable)
            {
                String jsonString = null;
                try {
                    jsonString = desCrypt.decrypt(new String(bytes));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try
                {
                    JSONObject json = new JSONObject(jsonString);
                    Object message = json.getString("message");
                    Toast.makeText(SettingActivity.this, message.toString(), Toast.LENGTH_LONG).show();
                }
                catch (JSONException e)
                {
                    //e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE)
        {
            if(data == null)
            {
                return;
            }
            else
            {
                Bundle extras = data.getExtras();
                if(extras != null)
                {
                    Bitmap bm = extras.getParcelable("data");
                    Uri uri = saveBitmap(bm);
                    startUriZoom(uri);
                }
            }

        }
        else if(requestCode == GALLERY_REQUEST_CODE)
        {
            if(data == null)
            {
                return;
            }
            Uri uri;
            uri = data.getData();
            Uri fileUri = convertUri(uri);
            startUriZoom(fileUri);
        }
        else if(requestCode == CROP_REQUEST_CODE)
        {
            if(data == null)
            {
                return;
            }

            findViewById(R.id.avatarVal).setBackgroundResource(0);

            Bundle extras = data.getExtras();
            Bitmap bm = extras.getParcelable("data");
            ImageView imageView = (ImageView) findViewById(R.id.avatarVal);
            imageView.setImageBitmap(bm);
            sendImage(bm);
        }
    }
}
