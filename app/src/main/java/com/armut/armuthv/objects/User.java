package com.armut.armuthv.objects;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;

/**
 * Created by oguzemreozcan on 14/04/15.
 */
public class User {

    @SerializedName("user")
    private UserInfo userInfo;
    @SerializedName("addresses")
    private Collection<Address> addresses;

//    @SerializedName("credit_cards")
//    private Collection<CreditCardInfo> creditCards;
//    public Collection<CreditCardInfo> getCreditCards() {
//        return creditCards;
//    }
//    public void setCreditCards(Collection<CreditCardInfo> creditCards) {
//        this.creditCards = creditCards;
//    }

    public Collection<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Collection<Address> addresses) {
        this.addresses = addresses;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }


    public static class ModifyPassword {
        @SerializedName("old_password")
        String oldPass;
        @SerializedName("new_password")
        String newPass;

        public ModifyPassword(String oldPass, String newPass) {
            this.oldPass = oldPass;
            this.newPass = newPass;
        }

        public String getOldPass() {
            return oldPass;
        }

        public void setOldPass(String oldPass) {
            this.oldPass = oldPass;
        }

        public String getNewPass() {
            return newPass;
        }

        public void setNewPass(String newPass) {
            this.newPass = newPass;
        }
    }
}
