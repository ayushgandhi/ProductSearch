package pict.ama.com.beproone;

/**
 * Created by root on 4/5/16.
 */
public class ProductModel
{
    private String website;
    private String name;
    private String imageUrl;
    private int price;
    private int listprice;
    private int id;
    public void temp()
    {
        String s=imageUrl;
        s=s.substring(1,5)+":"+s.substring(5);
        imageUrl=s;
    }
    public int getID(){return id;}

    public void setID(int a){this.id=a;}

    public int getPrice() {
        return price;
    }

    public void setPrice(int pr) {
        this.price = pr;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String source) {
        this.website = source;
    }

    public int getListprice() {
        return listprice;
    }

    public void setListprice(int lp) {
        this.listprice = lp;
    }

    public void setName(String a)
    {
        this.name=a;
    }
    public String getName()
    {
        return name;
    }
    public void setImageUrl(String a)
    {
        this.imageUrl=a;
    }
    public String getImageUrl()
    {
        return imageUrl;
    }
}
