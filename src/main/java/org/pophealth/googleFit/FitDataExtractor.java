package org.pophealth.googleFit;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.fitness.Fitness;
import com.google.api.services.fitness.model.Dataset;
import com.google.api.services.fitness.model.ListDataSourcesResponse;
import java.util.Date;

public class FitDataExtractor {


    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "PopHealthWebClient";

    public void extractData(Credential credential, HttpTransport httpTransport) throws Exception {

        Fitness client = new Fitness.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

        ListDataSourcesResponse dataSourcesResponse = client.users().dataSources().list("me").execute();
        dataSourcesResponse.getDataSource().stream()
                .forEach(ds -> System.out.println("Stream ID "+ds.getDataStreamId() + " Stream Name "+ds.getDataStreamName()));

        //TODO
        Dataset dataset = client.users().dataSources()
                .datasets()
                .get("me", "derived:com.google.step_count.delta:com.google.android.gms:estimated_steps",
                        "1544887669506000000-1595060469507000000")
                .execute();

        System.out.println("\n\n\n\n\n");
       dataset.getPoint().stream().forEach(pt -> System.out.println(new Date(pt.getEndTimeNanos()/(1000*1000)) + " Size " + pt.getValue().size() + " Value "+pt.getValue().get(0).getIntVal()));
//       System.out.println("\n\n\n\n\nDataSet "+dataset);

    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
//        1544887669506000000-1595060469507000000
        Date date = new Date(Long.parseLong("1584887669506"));
        System.out.println(date);
    }
}