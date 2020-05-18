package ro.pub.cs.systems.eim.practicaltest02;

import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CommunicationThread extends Thread {

    private Socket socket;
    private ServerThread serverThread;
    private static final String WebService = "https://api.coindesk.com/v1/bpi/currentprice/";

    public CommunicationThread(Socket socket, ServerThread serverThread) {
        this.socket = socket;
        this.serverThread = serverThread;
    }

    @Override
    public void run() {
        if (socket == null)
            return;

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

            String currency = bufferedReader.readLine();
            if (currency.isEmpty())
                return;

            HashMap<String, BitcoinRateInfo> data = serverThread.getData();
            BitcoinRateInfo rateInfo = null;
            if (data.containsKey(currency)) {
                rateInfo = data.get(currency);
            }
            else
            {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(WebService + currency + ".json");
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                if (pageSourceCode == null)
                    return;

                Document document = Jsoup.parse(pageSourceCode);
                Elements preElements = document.getElementsByTag("pre");
                for (Element el : preElements) {
                    String elString = el.data();
                    if (elString.contains("bpi")) {
                        JSONObject json = new JSONObject(elString);
                        JSONObject bpi = json.getJSONObject("bpi");
                        JSONObject ccy = bpi.getJSONObject(currency);
                        String rate = ccy.getString("rate");

                        JSONObject time = json.getJSONObject("time");
                        String updated = time.getString("updated");

                        rateInfo = new BitcoinRateInfo(currency, updated, rate);
                        serverThread.setData(currency, rateInfo);
                        break;
                    }
                }
            }

            if (rateInfo == null)
                return;

            printWriter.println(rateInfo.rate);
            printWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}