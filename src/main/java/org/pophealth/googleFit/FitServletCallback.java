package org.pophealth.googleFit;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "OAuthCallback", urlPatterns = "/oauth2callback")
public class FitServletCallback extends AbstractAuthorizationCodeCallbackServlet {

    private final Logger log = LoggerFactory.getLogger(FitServletCallback.class);


    @Inject
    FitServletUtil fitUtil;

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
            throws ServletException, IOException {
        resp.sendRedirect("/fitAuth");
    }

    @Override
    protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
            throws ServletException, IOException {
        log.error("Received error in fit callback " + errorResponse);
        resp.sendError(SC_INTERNAL_SERVER_ERROR, "Something went wrong :(");
    }

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath("/oauth2callback");
        return url.build();
    }

    //TODO client creds in props
    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
                fitUtil.getClientId(),
                fitUtil.getClientSecret(),
                fitUtil.getFitScopes()).setDataStoreFactory(
                fitUtil.getDataStoreFactory()).setAccessType("offline").build();
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
        return "test";
    }
}
