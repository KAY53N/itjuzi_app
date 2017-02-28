package xujiantao.com.chuangwen;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.*;

import common.Function;
import security.DES;

public class ForgetPasswordActivity extends Fragment implements Validator.ValidationListener
{
    private static final String TAG  = "找回密码Activity ============ ";
    private InputMethodManager imm;

    @NotEmpty
    @Email
    @Order(1)
    private EditText email;

    private RelativeLayout progressBar;

    private Validator validator = new Validator(this);

    private ViewGroup vg;

    private DES desCrypt = new DES();


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.forget_password, container, false);
        root.findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getFragmentManager().beginTransaction().addToBackStack(null)
                        .replace(R.id.container, new SignUpActivity())
                        .commit();
            }
        });

        root.findViewById(R.id.signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getFragmentManager().beginTransaction().addToBackStack(null)
                        .replace(R.id.container, new SignInActivity())
                        .commit();
            }
        });

        email = (EditText) root.findViewById(R.id.account);

        progressBar = (RelativeLayout) root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        validator.setValidationMode(Validator.Mode.IMMEDIATE);
        validator.setValidationListener(this);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        vg = container;

        root.findViewById(R.id.forget_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                imm.hideSoftInputFromWindow(email.getWindowToken(), 0);

                Function.checkNetworkMsg(getActivity(), container);

                if(Function.isNetworkConnected(getActivity()))
                {
                    progressBar.setVisibility(View.VISIBLE);
                    validator.validate();
                }
                Log.i(TAG, " 密码找回中...");
            }
        });

        return root;
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

                Log.i(TAG, "###### " + returnStatus);

                progressBar.setVisibility(View.GONE);

                Snackbar snackbar = Snackbar.make(vg, returnMessage, Snackbar.LENGTH_LONG).setActionTextColor(Color.RED);
                Function.setSnackbarMessageTextColor(snackbar,Color.GREEN);
                snackbar.show();

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

    private void sendPasswordEmail()
    {
        String url = "http://api.xujiantao.com/account/forget_password";
        StringRequest request = new StringRequest(Request.Method.POST, url, new ResponseListener(), new ResponseErrorListener()){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> map = new HashMap<String, String>();
                map.put("email", email.getText().toString());
                return map;
            }
        };
        Volley.newRequestQueue(getActivity().getApplicationContext()).add(request);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onValidationSucceeded()
    {
        sendPasswordEmail();
    }

    @Override
    public void onValidationFailed(List<ValidationError> validationErrors)
    {
        for(ValidationError error : validationErrors)
        {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());

            progressBar.setVisibility(View.GONE);
            if(view instanceof EditText)
            {
                ((EditText) view).setError(message);
            }
            else
            {
                Snackbar snackbar = Snackbar.make(vg, message, Snackbar.LENGTH_LONG).setActionTextColor(Color.RED);
                Function.setSnackbarMessageTextColor(snackbar,Color.GREEN);
                snackbar.show();
            }
        }
    }
}