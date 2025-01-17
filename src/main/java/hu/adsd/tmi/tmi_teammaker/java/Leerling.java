package hu.adsd.tmi.tmi_teammaker.java;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Leerling {
    private final Connection con = new Database().Con();

    private final String name;
    private final int klasid;
    private final JSONArray kwaliteiten;
    private final JSONArray producten;
    private final Boolean isCaptain;
    private Integer team;
    private Integer leerlingId;

    public Leerling(String name, int klasid, JSONArray kwaliteiten, JSONArray producten, Boolean isCaptain, int team) {
        this.name = name;
        this.klasid = klasid;
        this.kwaliteiten = kwaliteiten;
        this.producten = producten;
        this.isCaptain = isCaptain;
        this.team = team;
    }

    public Integer getLeerlingId() {
        return leerlingId;
    }

    public String GetName() {
        return this.name;
    }

    public int GetKlasid() {
        return this.klasid;
    }

    public JSONArray Getkwaliteiten() {
        return this.kwaliteiten;
    }

    public JSONArray GetProducten() {
        return this.producten;
    }

    public Boolean isCaptain() {
        return this.isCaptain;
    }

    public int GetTeam() {
        return this.team;
    }

    public void setLeerlingId(int leerlingId) {
        this.leerlingId = leerlingId;
    }

    public void storeleerling()
    {
        try {
            String query = "INSERT INTO Leerlingen (naam, klasid, kwaliteitenid, projectenid, captain, team) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = con.prepareStatement(query);

            statement.setString(1, GetName());
            statement.setInt(2, GetKlasid());
            statement.setString(3, Getkwaliteiten().toJSONString());
            statement.setString(4, GetProducten().toJSONString());
            statement.setBoolean(5, isCaptain());
            statement.setInt(6, GetTeam());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        setLeerlingId();
    }

    public void setLeerlingId() {
        try {
            String query = "SELECT * FROM Leerlingen WHERE captain = 0 ORDER BY leerlingid DESC LIMIT 1";
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet leerlingID = statement.executeQuery();
            if (leerlingID.next()) {
                this.leerlingId = leerlingID.getInt("leerlingid");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject getLeerlingKwaliteitenData() {
        JSONObject kwaliteiten = new JSONObject();

        for (Object id : Getkwaliteiten()) {
            String query = "SELECT naam FROM Kwaliteiten WHERE kwaliteitid = ?";
            PreparedStatement statement;
            try {
                statement = con.prepareStatement(query);
                statement.setInt(1, Integer.parseInt(id.toString()));
                ResultSet kwaliteitRs = statement.executeQuery();
                if (kwaliteitRs.next()) {
                    kwaliteiten.put(id, kwaliteitRs.getString("naam"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return  kwaliteiten;
    }

    public JSONObject getLeerlingProductenData() {
        JSONObject projecten = new JSONObject();

        for (Object id : GetProducten()) {
            String query = "SELECT naam FROM Projecten WHERE projectid = ?";
            PreparedStatement statement;
            try {
                statement = con.prepareStatement(query);
                statement.setInt(1, Integer.parseInt(id.toString()));
                ResultSet projectRs = statement.executeQuery();
                if (projectRs.next()) {
                    projecten.put(id, projectRs.getString("naam"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return  projecten;
    }
}
