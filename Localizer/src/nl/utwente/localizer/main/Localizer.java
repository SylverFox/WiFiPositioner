package nl.utwente.localizer.main;

import nl.utwente.localizer.sql.ImportDB;
import nl.utwente.localizer.sql.SQLHandle;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Joris on 02/06/2014.
 */
public class Localizer {
    public static void main(String[] args) { new Localizer(); }

    private final String DATABASE_REMOTE = "/data/data/nl.utwente.wifipositioner/databases/CaptureDatabase";
    private final String DATABASE_LOCAL = "res/CaptureDatabase";

    private SQLHandle sqlHandle;


    public Localizer() {
        ImportDB.importDB(DATABASE_REMOTE,DATABASE_LOCAL);
        sqlHandle = new SQLHandle(DATABASE_LOCAL);

        ArrayList<String> macs = getMacs();


        System.out.println("done");
    }

    private ArrayList<String> getMacs() {
        ArrayList<String> out = null;
        try {
            out = sqlHandle.getMacs();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }
}
