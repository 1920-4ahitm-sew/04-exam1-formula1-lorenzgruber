package at.htl.formula1.boundary;

import at.htl.formula1.entity.Driver;
import at.htl.formula1.entity.Result;

import javax.annotation.PostConstruct;
import javax.json.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("result")
public class ResultsEndpoint {

    @PersistenceContext
    EntityManager em;

    @PostConstruct
    public void init(){ }

    /**
     * @param name als QueryParam einzulesen
     * @return JsonObject
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("name")
    public JsonObject getPointsSumOfDriver(
            @QueryParam("name") String name
    ) {
        List<Result> results = em.createNamedQuery("Result.getByDriverName", Result.class).setParameter("NAME", name).getResultList();
        int total = 0;
        for (Result result : results) {
            total += result.getPoints();
        }

        return Json.createObjectBuilder().add("driver", name).add("total", total). build();
    }

    /**
     * @param id des Rennens
     * @return
     */
    public Response findWinnerOfRace(long id) {
        return null;
    }


    // Ergänzen Sie Ihre eigenen Methoden ...

}
