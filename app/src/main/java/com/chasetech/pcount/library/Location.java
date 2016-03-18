package com.chasetech.pcount.library;

/**
 * Created by Vinid on 10/25/2015.
 */
public class Location {

    public final int locationCode;

    public final String locationName;

    public int type;

    public final int multiple;

    public Location(int locationCode, String locationName, int type, int multiple) {

        this.locationCode = locationCode;

        this.locationName = locationName;

        this.type = type;

        this.multiple = multiple;

    }

}
