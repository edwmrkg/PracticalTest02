package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private EditText portText;
    private Button connectButton;
    private EditText currencyText;
    private Button getRateButton;
    private TextView valueText;
    private ServerThread serverThread;
    private ClientThread clientThread;

    private class ConnectButtonListener implements Button.OnClickListener {

        @Override
        public void onClick(View v) {
            String serverPort = portText.getText().toString();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Port is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread != null) {
                serverThread.startServer();
            }
        }
    }

    private class GetRateButtonListener implements Button.OnClickListener {

        @Override
        public void onClick(View v) {
            String clientPort = portText.getText().toString();
            if (clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Port is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "Server not active!", Toast.LENGTH_SHORT).show();
                return;
            }

            String currency = currencyText.getText().toString();
            if (!currency.equals("USD") && !currency.equals("EUR")) {
                Toast.makeText(getApplicationContext(), "Invalid currency!", Toast.LENGTH_SHORT).show();
                return;
            }

            clientThread = new ClientThread(Integer.parseInt(clientPort), currency, valueText);
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        portText = (EditText)findViewById(R.id.portText);
        connectButton = (Button)findViewById(R.id.connectButton);
        currencyText = (EditText)findViewById(R.id.currencyText);
        getRateButton = (Button)findViewById(R.id.sendRequestButton);
        valueText = (TextView)findViewById(R.id.resultText);

        ConnectButtonListener connectButtonListener = new ConnectButtonListener();
        connectButton.setOnClickListener(connectButtonListener);

        GetRateButtonListener getRateButtonListener = new GetRateButtonListener();
        getRateButton.setOnClickListener(getRateButtonListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serverThread != null) {
            serverThread.stopServer();
        }
    }
}
