package org.pophealth.googleFit;


import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.pophealth.client.HealthDataService;
import org.pophealth.model.HealthData;

@WebServlet(name = "FitAuth", urlPatterns = "/fitAuth")
public class FitServlet extends AbstractAuthorizationCodeServlet {

    HttpTransport transport =  new NetHttpTransport();

    @Inject
    FitServletUtil fitUtil;

    @Inject
    @RestClient
    HealthDataService healthDataService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  throws IOException {

        try {
            Credential credential = getCredential();
            FitDataExtractor extractor = new FitDataExtractor();
            List<HealthData> data = extractor.extractData(credential, transport);

            healthDataService.postRules(data);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath("/oauth2callback");
        return url.build();
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return new GoogleAuthorizationCodeFlow.Builder(
                transport ,
                JacksonFactory.getDefaultInstance(),
                fitUtil.getClientId(),
                fitUtil.getClientSecret(),
                fitUtil.getFitScopes())
                .setDataStoreFactory(fitUtil.getDataStoreFactory()).setAccessType("offline").build();
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
        return "test";
    }

}
