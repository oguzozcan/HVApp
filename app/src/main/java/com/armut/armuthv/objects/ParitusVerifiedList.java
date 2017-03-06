package com.armut.armuthv.objects;

import java.util.ArrayList;

/**
 * Created by oguzemreozcan on 12/01/17.
 */

public class ParitusVerifiedList {

    private ArrayList<ParitusVerifyObject> verifiedAddressList;

    public ArrayList<ParitusVerifyObject> getVerifiedAddressList() {
        if(verifiedAddressList == null){
            verifiedAddressList = new ArrayList<>();
        }
        return verifiedAddressList;
    }

    public void setVerifiedAddressList(ArrayList<ParitusVerifyObject> verifiedAddressList) {
        this.verifiedAddressList = verifiedAddressList;
    }
}
