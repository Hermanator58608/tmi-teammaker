package hu.adsd.tmi.tmi_teammaker.java;

import jakarta.servlet.http.HttpServlet;

import java.sql.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/startpagina")
public class StartPagina extends HttpServlet {

    private final Connection con = new Database().Con();

    @RequestMapping(value = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String postStudentData(@RequestBody String data) throws SQLException {
        // parse JSON data
        JSONObject jsonData = (JSONObject) JSONValue.parse(data);

        String naam = jsonData.get("naam").toString();
        int klascode = Integer.parseInt(jsonData.get("klascode").toString());
        boolean isCaptain = (boolean) jsonData.get("isCaptain");

        // Hier Kan je naam, klascode en isCaptain meegeven aan het kwaliteitenblad

        // return een JSONObject with a Boolean if the given Klascode exists
        JSONObject klasCodeExist = new JSONObject();
        klasCodeExist.put("exists", KlascodeExists(klascode));
        return klasCodeExist.toJSONString();
    }

    private boolean KlascodeExists(int klascode) throws SQLException {
        //
        String query = "SELECT EXISTS(SELECT 1 FROM Klassen WHERE klascode = ?)";
        PreparedStatement statement = con.prepareStatement(query);
        statement.setInt(1, klascode);

        ResultSet rs =  statement.executeQuery();

        boolean exists = false;
        if (rs.next()) {
            exists = rs.getBoolean(1);
        }
        return exists;
    }
}
