package pict.ama.com.beproone;

/**
 * Created by root on 4/5/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class MessageAdapter extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources res;
    ProductModel tempValues = null;

    /*************  CustomAdapter Constructor *****************/
    public MessageAdapter(Activity a, ArrayList d, Resources resLocal) {

        /********** Take passed values **********/
        activity = a;
        data = d;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater)activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {
        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView name;
        public TextView website;
        public TextView price;
        public TextView listprice;
        public ImageView imageLink;
    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {

            /****** Inflate adapter_message.xmlsage.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.product_layout, parent, false);

            /****** View Holder Object to contain adapter_message_message.xml file elements ******/

            holder = new ViewHolder();
            holder.name = (TextView) vi.findViewById(R.id.textView3);
            holder.website = (TextView) vi.findViewById(R.id.textView4);
            holder.price = (TextView) vi.findViewById(R.id.textView5);
            holder.listprice = (TextView) vi.findViewById(R.id.textView6);
            holder.imageLink = (ImageView) vi.findViewById(R.id.imageView2);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() <= 0) {
            holder.name.setText("");
            holder.price.setText("");
            holder.listprice.setText("");
            holder.website.setText("");
            Bitmap icon = BitmapFactory.decodeResource(vi.getContext().getResources(), R.drawable.abc_btn_radio_material);
            holder.imageLink.setImageBitmap(icon);
        } else {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (ProductModel) data.get(position);

            /************  Set Model values in Holder elements ***********/
            holder.name.setText(tempValues.getName());
            holder.price.setText("Offer price:" + tempValues.getPrice());
            holder.listprice.setText(""+tempValues.getListprice());
            holder.website.setText(tempValues.getWebsite());
            final Context c = vi.getContext();
            Picasso.with(c).load(tempValues.getImageUrl()).into(new com.squareup.picasso.Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Bitmap bmapScaled = bitmap.createScaledBitmap(bitmap, 200, 300, true);
                    holder.imageLink.setImageBitmap(bmapScaled);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.e("MessageAdapter","url failed");

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
        return vi;
    }
    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }
}
