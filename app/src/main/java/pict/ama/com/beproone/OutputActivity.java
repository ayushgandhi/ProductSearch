package pict.ama.com.beproone;

import android.app.Activity;
import android.app.DownloadManager;
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
import com.android.volley.Request;
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
    Button b,bl;
    String s;
    JSONObject j,j_r=new JSONObject();
    String re;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.out);
        Intent intent=getIntent();
        s=intent.getStringExtra("Rec Text");
       try {
           j=new JSONObject();
            j.put("string",s);
           Log.e("Converted JSON:",j+"");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        tv=(TextView)findViewById(R.id.textView);
        tv_json=(TextView)findViewById(R.id.textView2);
        tv.setText(s);
        bl=(Button)findViewById(R.id.button5);
        bl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button5:
                        String URL = "https://mighty-sea-77814.herokuapp.com/search/get_text/";
                        String URL1="http://192.168.0.112:8000/search/get_text/";
                        JsonObjectRequest searchRequest = new JsonObjectRequest(Request.Method.POST,URL, j, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                j_r=response;
                                re=response.toString();
                                Log.e("Json to string",re);
                                try {
                                    Class ourClass = Class.forName("pict.ama.com.beproone.ProductActivity");
                                    Intent ourIntent = new Intent(OutputActivity.this, ourClass);
                                    ourIntent.putExtra("Response",re);
                                    startActivity(ourIntent);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.e("Token Send Error", error.getMessage());
                                Toast.makeText(OutputActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
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
