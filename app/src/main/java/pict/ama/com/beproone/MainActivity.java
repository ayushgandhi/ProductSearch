package pict.ama.com.beproone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    ProgressDialog progress;
    String rectext;
    Boolean fs;
    Button b1,b2,b3;
    Bitmap bmapScaled;
    ImageView iv;
    private int PICK_IMAGE,h=320,w=320;
    private String DATA_PATH="/storage/emulated/0/Android/data/pict.ama.com.beproone/files/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp=getSharedPreferences("Language file copied",MODE_PRIVATE);
        fs=sp.getBoolean("Copied",false);
        if(fs==false)
        {
            copyAssets();
            SharedPreferences.Editor ed = sp.edit();
            ed.putBoolean("Copied", true);
            ed.commit();
        }
        setContentView(R.layout.activity_main);
        b1=(Button)findViewById(R.id.button);
        b2=(Button)findViewById(R.id.button2);
        b3=(Button)findViewById(R.id.button3);
        iv=(ImageView)findViewById(R.id.imageView);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
    }
    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
            String filename = "eng.traineddata";
            InputStream in = null;
        OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File o1=getExternalFilesDir(null);
                File outFile = new File(getExternalFilesDir(null)+"/tessdata", filename);
                //DATA_PATH=o1.getAbsolutePath();
                DATA_PATH=getExternalFilesDir(null)+"";
                Log.e("Data path:",DATA_PATH);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.button:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, ""), PICK_IMAGE);
                break;
            case R.id.button2:
               /* progress=new ProgressDialog(this);
                Thread t=new Thread() {
                    public void run() {
                        try {
                            Log.e("Data path:", DATA_PATH);
                            TessBaseAPI tess=new TessBaseAPI();
                            tess.init(DATA_PATH, "eng");
                            tess.setImage(bmapScaled);
                            rectext=tess.getUTF8Text();
                            tess.end();
                            Log.e("Text recognized:", rectext);
                        } catch(Exception e) {
                            Log.e("threadmessage",e.getMessage());
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress = ProgressDialog.show(MainActivity.this, "Just a moment", "Analysing image for text", true);
                            }
                        });
                    }
                };
                t.start();
                try
                {
                    t.join();
                    progress.dismiss();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }*/
                new OCR().execute(bmapScaled);
                break;
            case R.id.button3:
                //copyAssets();
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != Activity.RESULT_CANCELED)
        {
            if (requestCode == PICK_IMAGE)
            {
                Uri selectedImageUri = data.getData();
                Bitmap bitmap;
                try
                {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
                    bmapScaled=bitmap.createScaledBitmap(bitmap, 3120, 3120, true);
                    iv.setImageBitmap(bmapScaled);
                }
                catch(FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }
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
    protected class OCR extends AsyncTask<Bitmap,Void,String>
    {
        ProgressDialog pd;
        @Override
        protected void onPreExecute()
        {
            pd=new ProgressDialog(MainActivity.this);
            pd= ProgressDialog.show(MainActivity.this, "Just a moment", "Analysing image for text", true);
        }
        @Override
        protected String doInBackground(Bitmap... b)
        {
            String s;
            Log.e("Data path:", DATA_PATH);
            TessBaseAPI tess=new TessBaseAPI();
            tess.init(DATA_PATH, "eng");
            tess.setImage(bmapScaled);
            s=tess.getUTF8Text();
            tess.end();
            Log.e("Text recognized:", s);
            return s;
        }
        @Override
        protected void onPostExecute(String s)
        {
            rectext=s;
            pd.dismiss();
            try
            {
                Class ourClass = Class.forName("pict.ama.com.beproone.OutputActivity");
                Intent ourIntent = new Intent(MainActivity.this, ourClass);
                ourIntent.putExtra("Rec Text",rectext);
                Log.e("hi","hi");
                startActivity(ourIntent);
            }
            catch(ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }
}
