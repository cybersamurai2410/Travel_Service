/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package connection_manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.entity.StringEntity;

/**
 *
 * @author T0266882
 */

public class ConnectionManager {
    
    // Send as JSON object -> extra param?
    
    private HttpURLConnection conn;
    
    public void openConnection(String address, String requestMethod) throws MalformedURLException, ProtocolException, IOException
    {
        URL url = new URL(address); 
        this.conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod(requestMethod); 
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.connect();
    }
    
    public String getService(String address) throws MalformedURLException, ProtocolException, IOException
    {
        openConnection(address, "GET");    
        
        if(conn.getResponseCode() == 200){
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            
            String line;
            StringBuilder response = new StringBuilder();
            while((line = br.readLine()) != null)
            {
                response.append(line);
            }
            
            br.close(); 
            conn.disconnect(); 
            
            return response.toString();
            
        }else{
            throw new RuntimeException("[Error] HTTP error code " + conn.getInputStream());
        }
    }
    
    public void postService(String address, String message) throws MalformedURLException, ProtocolException, IOException
    {
        openConnection(address, "POST");
        
        try(OutputStream os = conn.getOutputStream()) {
            byte[] out = message.getBytes(StandardCharsets.UTF_8);
            os.write(out, 0, out.length);
        }
        
        //HttpPost httppost = new HttpPost("");

        conn.disconnect();
        
        // https://www.baeldung.com/httpurlconnection-post
    }
}
