package hu.adsd.tmi.tmi_teammaker.java;

import jakarta.servlet.http.HttpServlet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@CrossOrigin
@RequestMapping("/docent_dashboard")
public class docentDashboard extends HttpServlet {
    private final Connection con = new Database().Con();

    @RequestMapping(value = "/generate_klascode", method = RequestMethod.GET)
    public Integer returnKlascode() throws SQLException {
        return generateKlascode();
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
        public void postData(@RequestBody String data) {
        JSONParser parser = new JSONParser();
        JSONObject projectNamesAndKlascode;
        try {
            // parse the given data into a JSON object
            projectNamesAndKlascode = (JSONObject) parser.parse(data);
            System.out.println(projectNamesAndKlascode);
            // get the klascode and projects from the JSON object
            int klascode = Integer.parseInt(projectNamesAndKlascode.get("klascode").toString());
            JSONArray projectsJson = (JSONArray) projectNamesAndKlascode.get("projects");

            // loop through the projects json and add every value to an arraylist
            List<String> projects = new ArrayList<>();
            for (int i = 0; i < projectsJson.size(); i++) {
                String item = (String) projectsJson.get(i);
                projects.add(item);
            }
            storeProjects(klascode,projects);


        } catch (ParseException e) {
            System.out.println("Could not convert project data to JSON format");
        }
    }

    @RequestMapping(value = "/confirmBladid", method = RequestMethod.POST)
        public void postBladData(@RequestBody String data) {
            JSONParser parser = new JSONParser();
            JSONObject klascodeAndBladID;
            try {
                // parse the given data into a JSON object
                klascodeAndBladID = (JSONObject) parser.parse(data);
                System.out.println(klascodeAndBladID);
                // get the klascode and bladid from the JSON object
                int klascode = Integer.parseInt(klascodeAndBladID.get("klascode").toString());
                int bladId = Integer.parseInt(klascodeAndBladID.get("bladid").toString());

                setBladId(klascode,bladId);


            } catch (ParseException e) {
                System.out.println("Could not convert project data to JSON format");
            }
    }

    private int generateKlascode() throws SQLException {
        int newKlascode = newRandomKlascode();

        // Check if the klascode already exists, if so, generate a new one
        while (KlascodeExists(newKlascode)) {
            System.out.println("code already exists, generating new code");
            newKlascode = newRandomKlascode();
        }

        storeKlascode(newKlascode);
        return newKlascode;
    }

    private int newRandomKlascode() {
        // Create a new instance of the Random class
        Random rand = new Random();

        // Generate a random integer between 0 (inclusive) and 1,000,000 (exclusive)
        int randomNum = rand.nextInt(1000000);

        // Add leading zeros if necessary
        if (randomNum < 100000) {
            randomNum += 100000;
        }
        return randomNum;
    }

    private boolean KlascodeExists(int klascode) throws SQLException {
        // check if the klascode exists in the database
        // prepare query
        String query = "SELECT EXISTS(SELECT 1 FROM Klassen WHERE klascode = ?)";
        PreparedStatement statement = con.prepareStatement(query);

        //define values in the query
        statement.setInt(1, klascode);

        // get the result set from executing the query
        ResultSet rs =  statement.executeQuery();

        // set the exists value based on if the resultset is not empty
        boolean exists = false;
        if (rs.next()) {
            exists = rs.getBoolean(1);
        }
        return exists;
    }

    private void storeKlascode(int klascode) throws SQLException {
        // store the klascode into the database, with a kwaliteitenblad id
        // prepare query
        String query = "INSERT INTO Klassen (klascode, kwaliteitenbladid) VALUES (?, ?)";
        PreparedStatement statement = con.prepareStatement(query);

        //define values in the query
        statement.setInt(1, klascode);
        statement.setInt(2, 1);

        // execute query
        statement.executeUpdate();
    }

    private void storeProjects(int klascode, List<String> projects) {
        // store the projects in the database, with the klascode
        for (String project: projects) {
            // prepare query
            try {
                String query = "INSERT INTO Projecten (klasid, naam) VALUES (?, ?)";
                PreparedStatement statement = con.prepareStatement(query);

                // define values in the query
                statement.setInt(1, klascode);
                statement.setString(2, project);

                // execute query
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void setBladId(int klascode, int bladid){
        try {
            String query = "UPDATE Klassen SET kwaliteitenbladid = ? WHERE klascode = ?";
            PreparedStatement statement = con.prepareStatement(query);

            statement.setInt(1,bladid);
            statement.setInt(2,klascode);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/get_kwaliteitenbladen", method = RequestMethod.GET)
    private String getKwaliteitenbladen() {
        JSONArray kwaliteitenbladenArray = new JSONArray();
        try {
            String query = "SELECT * FROM Kwaliteitenbladeren";
            PreparedStatement statement = con.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                JSONObject kwaliteitenbladObject = new JSONObject();

                String naam = resultSet.getString("naam");
                String kwaliteiten = resultSet.getString("kwaliteitid");
                String bladId = resultSet.getString("kwaliteitenbladid");

                kwaliteitenbladObject.put("naam", naam);
                kwaliteitenbladObject.put("kwaliteiten", kwaliteiten);
                kwaliteitenbladObject.put("bladId", bladId);

                kwaliteitenbladenArray.add(kwaliteitenbladObject);
            }
            return kwaliteitenbladenArray.toJSONString();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
