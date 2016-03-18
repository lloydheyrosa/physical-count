package com.chasetech.pcount.Assortment;

/**
 * Created by ULTRABOOK on 2/29/2016.
 */
public class Assortment {
    public final String barcode;
    public final String desc;
    public final String category;
    public final String brand;
    public final String division;
    public final String subcate;
    public int ig;
    public int conversion;
    public int sapc;
    public int whpc;
    public int whcs;
    public int so;
    public int fso;
    public int multi;
    public final double fsovalue;
    public final int webid;
    public boolean updated;

    public Assortment(String barcode, String desc, String category, String brand, String division, String subcate, int ig, int conversion, double fsovalue, int webid, int multi, boolean update)
    {
        this.barcode = barcode;
        this.desc = desc;
        this.category = category;
        this.brand = brand;
        this.division = division;
        this.subcate = subcate;
        this.ig = ig;
        this.conversion = conversion;
        this.sapc = 0;
        this.whpc = 0;
        this.whcs = 0;
        this.so = 0;
        this.fso = 0;
        this.fsovalue = fsovalue;
        this.webid = webid;
        this.multi = multi;
        this.updated = update;
    }
}
