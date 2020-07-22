package org.pophealth.googleFit;

import com.google.api.client.util.store.FileDataStoreFactory;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class FitServletUtil {

    private FileDataStoreFactory dataStoreFactory;
    private String clientId;
    private String clientSecret;

    @ConfigProperty(name ="google.fit.client.json.path")
    private String clientSecretPath;

    @ConfigProperty(name="google.fit.api.scopes")
    private List<String> fitScopes;

    @ConfigProperty(name="google.fit.api.token.store")
    private String credDataStore;

    @PostConstruct
    public void init() {
        try {
            //TODO
            dataStoreFactory = new FileDataStoreFactory(new File(credDataStore));
            String jsonStr = IOUtils.toString(new FileReader(clientSecretPath));
            JsonObject jsonObj = new JsonObject(jsonStr);

            this.clientId = jsonObj.getJsonObject("web").getString("client_id");
            this.clientSecret = jsonObj.getJsonObject("web").getString("client_secret");

        }catch(Exception e){
            //TODO logger
            e.printStackTrace();
        }
    }

    public FileDataStoreFactory getDataStoreFactory() {
        return dataStoreFactory;
    }

    public void setDataStoreFactory(FileDataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientSecretPath() {
        return clientSecretPath;
    }

    public void setClientSecretPath(String clientSecretPath) {
        this.clientSecretPath = clientSecretPath;
    }

    public List<String> getFitScopes() {
        return fitScopes;
    }

    public void setFitScopes(List<String> fitScopes) {
        this.fitScopes = fitScopes;
    }
}
