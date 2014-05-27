package nl.utwente.wifipositioner;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Joris on 22/05/2014.
 */
public class TcpDumpHandle extends Thread {

    private final String DEBUG_TAG = "TcpDumpHandle";
    private final String tcpDumpComm = "./tcpdump-arm -l -i wlan0";
    private final String toFolder = "cd /data/bcmon";
    private final String loadDriverComm = "sh setup.sh";
    private final String unloadDriverComm = "sh unsetup.sh";

    private Process console;
    private BufferedReader dataIn;
    private DataOutputStream dataOut;
    private BufferedReader dataErr;

    private DataListener listener;

    private boolean continueCapture = false;

    public TcpDumpHandle(DataListener listener) {
        this.listener = listener;
    }

    public void run() {
        Log.d(DEBUG_TAG, "Starting run");

        boolean ok = false;
        try {
            ok = startProcess();
        } catch (IOException e) {
            Log.e(DEBUG_TAG,"Unable to start process");
            e.printStackTrace();
            ok = false;
        }

        if(ok) {
            Log.d(DEBUG_TAG, "Startup done. Device ready");
            continueCapture = true;
        } else {
            Log.e(DEBUG_TAG,"Startup failed. Device not ready");
            return;
        }

        while(continueCapture) {
            try {
                String line = dataIn.readLine();
                if(line != null)
                    listener.onConsoleMessage(line);
            } catch (IOException e) {}
        }

        Log.d(DEBUG_TAG,"Stopping process");
        try {
            stopProcess();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean startProcess() throws IOException {
        // Create process and get SU access
        ProcessBuilder processBuilder = new ProcessBuilder("su");
        processBuilder.redirectErrorStream(true);
        console = processBuilder.start();

        // get data streams
        dataIn = new BufferedReader(new InputStreamReader(console.getInputStream()));
        dataOut = new DataOutputStream(console.getOutputStream());
        dataErr = new BufferedReader(new InputStreamReader(console.getErrorStream()));


        // run setup scripts
        writeCommand(toFolder);
        writeCommand(loadDriverComm);
        writeCommand("iwconfig wlan0 mode monitor");
        writeCommand(tcpDumpComm);

        return true;
    }

    private boolean writeCommand(String command) {
        boolean output = true;
        ArrayList<String> outputlist = new ArrayList<String>();
        try {
            dataOut.writeBytes(command+"\n");
            dataOut.flush();
        } catch (IOException e) {
            output = false;
        }
        return output;
    }

    private void stopProcess() throws IOException {
        console.destroy();
        dataOut.close();
        dataIn.close();
        dataErr.close();
    }

    public void shutdown() {
        continueCapture = false;
    }

}
