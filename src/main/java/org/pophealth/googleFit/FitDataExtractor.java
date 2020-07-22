package org.pophealth.googleFit;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.fitness.Fitness;
import com.google.api.services.fitness.model.Dataset;
import com.google.api.services.fitness.model.ListDataSourcesResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.pophealth.model.HealthData;

public class FitDataExtractor {

    private final Logger log = LoggerFactory.getLogger(FitServletCallback.class);
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "PopHealthWebClient";

    public List<HealthData> extractData(Credential credential, HttpTransport httpTransport) throws Exception {

        Fitness client = new Fitness.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

        ListDataSourcesResponse dataSourcesResponse = client.users().dataSources().list("me").execute();
        dataSourcesResponse.getDataSource().stream()
                .forEach(ds -> log.debug("Stream ID "+ds.getDataStreamId() + " Stream Name "+ds.getDataStreamName()));

        //TODO Configure data range for fetch
        long hoursToFetch = 48*10*60*60*1000;
        long end = System.currentTimeMillis();
        long start = end - hoursToFetch;

        Dataset dataset = client.users().dataSources()
                .datasets()
                .get("me", "derived:com.google.step_count.delta:com.google.android.gms:estimated_steps",
                        start*1000*1000+"-"+end*1000*1000)
                .execute();

       dataset.getPoint().stream().
               forEach(pt -> log.debug(new Date(pt.getEndTimeNanos()/(1000*1000)) + " Size " + pt.getValue().size() + " Value "+pt.getValue().get(0).getIntVal()));

       List<HealthData> stepData = processStepData(dataset);
       stepData.forEach(step -> log.info("Step data "+step.getMeasurementDate()+" "+step.getSteps()));
       return stepData;

    }

    //TODO clean this up. Shouldn't need to intermediate objects
    public List<HealthData> processStepData(Dataset stepDataset) {

        List<HealthData> stepData = stepDataset.getPoint().stream().
                map(pt -> {
                            HealthData healthData = new HealthData();
                            healthData.setSteps(pt.getValue().get(0).getIntVal());
                            healthData.setTimeNanos(pt.getEndTimeNanos());
                            //TODO this isn't correct. Need to use timezone info to get correct activity date
                            healthData.setMeasurementDate(Instant.ofEpochMilli(pt.getEndTimeNanos()/(1000*1000)).atZone(
                                    ZoneId.systemDefault()).toLocalDate());
                            return healthData;
                        }
                ).collect(Collectors.toList());

        Map<LocalDate, Integer> aggregateSteps = stepData.stream().collect(Collectors.groupingBy(HealthData::getMeasurementDate, Collectors.summingInt(HealthData::getSteps)));

        List<HealthData> results = new ArrayList<>();
        aggregateSteps.forEach((k,v) ->{
                                    HealthData data = new HealthData();
                                    data.setMeasurementDate(k);
                                    data.setSteps(v);
                                    results.add(data);
                                } );

        return results;
    }
}
