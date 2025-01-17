package hu.adsd.tmi.tmi_teammaker.java;


import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LeerlingKlas {
    private final Connection con = new Database().Con();

    private int klascode;
    private int kwaliteitenbladId;
    private ArrayList<Leerling> leerlingen = new ArrayList<>();

    public LeerlingKlas(int klascode) {
        this.klascode = klascode;
    }

    public int getKlascode() {
        return klascode;
    }

    public ArrayList<Leerling> getLeerlingen() {
        getLeerlingFromDB();
        return this.leerlingen;
    }

    public void addLeerling(Leerling leerling) {
        this.leerlingen.add(leerling);
    }

    public void getLeerlingFromDB(){
        try {
            String leerlingKlas = "SELECT * FROM Leerlingen WHERE klasId = ?";
            PreparedStatement statement = con.prepareStatement(leerlingKlas);
            statement.setInt(1, getKlascode());

            ResultSet leerlingenRs = statement.executeQuery();

            while (leerlingenRs.next()) {
                String naam = leerlingenRs.getString("naam");
                int klasid = leerlingenRs.getInt("klasid");
                JSONParser parser = new JSONParser();

                JSONArray kwaliteiten = (JSONArray) parser.parse(leerlingenRs.getString("kwaliteitenid"));
                JSONArray producten = (JSONArray) parser.parse(leerlingenRs.getString("projectenid"));
                Boolean  isCaptain = leerlingenRs.getBoolean("captain");
                int team = leerlingenRs.getInt("team");

                Leerling leerling = new Leerling(naam, klasid, kwaliteiten, producten, isCaptain, team);
                leerling.setLeerlingId(leerlingenRs.getInt("leerlingid"));

                addLeerling(leerling);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
}
