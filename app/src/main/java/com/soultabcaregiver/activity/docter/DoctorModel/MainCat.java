package com.soultabcaregiver.activity.docter.DoctorModel;

/**
 * Created by poonam on 3/7/2019.
 */

public class MainCat {
    private String catId, catName, catDsc, catPic, type, typeid, isSocialList;

    public MainCat() {
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeid() {
        return typeid;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    private boolean checkSel;

    public int getCatIcon() {
        return catIcon;
    }

    public void setCatIcon(int catIcon) {
        this.catIcon = catIcon;
    }

    private int catIcon;

    public boolean isCheckSel() {
        return checkSel;
    }

    public void setCheckSel(boolean checkSel) {
        this.checkSel = checkSel;
    }

    public String getCatDsc() {
        return catDsc;
    }

    public void setCatDsc(String catDsc) {
        this.catDsc = catDsc;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getCatPic() {
        return catPic;
    }

    public void setCatPic(String catPic) {
        this.catPic = catPic;
    }

    public String getIsSocialList() {
        return isSocialList;
    }

    public void setIsSocialList(String isSocialList) {
        this.isSocialList = isSocialList;
    }

}
