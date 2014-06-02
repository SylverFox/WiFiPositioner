package nl.utwente.wifipositioner;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity implements View.OnClickListener,DataListener {

    private TcpDumpHandle tcpDumpHandle;
    private GPSHandle gpsHandle;
    private SQLiteDatabaseHandle sqLiteHandle;

    private TextView outputfield;
    private TextView locationfield;
    private TextView totalpacketsView;
    private TextView totalrecordsView;
    private EditText sourcefilterText;

    private Location currentLocation;
    private String sourcefilter = "";

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
        totalpacketsView = (TextView)findViewById(R.id.totalpacketsView);
        totalrecordsView = (TextView)findViewById(R.id.totalrecordsView);
        sourcefilterText = (EditText)findViewById(R.id.sourcefilterText);

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
                String sf = sourcefilterText.getText().toString();
                if(tcpDumpHandle == null) {
                    if(validMAC(sf)) {
                        this.sourcefilter = sf.toLowerCase();
                        tcpDumpHandle = new TcpDumpHandle(this);
                        tcpDumpHandle.start();
                    } else {
                        Toast.makeText(getApplicationContext(),"Invalid MAC",1000).show();
                    }

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

    private boolean validMAC(String input) {
        if(input.length() == 0)
            return true;

        boolean output = true;
        if(input.length() != 17)
            output = false;
        input = input.replaceAll(":","");
        if(input.length() != 12)
            output = false;
        input = input.replaceAll("[[0-9][a-f][A-F]]*","");
        if(input.length() != 0)
            output = false;
        return output;
    }

    private int packets = 0;
    private int records = 0;
    @Override
    public void onConsoleMessage(String message) {
        if(message == null)
            return;
        else if(message.isEmpty())
            return;

        packets++;

        message = message.trim();

        if(currentLocation != null && message.contains("signal") && message.contains("SA")) {

            Pair<String,String> data = parseLine(message);
            if(sourcefilter.length() > 0) {
                if(data.first.equals(sourcefilter)) {
                    records++;
                    sqLiteHandle.addRecord(currentLocation,data.first,data.second);
                }
            } else {
                records++;
                sqLiteHandle.addRecord(currentLocation,data.first,data.second);
            }


            if(packets%100 == 0 || packets < 10)
                postMessage("MAC: "+data.first+" - RSSI: "+data.second+"\n");

            if(records%10 == 0) {
                totalrecordsView.post(new Runnable() {
                    @Override
                    public void run() {
                        totalrecordsView.setText("Total records: " + records);
                    }
                });
            }
        }

        if(packets%20 == 0) {
            totalpacketsView.post(new Runnable() {
                @Override
                public void run() {
                    totalpacketsView.setText("Total packets: " + packets);
                }
            });
        }
    }

    @Override
    public void onGPSUpdate(Location location) {
        currentLocation = location;
        locationfield.setText("GPS: " + location.getLatitude() + "," + location.getLongitude());
    }

    private Pair<String,String> parseLine(String line) {
        String mac = line.replaceAll(".*SA:([\\w:]+).*","$1").toLowerCase();
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
