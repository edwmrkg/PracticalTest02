package ro.pub.cs.systems.eim.practicaltest02;

import android.widget.TextView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread {

    private ServerSocket serverSocket;
    private boolean isRunning;
    private int port;
    HashMap<String, BitcoinRateInfo> data = new  HashMap<String, BitcoinRateInfo>();

    public ServerThread(int port) {
        this.port = port;
    }

    public void startServer() {
        isRunning = true;
        start();
    }

    public void stopServer() {
        isRunning = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setData(String currency, BitcoinRateInfo info) {
        this.data.put(currency, info);
    }

    public synchronized HashMap<String, BitcoinRateInfo> getData() {
        return data;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (isRunning) {
                Socket socket = serverSocket.accept();
                if (socket != null) {
                    CommunicationThread communicationThread = new CommunicationThread(socket, this);
                    communicationThread.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
