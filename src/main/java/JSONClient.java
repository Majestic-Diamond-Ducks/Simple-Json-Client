import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class JSONClient {

    private final String CONFIG = "config.json";
    private final String DOCUMENT = "document.json";

    private JSONObject configJSON;
    private JSONObject docJSON;

    private String hostname;
    private int port;

    public JSONClient() {
        setupJSONObjects();
        this.hostname = configJSON.getString("Hostname");
        this.port = configJSON.getInt("Port");
    }

    private void setupJSONObjects() {
        try(BufferedReader configReader = new BufferedReader(new FileReader(CONFIG));
            BufferedReader docReader = new BufferedReader(new FileReader(DOCUMENT));) {

            StringBuilder sb = new StringBuilder();
            String fileIn;

            while(null != (fileIn = configReader.readLine())) {
                sb.append(fileIn);
            }
            this.configJSON = new JSONObject(sb.toString());
            System.out.println("Config JSON created");

            sb.setLength(0);

            while(null != (fileIn = docReader.readLine())) {
                sb.append(fileIn);
            }
            this.docJSON = new JSONObject(sb.toString());
            System.out.println("Document JSON created");
        }
        catch(IOException e) {
            System.err.println("File error: " + e.getMessage());
        }
    }

    public void sendDocument() {
        //Try with resources
        try(Socket socket = new Socket(this.hostname, this.port);) {

            OutputStream os = socket.getOutputStream(); //Create output stream
            OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);

            String consoleIn;
            BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Press Enter to send message. Type exit to exit the program");
            while(! (consoleIn = cin.readLine()).equalsIgnoreCase("exit")) { //Check console for "exit"
                System.out.println(docJSON.toString()); //If no "exit" send message
                osw.write(docJSON.toString());
                osw.write("\n\n");
                osw.flush();
            }
            osw.close(); //Close stream writer
        }
        catch(UnknownHostException e) {
            System.err.println("Unknown host: " + e.getMessage());
        }
        catch(IOException e) {
            System.err.println("Network error: " + e.getMessage());
        }
    }
}
