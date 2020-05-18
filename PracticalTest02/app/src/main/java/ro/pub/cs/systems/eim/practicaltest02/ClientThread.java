package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientThread extends Thread {

    private Socket socket;
    private int port;
    private String currency;
    private TextView rateTextView;

    public ClientThread(int port, String currency, TextView rateTextView) {
        this.port = port;
        this.currency = currency;
        this.rateTextView = rateTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(InetAddress.getLocalHost(), port);
            if (socket == null)
                return;

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

            printWriter.println(currency);

            String info;
            while ((info = bufferedReader.readLine()) != null) {
                Log.d("[CLIENT]", info);
                final String infoFinal = info;
                rateTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        rateTextView.setText(infoFinal);
                    }
                });
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
