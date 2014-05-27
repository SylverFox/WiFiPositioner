package nl.utwente.wifipositioner;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener,DataListener {

    private TcpDumpHandle tcpDumpHandle;
    private GPSHandle gpsHandle;
    private SQLiteDatabaseHandle sqLiteHandle;

    private TextView outputfield;
    private TextView locationfield;

    private Location currentLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        gpsHandle = new GPSHandle(this,locationManager);
        sqLiteHandle = new SQLiteDatabaseHandle(getApplicationContext());

        Button startcapturebutton = (Button)findViewById(R.id.startcapturebutton);
        startcapturebutton.setOnClickListener(this);
        Button stopcapturebutton = (Button)findViewById(R.id.stopcapturebutton);
        stopcapturebutton.setOnClickListener(this);
        outputfield = (TextView)findViewById(R.id.outputfield);
        locationfield = (TextView)findViewById(R.id.locationtextview);
    }

    @Override
    public void onDestroy() {
        if(tcpDumpHandle != null)
            tcpDumpHandle.shutdown();
        super.onDestroy();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id) {
            case R.id.startcapturebutton:
                if(tcpDumpHandle == null) {
                    tcpDumpHandle = new TcpDumpHandle(this);
                    tcpDumpHandle.start();
                } else {
                    Toast.makeText(getApplicationContext(),"Capture already started",1000).show();
                }

                break;
            case R.id.stopcapturebutton:
                if(tcpDumpHandle != null) {
                    tcpDumpHandle.shutdown();
                }
                tcpDumpHandle = null;
                break;
            default:
                break;
        }
    }

    private int i = 0;
    @Override
    public void onConsoleMessage(String message) {
        i++;
        if(message == null)
            return;
        else if(message.isEmpty())
            return;

        message = message.trim();

        if(currentLocation != null && message.contains("signal") && message.contains("SA")) {
            Pair<String,String> data = parseLine(message);
            sqLiteHandle.addRecord(currentLocation,data.first,data.second);
            if(i%100 == 0)
                postMessage("MAC: "+data.first+" - RSSI: "+data.second+"\n");
        }
    }

    @Override
    public void onGPSUpdate(Location location) {
        currentLocation = location;
        locationfield.setText("GPS: " + location.getLatitude() + "," + location.getLongitude());
    }

    private Pair<String,String> parseLine(String line) {
        String mac = line.replaceAll(".*SA:([\\w:]+).*","$1");
        String rssi = line.replaceAll(".*(-.*)dB signal.*", "$1");
        return new Pair<String, String>(mac,rssi);
    }

    private void postMessage(final String message) {
        outputfield.post(new Runnable() {
            @Override
            public void run() {
                outputfield.append(message);
                ((ScrollView)findViewById(R.id.scrollView)).fullScroll(View.FOCUS_DOWN);
            }
        });
    }

}
