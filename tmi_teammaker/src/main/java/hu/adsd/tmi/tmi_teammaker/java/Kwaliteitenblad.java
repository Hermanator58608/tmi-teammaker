package hu.adsd.tmi.tmi_teammaker.java;

import java.sql.*;
import java.util.Arrays;

import jakarta.servlet.http.HttpServlet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Controller
@CrossOrigin(origins = "http://localhost:*")
@RequestMapping("/Kwaliteitenblad")
public class Kwaliteitenblad extends HttpServlet {

    private final Connection con = new Database().Con();
    private final SimpMessagingTemplate messagingTemplate;
    public int bladid = 1;

    @Autowired
    public Kwaliteitenblad(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RequestMapping(value = "/Klascode", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> handleRequest(@RequestParam String klascode) {
        return ResponseEntity.ok(klascode);
    }

    @GetMapping(value = "/Kwaliteiten")
    @ResponseBody
    public String getKwaliteiten(String klascode) throws SQLException{

        int klascodeid = Integer.parseInt(klascode);
        // initates kwaliteitenbladid
        String kwaliteitenbladid ="SELECT kwaliteitenbladid FROM Klassen WHERE klascode = ?";
        PreparedStatement statement1 = con.prepareStatement(kwaliteitenbladid);
        statement1.setInt(1,klascodeid);
        ResultSet kwaliteitenbladids = statement1.executeQuery();

        if(kwaliteitenbladids.next())
        {
            bladid = kwaliteitenbladids.getInt("kwaliteitenbladid");
        }

        //vergelijkt de kwaliteitenids van een kwaliteiten
        String kwaliteitenblad = "SELECT * FROM Kwaliteitenbladeren WHERE kwaliteitenbladid = ?";
        PreparedStatement statement = con.prepareStatement(kwaliteitenblad);
        statement.setInt(1, bladid);
        ResultSet kwaliteitenbladrs = statement.executeQuery();

        JSONArray kwaliteitenArray = new JSONArray();

        if(kwaliteitenbladrs.next()) {
            // Makes
            String Stringkwaliteitenids = kwaliteitenbladrs.getString("kwaliteitid");

            String[] StringArrKwaliteitenids = Stringkwaliteitenids.split(",");
            int[] IntKwaliteitenids = new int[StringArrKwaliteitenids.length];

            for (int i = 0; i < StringArrKwaliteitenids.length; i++) {
                IntKwaliteitenids[i] = Integer.parseInt(StringArrKwaliteitenids[i]);
            }
            //checks for each number in the kwaliteitenids and returns JSONarray with naam and omschrijvingen
            for (Integer g : IntKwaliteitenids
            ) {
                JSONObject kwaliteitenObject = new JSONObject();
                int currentKwaliteitid = g;

                String kwaliteiten = "SELECT * FROM Kwaliteiten WHERE kwaliteitid = ?";
                PreparedStatement statement3 = con.prepareStatement(kwaliteiten);
                statement3.setInt(1,currentKwaliteitid);
                ResultSet kwaliteitenrs = statement3.executeQuery();

                if(kwaliteitenrs.next())
                {
                        kwaliteitenObject.put("Naam",kwaliteitenrs.getString("naam"));
                        kwaliteitenObject.put("Omschrijving", kwaliteitenrs.getString("omschrijving"));
                        kwaliteitenObject.put("KwaliteitId", kwaliteitenrs.getString("kwaliteitid"));
                }
                kwaliteitenArray.add(kwaliteitenObject);
                kwaliteitenrs.close();
            }
        }

        return kwaliteitenArray.toJSONString();
    }
    @GetMapping(value = "/setHeader")
    @ResponseBody
    public String setHeader(){
        System.out.println(bladid);
        String headerString = "";
        if (bladid == 1){
            headerString = "Kruis drie dingen aan die na het project over jou gezegd zouden kunnen worden.";
        }
        else{
            headerString = "Kruis een ding aan dat na het project over jou gezegd zou kunnen worden.";
        }
        return  headerString;
    }


    @GetMapping(value = "/Projecten")
    @ResponseBody
    public String getProjecten(String klascode) throws SQLException
    {
        int klascodeId = Integer.parseInt(klascode);
        String projecten = "SELECT * FROM Projecten WHERE klasid = ?";
        PreparedStatement statement = con.prepareStatement(projecten);
        statement.setInt(1,klascodeId);
        ResultSet projectenrs = statement.executeQuery();

        JSONArray projectenArray = new JSONArray();

        while(projectenrs.next()){
            JSONObject jsonObject = new JSONObject();
//            System.out.println(projectenrs.getString("naam"));
            jsonObject.put("Naam", projectenrs.getString("naam"));
            jsonObject.put("projectid", projectenrs.getString("projectid"));
            projectenArray.add(jsonObject);
        }
//        System.out.println(projectenArray);
        return projectenArray.toJSONString();
    }

    @RequestMapping(value = "/Bevestig", method = RequestMethod.POST)
    public void bevestig(@RequestBody String data) {
        JSONParser parser = new JSONParser();
        JSONObject leerlingData;
        try {
            leerlingData = (JSONObject) parser.parse(data);
            String naam = leerlingData.get("naam").toString();
            int klasid = Integer.parseInt(leerlingData.get("klasid").toString());
            JSONArray kwaliteiten = (JSONArray) parser.parse(leerlingData.get("kwaliteiten").toString());
            JSONArray producten = (JSONArray) parser.parse(leerlingData.get("projecten").toString());
            String captainvalue = leerlingData.get("captain").toString();
            boolean captain = Boolean.parseBoolean(captainvalue);

            Leerling leerling = new Leerling(naam, klasid, kwaliteiten, producten, captain, 0);
            leerling.storeleerling();
            sendStudentData(leerling);
        }
         catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Sends student data to the appropriate destination based on whether the student is a captain or not.
     *
     * @param leerling JSONObject containing student data
     */
    private void sendStudentData(Leerling leerling) {
        if (leerling.isCaptain()) {
            sendCaptainData(leerling, Integer.toString(leerling.GetKlasid()));
        } else {
            sendLeerlingData(leerling, Integer.toString(leerling.GetKlasid()));
        }
    }

    /**
     * Sends captain data to the specified destination.
     *
     * @param captain  JSONObject containing captain data
     * @param klasId   String representing the class ID
     */
    @MessageMapping("/captainData/{klasId}")
    private void sendCaptainData(@Payload Leerling captain, @DestinationVariable String klasId) {
        JSONObject captainJSON = new JSONObject();
        captainJSON.put("naam", captain.GetName());
        captainJSON.put("kwaliteitNamen", captain.getLeerlingKwaliteitenData());
        captainJSON.put("projectNamen", captain.getLeerlingProductenData());
        captainJSON.put("leerlingId", captain.getLeerlingId());
        messagingTemplate.convertAndSend("/topic/captainData/" + klasId, captainJSON);
    }

    /**
     * Sends student data to the specified destination.
     *
     * @param leerling JSONObject containing student data
     * @param klasId   String representing the class ID
     */
    @MessageMapping("/leerlingData/{klasId}")
    private void sendLeerlingData(@Payload Leerling leerling, @DestinationVariable String klasId) {
        JSONObject leerlingJSON = new JSONObject();
        leerlingJSON.put("naam", leerling.GetName());
        leerlingJSON.put("kwaliteitNamen", leerling.getLeerlingKwaliteitenData());
        leerlingJSON.put("projectNamen", leerling.getLeerlingProductenData());
        leerlingJSON.put("leerlingId", leerling.getLeerlingId());
        messagingTemplate.convertAndSend("/topic/leerlingData/" + klasId, leerlingJSON);
    }


}