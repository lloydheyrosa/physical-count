package com.chasetech.pcount.MKL;

/**
 * Created by Inid on 10/25/2015.
 */
public class PCount {

    public final int id;
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
    public final String itembarcode;
    public boolean updated;

    public PCount(int pcountid, String barcode, String desc, String category, String brand, String division, String subcate, int ig, int conversion, double fsovalue, int webid, int multi, String otherbarc, boolean isupdated)
    {
        this.id = pcountid;
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
        this.itembarcode = otherbarc;
        this.updated = isupdated;
    }

}
