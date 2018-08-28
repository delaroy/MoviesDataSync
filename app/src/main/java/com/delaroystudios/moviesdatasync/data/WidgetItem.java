package com.delaroystudios.moviesdatasync.data;

/**
 * Created by delaroy on 9/1/17.
 */

public class WidgetItem {
   public Integer _empID;
    public  String _name;
    public String _userrating;
    public String _picture;

    public WidgetItem(Integer empID, String name, String userrating, String picture) {
        this._empID = empID;
        this._name = name;
        this._userrating = userrating;
        this._picture = picture;
    }
}