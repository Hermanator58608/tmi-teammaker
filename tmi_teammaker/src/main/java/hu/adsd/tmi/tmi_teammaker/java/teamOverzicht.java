package hu.adsd.tmi.tmi_teammaker.java;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/teamOverzicht")
public class teamOverzicht {

    private JSONArray teamsArray;

    public void setTeamsArray(JSONArray teamsArray) {
        this.teamsArray = teamsArray;
    }

    public JSONArray getTeamsArray() {
        return teamsArray;
    }

    @RequestMapping(value = "/klasArray", method = RequestMethod.POST)
    public void toonNamen(@RequestBody String data)
    {
        System.out.println("test");
        JSONParser parser = new JSONParser();
        JSONArray teamArray;
        try {
            teamArray = (JSONArray) parser.parse(data);
            setTeamsArray(teamArray);
        }
        catch (ParseException e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/teamArray")
    @ResponseBody
    public String getTeamArray()
    {
        JSONArray jsonArray = getTeamsArray();
        System.out.println(jsonArray);
        return jsonArray.toJSONString();
    }

}
