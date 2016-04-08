package com.chasetech.pcount.library;

/**
 * Created by Vinid on 10/25/2015.
 */
public class Location {

    public final int locationCode;
    public final String locationName;
    public final String storecode;
    public final int channelID;
    public int type;
    public final int multiple;
    public final String channelDesc;
    public final String channelArea;

    public Location(int locationCode, String scode, String locationName, int type, int multiple, int channelid, String channeldesc, String channelarea) {
        this.locationCode = locationCode;
        this.locationName = locationName;
        this.type = type;
        this.multiple = multiple;
        this.channelDesc = channeldesc;
        this.channelArea = channelarea;
        this.storecode = scode;
        this.channelID = channelid;
    }

}
