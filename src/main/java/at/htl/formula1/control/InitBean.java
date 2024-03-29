package at.htl.formula1.control;

import at.htl.formula1.boundary.ResultsRestClient;
import at.htl.formula1.entity.Driver;
import at.htl.formula1.entity.Race;
import at.htl.formula1.entity.Team;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

@ApplicationScoped
@Transactional
public class InitBean {

    private static final String TEAM_FILE_NAME = "teams.csv";
    private static final String RACES_FILE_NAME = "races.csv";

    @PersistenceContext
    EntityManager em;

    @Inject
    ResultsRestClient client;


    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {

        readTeamsAndDriversFromFile(TEAM_FILE_NAME);
        readRacesFromFile(RACES_FILE_NAME);
        client.readResultsFromEndpoint();
    }

    /**
     * Einlesen der Datei "races.csv" und Speichern der Objekte in der Tabelle F1_RACE
     *
     * @param racesFileName
     */
    @Transactional
    private void readRacesFromFile(String racesFileName) {
        File file = new File(getClass().getClassLoader().getResource(racesFileName).getFile());

        try(Scanner scanner = new Scanner(file, "UTF-8")) {
            scanner.nextLine();
            while(scanner.hasNextLine()){
                String rows[] = scanner.nextLine().split(";");
                LocalDate ld = LocalDate.parse(rows[2], DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                Race race = new Race(Long.parseLong(rows[0]), rows[1], ld);
                em.persist(race);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Einlesen der Datei "teams.csv".
     * Das String-Array jeder einzelnen Zeile wird der Methode persistTeamAndDrivers(...)
     * übergeben
     *
     * @param teamFileName
     */
    @Transactional
    private void readTeamsAndDriversFromFile(String teamFileName) {
        File file = new File(getClass().getClassLoader().getResource(teamFileName).getFile());

        try(Scanner scanner = new Scanner(file, "UTF-8")) {
            scanner.nextLine();
            while(scanner.hasNextLine()){
               persistTeamAndDrivers(scanner.nextLine().split(";"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Es wird überprüft ob es das übergebene Team schon in der Tabelle F1_TEAM gibt.
     * Falls nicht, wird das Team in der Tabelle gespeichert.
     * Wenn es das Team schon gibt, dann liest man das Team aus der Tabelle und
     * erstellt ein Objekt (der Klasse Team).
     * Dieses Objekt wird verwendet, um die Fahrer mit Ihrem jeweiligen Team
     * in der Tabelle F!_DRIVER zu speichern.
     *
     * @param line String-Array mit den einzelnen Werten der csv-Datei
     */

    private void persistTeamAndDrivers(String[] line) {

        Team team = null;
        try{
            team = em.createNamedQuery("Team.getByName", Team.class).setParameter("NAME", line[0]).getSingleResult();
        }catch(NoResultException e){}

        if(team == null){
            Team t = new Team(line[0]);
            em.persist(t);
            Driver driver1 = new Driver(line[1], t);
            Driver driver2 = new Driver(line[2], t);
            em.persist(driver1);
            em.persist(driver2);
        }


    }


}
