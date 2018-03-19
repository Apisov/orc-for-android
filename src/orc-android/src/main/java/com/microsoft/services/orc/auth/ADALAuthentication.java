package com.microsoft.services.orc.auth;

import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationException;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.services.orc.http.Credentials;
import com.microsoft.services.orc.http.impl.OAuthCredentials;

public class ADALAuthentication implements AuthenticationCredentials {

    private AuthenticationContext context;
    private String resourceId;
    private String clientId;

    public ADALAuthentication(AuthenticationContext context, String resourceId, String clientId) {
        this.context = context;
        this.resourceId = resourceId;
        this.clientId = clientId;
    }

    public Credentials getCredentials() {
        AuthenticationResult result = null;
        try {
            result = this.context.acquireTokenSilentSync(resourceId, clientId, null);
        } catch (AuthenticationException | InterruptedException e) {
            e.printStackTrace();
        }
        return new OAuthCredentials(result.getAccessToken());
    }
}
