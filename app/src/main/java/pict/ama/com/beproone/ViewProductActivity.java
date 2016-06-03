package pict.ama.com.beproone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by root on 6/3/16.
 */
public class ViewProductActivity extends AppCompatActivity
{
    private ArrayList<ProductModel> al=new ArrayList<>();
    private int pos;
    private ProductModel m;
    private TextView ofp,orp,name,des,ref;
    private ImageView iv;
    private Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_product_layout);
        setup();
        Intent intent=getIntent();
        al=(ArrayList<ProductModel>)intent.getSerializableExtra("Product model list");
        pos=intent.getIntExtra("Position",0);
        Log.e("Position",Integer.toString(pos));
        m=al.get(pos);
        Log.e("name","Naam hai "+m.getName());
        setDetails();
    }
    protected void setup()
    {
        ofp=(TextView)findViewById(R.id.textView11);
        orp=(TextView)findViewById(R.id.textView12);
        name=(TextView)findViewById(R.id.textView8);
        des=(TextView)findViewById(R.id.textView13);
        ref=(TextView)findViewById(R.id.textView14);
        iv=(ImageView)findViewById(R.id.imageView3);
        b=(Button)findViewById(R.id.button4);
    }
    protected void setDetails()
    {
        ofp.setText(Integer.toString(m.getListprice()));
        orp.setText(Integer.toString(m.getPrice()));
        name.setText(m.getName());
        des.setText("No description found");
        ref.setText("Referred from: "+m.getWebsite());
        Picasso.with(this).load(m.getImageUrl()).into(new com.squareup.picasso.Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {
                Bitmap bmapscaled=bitmap.createScaledBitmap(bitmap,600,600,true);
                iv.setImageBitmap(bmapscaled);
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e("MessageAdapter", "url failed");

            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button4:
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        if(m.getWebsite().equals("Jabong"))
                        {
                            intent.setData(Uri.parse("http://"+m.getUrl()));
                        }
                        else
                        {
                            intent.setData(Uri.parse(m.getUrl()));
                        }
                        startActivity(intent);
                        break;
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
