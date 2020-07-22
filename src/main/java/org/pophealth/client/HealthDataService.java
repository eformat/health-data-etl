package org.pophealth.client;


import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.pophealth.model.HealthData;

@Path("")
@RegisterRestClient
public interface HealthDataService {

    @POST
    @Path("/healthRules")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    List<HealthData> postRules(List<HealthData> healthData);
}
