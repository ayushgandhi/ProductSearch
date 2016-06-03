package pict.ama.com.beproone;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.Integer.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by root on 4/5/16.
 */
public class ProductActivity extends AppCompatActivity
{
    private ListView lv;
    public ArrayList<ProductModel> c_list = new ArrayList<>(10);
    String s;
    MessageAdapter ca;
    Resources res1;
    JSONObject j;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_product_layout);
        Intent intent=getIntent();
        s=intent.getStringExtra("Response");
        res1=getResources();
        MessageAdapter ca = new MessageAdapter(ProductActivity.this, c_list, res1);
        lv=(ListView)findViewById(R.id.listView);
        lv.setAdapter(ca);
        onsuccess(s);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Class ourClass = Class.forName("pict.ama.com.beproone.ViewProductActivity");
                    Intent ourIntent = new Intent(ProductActivity.this, ourClass);
                    ourIntent.putExtra("Product model list",c_list);
                    ourIntent.putExtra("Position",position);
                    startActivity(ourIntent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void onsuccess(String response)
    {
        Log.e("ProductAcitvity",response);
        try
        {
            Log.e("ProductAcitvity","Get request sent");
            j=new JSONObject(response);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        Integer im;
        im=j.length();
        if(im>0) {
            JSONObject[] res = new JSONObject[im];
            for (int i = 0; i < im; i++) {
                try {
                    res[i] = j.getJSONObject(Integer.toString(i + 1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < im; i++) {
                ProductModel m = new ProductModel();
                try {
                    m.setName(res[i].getString("name"));
                    m.setID(res[i].getInt("id"));
                    m.setImageUrl(res[i].getString("imageLink"));
                    m.setWebsite(res[i].getString("referredFrom"));
                    m.setUrl(res[i].getString("url"));
                    if(m.getWebsite().equals("Jabong"))
                    {
                        m.setListprice(res[i].getInt("price"));
                        m.setPrice(res[i].getInt("listPrice"));
                    }
                    else {
                        m.setPrice(res[i].getInt("price"));
                        m.setListprice(res[i].getInt("listPrice"));
                    }
                    Log.e("ImageLink" + Integer.toString(i + 1), m.getImageUrl());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                c_list.add(i, m);
                res1 = getResources();
                ca = new MessageAdapter(ProductActivity.this, c_list, res1);
                lv.setAdapter(ca);
            }
        }
        else
        {
            ProductModel m = new ProductModel();
            m.setName("No results found");
            m.setID(1);
            m.setImageUrl("");
            //m.temp();
            m.setPrice(0);
            m.setListprice(0);
            m.setWebsite("Not Found");
            c_list.add(0, m);
            res1=getResources();
            ca=new MessageAdapter(ProductActivity.this,c_list,res1);
            lv.setAdapter(ca);
        }
    }
}
