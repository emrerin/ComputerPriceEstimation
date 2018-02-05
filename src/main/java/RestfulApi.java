import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.apache.http.Consts.UTF_8;

/*
 * @author Emre Erin
 */
public class RestfulApi {

    //CloseableHttpClient httpclient = HttpClients.createDefault();
    // public static JSONObject inpParms;

    public static String apikey;
    public static String apiurl;
    public static String jsonBody;

    /**
     * Read the JSON schema from the file attributes.json
     *
     * @param filename It expects a fully qualified file name that contains input JSON file
     */
    public static void readJson(String filename) {
        try {
            File apiFile = new File(filename);
            Scanner sc = new Scanner(apiFile);
            jsonBody = "";
            while (sc.hasNext()) {
                jsonBody += sc.nextLine()+ "\n";
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }

    /**
     * Read the API key and API URL of Azure ML request response REST API
     *
     * @param filename fully qualified file name that contains API key and API URL
     */
    public static void readApiInfo(String filename) {

        try {
            File apiFile = new File(filename);
            Scanner sc = new Scanner(apiFile);

            apiurl = sc.nextLine();
            apikey = sc.nextLine();

        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }

    /**
     * Call REST API for retrieving prediction from Azure ML 
     * @return response from the REST API
     */
    public static String rrsHttpPost() {

        HttpPost post;
        HttpClient client;
        StringEntity entity;

        try {
            // create HttpPost and HttpClient object
            post = new HttpPost(apiurl);
            client = HttpClientBuilder.create().build();

            // setup output message by copying JSON body into 
            // apache StringEntity object along with content type
            entity = new StringEntity(jsonBody, UTF_8);
            entity.setContentEncoding((Header) UTF_8);
            entity.setContentType("text/json");

            // add HTTP headers
            post.setHeader("Accept", "text/json");
            post.setHeader("Accept-Charset", "UTF-8");

            // set Authorization header based on the API key
            post.setHeader("Authorization", ("Bearer "+ apikey));
            post.setEntity(entity);

            // Call REST API and retrieve response content
            HttpResponse authResponse = client.execute(post);

            return EntityUtils.toString(authResponse.getEntity());

        }
        catch (Exception e) {

            return e.toString();
        }

    }

    /**
     * @param args the command line arguments specifying JSON and API info file names
     */
    public static void main(String[] args) {

        String apiInfo = getTextFile("C:\\Users\\figo\\IdeaProjects\\AzureML\\src\\main\\java\\apiInfo.txt");

        String jsonFile = getTextFile("C:\\Users\\figo\\IdeaProjects\\AzureML\\src\\main\\java\\attributes.json");


        // check for mandatory argments. This program expects 2 arguments 
        // first argument is full path with file name of JSON file and 
        // second argument is full path with file name of API file that contains API URL
        // and API Key of request response REST API
        /*if (args.length < 2) {
            System.out.println("Incorrect usage. Please use the following calling pattern");
            System.out.println("java AzureML_CBU <attributes.json> <apiInfo.txt>");
            //readApiInfo("apiInfo.txt");
            //readJson("attributes.json");
        }*/

        try {

           /* // read JSON file name
            String jsonFile = args[0];
            // read API file name
            String apiFile = args[1];*/

            // call method to read API URL and key from API file
            readApiInfo(apiInfo);

            // call method to read JSON input from the JSON file
            readJson(jsonFile);

            // print the response from REST API
            System.out.println(rrsHttpPost());
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private static String getTextFile(String s) {
        String result = "";

        try {

            result = new String(Files.readAllBytes(Paths.get(s)));

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //U3cFrh4XsGZXQUZYmu3XYyhxSLs2XDYnWLGTB/PA7u4P1VZqBQ6vPXKNsM3r35AbuSvvs6a3HO/hx84FfR6ZCA==
        return result;
    }
}