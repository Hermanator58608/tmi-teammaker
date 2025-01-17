package hu.adsd.tmi.tmi_teammaker.java;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/teamMaker")
public class teamMaker {
    @GetMapping(value = "/leerlingen")
    @ResponseBody
    public String getLeerlingenFromDB(int klascode) {
        JSONArray leerlingenArray = new JSONArray();
        LeerlingKlas leerlingKlas = new LeerlingKlas(klascode);
        for (Leerling leerling : leerlingKlas.getLeerlingen()) {
            JSONObject leerlingJson = new JSONObject();
            leerlingJson.put("leerlingId", leerling.getLeerlingId());
            leerlingJson.put("projectNamen", leerling.getLeerlingProductenData());
            leerlingJson.put("naam", leerling.GetName());
            leerlingJson.put("kwaliteitNamen", leerling.getLeerlingKwaliteitenData());
            leerlingJson.put("captain", leerling.isCaptain());
            leerlingJson.put("team", leerling.GetTeam());

            leerlingenArray.add(leerlingJson);
        }

        return leerlingenArray.toJSONString();
    }

}
