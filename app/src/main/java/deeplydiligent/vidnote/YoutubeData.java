package deeplydiligent.vidnote;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by deep on 30/12/2017.
 */

public class YoutubeData extends AsyncTask<String, Void, String> {
    String titleStr = "default";
    String vidID = "none";

    // this is new code
    ResponseListener listener;
    public void setOnResponseListener(ResponseListener listener) {
        this.listener = listener;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String name) {
        super.onPostExecute(name);
        name = titleStr;
        listener.onResponseReceive(name,vidID);
    }

    @Override
    protected String doInBackground(String... params){
        vidID = params[0];
        String featuredFeed = "https://www.googleapis.com/youtube/v3/videos?id="+vidID+"&key=AIzaSyC7WB1t3Kvk-WwMfyVLRCaJr6JMv8NUmnI&part=snippet,contentDetails,statistics,status";
        try{
            String webPageSource = getWebPabeSource(featuredFeed);
            JSONObject jObject = new JSONObject(webPageSource);
            JSONObject items = new JSONObject(jObject.getJSONArray("items").getString(0));
            JSONObject snippet = new JSONObject(items.getString("snippet"));
            Log.d("lmao", "json string: " +  snippet.getString("title"));
            titleStr = snippet.getString("title");


        } catch (Exception e){
            e.printStackTrace();
        }finally{

        }



        return null;
    }

    private static String getWebPabeSource(String sURL) throws IOException {
        URL url = new URL(sURL);
        URLConnection urlCon = url.openConnection();
        BufferedReader in = null;

        if (urlCon.getHeaderField("Content-Encoding") != null
                && urlCon.getHeaderField("Content-Encoding").equals("gzip")) {
            in = new BufferedReader(new InputStreamReader(new GZIPInputStream(
                    urlCon.getInputStream())));
        } else {
            in = new BufferedReader(new InputStreamReader(
                    urlCon.getInputStream()));
        }

        String inputLine;
        StringBuilder sb = new StringBuilder();

        while ((inputLine = in.readLine()) != null)
            sb.append(inputLine);
        in.close();

        return sb.toString();
    }

}