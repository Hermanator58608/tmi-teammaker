package hu.adsd.tmi.tmi_teammaker.java;

import org.json.simple.JSONArray;

public class SimpelLeerling extends Leerling {
    public SimpelLeerling(String name, int team, Boolean isCaptain) {
        super(name, 0, new JSONArray(), new JSONArray(), isCaptain, team);
    }
}
