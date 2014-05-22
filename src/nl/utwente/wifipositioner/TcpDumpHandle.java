package nl.utwente.wifipositioner;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Joris on 22/05/2014.
 */
public class TcpDumpHandle extends Thread {

    private final String tcpDumpComm = "/data/local/tcpdump-arm -l -i eth0";
    private final String loadDriverComm = "/system/bin/sh /data/bcmon/setup.sh";
    private final String unloadDriverComm = "/system/bin/sh /data/bcmon/unsetup.sh";

    private Process captureProcess;
    private DataOutputStream dataOut;
    private BufferedReader dataIn;
    private DataListener listener;

    private boolean continueCapture = false;
    private boolean driverLoaded = false;

    public TcpDumpHandle(DataListener listener) {
        this.listener = listener;
    }

    public void run() {
        try {
            startCapture();
        } catch (IOException e) {
            continueCapture = false;
            e.printStackTrace();
        }

        while(continueCapture) {
            if(dataIn != null) {
                try {
                    String line = dataIn.readLine();
                    if(line != null)
                        listener.onConsoleMessage(line);
                } catch (IOException e) {}
            }
        }
    }

    public void startCapture() throws IOException {
        captureProcess = Runtime.getRuntime().exec("su");
        dataOut = new DataOutputStream(captureProcess.getOutputStream());
        dataIn = new BufferedReader(new InputStreamReader(captureProcess.getInputStream()));
        //start tcpdump
        dataOut.writeBytes(tcpDumpComm);
        dataOut.flush();
    }

    public void stopCapture() throws IOException {
        if(!continueCapture)
            return;

        dataOut.writeBytes("exit\n");
        dataOut.flush();
        dataOut.close();

        dataIn.close();

        captureProcess.destroy();

        //close tcpdump process
        Process process2 = Runtime.getRuntime().exec("ps tcpdump-arm");
        BufferedReader in = new BufferedReader(new InputStreamReader(process2.getInputStream()));
        String temp = in.readLine();
        temp = in.readLine();
        temp = temp.replaceAll("^root *([0-9]*).*","$1");
        int pid = Integer.parseInt(temp);
        execCommand("kill "+pid);

        continueCapture = false;
    }

    public void loadCustomDriver() {
        try {
            execCommand(loadDriverComm);
        } catch (IOException e) {
            e.printStackTrace();
        }

        driverLoaded = true;
    }

    public void unloadCustomDriver() {
        try {
            execCommand(unloadDriverComm);
        } catch (IOException e) {
            e.printStackTrace();
        }

        driverLoaded = false;
    }

    private void execCommand(String command) throws IOException {
        Process tempProcess = Runtime.getRuntime().exec("su");
        DataOutputStream out = new DataOutputStream(tempProcess.getOutputStream());
        out.writeBytes(command);
        out.flush();
        out.writeBytes("exit\n");
        out.flush();
        out.close();
        tempProcess.destroy();
    }

    public void shutdown() {
        try {
            stopCapture();
            unloadCustomDriver();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
