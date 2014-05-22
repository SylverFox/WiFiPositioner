package nl.utwente.wifipositioner;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

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

        tcpDumpHandle = new TcpDumpHandle(this);
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        gpsHandle = new GPSHandle(this,locationManager);
        sqLiteHandle = new SQLiteDatabaseHandle(getApplicationContext());

        Button driverloadbutton = (Button)findViewById(R.id.loaddriverbutton);
        driverloadbutton.setOnClickListener(this);
        Button driverunloadbutton = (Button)findViewById(R.id.unloaddriverbutton);
        driverunloadbutton.setOnClickListener(this);
        Button startcapturebutton = (Button)findViewById(R.id.startcapturebutton);
        startcapturebutton.setOnClickListener(this);
        Button stopcapturebutton = (Button)findViewById(R.id.stopcapturebutton);
        stopcapturebutton.setOnClickListener(this);
        outputfield = (TextView)findViewById(R.id.outputfield);
        locationfield = (TextView)findViewById(R.id.locationtextview);
    }

    @Override
    public void onDestroy() {
        tcpDumpHandle.shutdown();
        super.onDestroy();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id) {
            case R.id.loaddriverbutton:
                tcpDumpHandle.loadCustomDriver();
                break;
            case R.id.unloaddriverbutton:
                tcpDumpHandle.unloadCustomDriver();
                break;
            case R.id.startcapturebutton:
                tcpDumpHandle.start();
                break;
            case R.id.stopcapturebutton:
                try {
                    tcpDumpHandle.stopCapture();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onConsoleMessage(String message) {
        if(message == null)
            return;
        else if(message.isEmpty())
            return;
        else if(currentLocation == null)
            return;

        message = message.trim();

        String mac = message.trim();
        String rssi = "";

        outputfield.append(message);
        outputfield.append("MAC: "+mac+" RSSI: "+rssi);
        sqLiteHandle.addRecord(currentLocation,mac,rssi);
    }

    @Override
    public void onGPSUpdate(Location location) {
        currentLocation = location;
        locationfield.setText("Location: " + location.getLatitude() + "," + location.getLongitude());
    }
}
