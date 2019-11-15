package at.htl.formula1.boundary;

import at.htl.formula1.entity.Driver;
import at.htl.formula1.entity.Race;
import at.htl.formula1.entity.Result;

import javax.annotation.PostConstruct;
import javax.json.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.util.*;

@Path("results")
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
    @Path("/")
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
     * @param country des Rennens
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/winner/{country}")
    public Response findWinnerOfRace(@PathParam("country") String country) {
        Result result = em.createNamedQuery("Result.getRaceWinner", Result.class).setParameter("COUNTRY", country).getSingleResult();
        return Response.ok(result).build();
    }


    // Ergänzen Sie Ihre eigenen Methoden ...

    @GET
    @Path("raceswon")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Race> findRacedWonByTeam(@QueryParam("team") String team){
        List<Result> results = em.createNamedQuery("Result.getRacesWonByTeam", Result.class).setParameter("NAME", team).getResultList();
        List<Race> races = new LinkedList<>();
        for (Result result : results) {
            races.add(result.getRace());
        }
        return races;
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public List<JsonObject> getDrivers(){
        List<JsonObject> driverPoints = new LinkedList<>();
        List<Driver> drivers = em.createNamedQuery("Driver.findAll", Driver.class).getResultList();
        for (Driver driver : drivers) {
            int total = 0;
            List<Result> results = em.createNamedQuery("Result.getByDriverId", Result.class).setParameter("ID", driver.getId()).getResultList();
            for (Result result : results) {
                total += result.getPoints();
            }
            driverPoints.add(Json.createObjectBuilder().add("driver", driver.getName()).add("total", total). build());
        }
        return driverPoints;
    }
}
