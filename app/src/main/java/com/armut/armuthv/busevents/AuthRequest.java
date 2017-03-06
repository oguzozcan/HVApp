package com.armut.armuthv.busevents;

/**
 * Created by oguzemreozcan on 15/06/16.
 */
public class AuthRequest {

    private final String userName;
    private final String password;
    private final String grantType;
    private final String clientId;
    private final String impersonate;

    public AuthRequest(final String userName, final String password, final String impersonate, final String grantType, final String clientId) {
        this.userName = userName;
        this.password = password;
        this.impersonate = impersonate;
        this.grantType = grantType;
        this.clientId = clientId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public String getImpersonate() {
        return impersonate;
    }
}
