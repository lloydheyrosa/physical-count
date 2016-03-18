package com.chasetech.pcount;

/**
 * Created by ULTRABOOK on 10/7/2015.
 */
public class Items {

    public final String barcode;
    public final String itemcode;
    public final String desc;
    public final String costp;
    public final String price;
    public final int image;
    public final int colortype;

    public Items(String barcode, String itemcode, String desc, String costp, String price, int image, int colortype)
    {

        this.barcode = barcode;

        this.itemcode = itemcode;

        this.desc = desc;

        this.costp = costp;

        this.price = price;

        this.image = image;

        this.colortype = colortype;

    }

}