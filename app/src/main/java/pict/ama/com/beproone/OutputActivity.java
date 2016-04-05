package pict.ama.com.beproone;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OutputActivity extends AppCompatActivity
{
    TextView tv,tv_json;
    Button b;
    String s;
    JSONObject j=new JSONObject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.out);
        Intent intent=getIntent();
        s=intent.getStringExtra("Rec Text");
        String delimit="MRP";
        String delimit_e="\n";
        String[] token=s.split(delimit);
        //String[] fin_token=token[1].split(delimit_e);
       try {
            j.put("string",s);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        tv=(TextView)findViewById(R.id.textView);
        tv_json=(TextView)findViewById(R.id.textView2);
        tv.setText(s);
        b=(Button)findViewById(R.id.button4);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button4:
                        String URL = "https://mighty-sea-77814.herokuapp.com/search/get_text/";
                        JsonObjectRequest searchRequest = new JsonObjectRequest(URL, j, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("Token Success", response.toString());
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.e("Token Send Error", error.getMessage());
                            }
                        });

                        searchRequest.setRetryPolicy(new DefaultRetryPolicy(
                                5000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        //Add a request (in this example, called stringRequest) to your RequestQueue.
                        VolleyStringRequestQueue.getInstance(getApplicationContext()).addToRequestQueue(searchRequest);
                }
            }
        });
       // tv_json.setText(j.toString());
        MightySeaAPI.search(this,s,new MightySeaAPI.IAPICallback(){
            @Override
            public void success(String response)
            {
                Toast.makeText(getApplicationContext(), "Successfully sent the string!", Toast.LENGTH_LONG);
            }
            @Override
            public void failure(String message)
            {
                Toast.makeText(getApplicationContext(),"UnSuccessfull!",Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
