package com.gaurav.httprequest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    public static final String USGS = "http://192.168.43.55:99";
    Button btn;
    String Res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);
    }

    public void sendrequest(View view) {
        httpAsyncTask task = new httpAsyncTask();
        task.execute();
    }

    public class httpAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url = null;
            try {
                url = new URL(USGS);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                updateui(s);
            }
        }


        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection Connection = null;
            InputStream inputStream = null;
            try {
                Connection = (HttpURLConnection) url.openConnection();
                Connection.setRequestMethod("GET");
                Connection.setReadTimeout(10000);
                Connection.setConnectTimeout(15000);
                Connection.connect();
                inputStream = Connection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                Res = String.valueOf(Connection.getResponseCode());
            } catch (IOException e) {

            }
            finally {
                if (Connection != null) {
                    Connection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }
    }

    private void updateui(String s) {
        TextView text;
        text = (TextView) findViewById(R.id.TextView);
        text.setText(s);
        btn.setText(Res);
    }
}
