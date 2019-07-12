import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.JsonString;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartHomeApiClient {
    private static final HttpTransport transport = new NetHttpTransport();
    private static final JsonFactory jsonFactory = new JacksonFactory();
    private HttpRequestFactory requestFactory = null;

    private String baseUrl = null;
    private String authToken = null;

    public SmartHomeApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        SmartHomeObject e = new Lamp();
    }

    public SmartHomeApiClient(String baseUrl, String authToken) {
        this(baseUrl);
        setAuthToken(authToken);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(final String authToken) {
        this.authToken = authToken;
        // create new request factory with token set in Authorization header
        requestFactory = transport.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                request.setParser(new JsonObjectParser(jsonFactory));
                request.getHeaders().setAuthorization(String.format("Token %s", authToken));
                request.getHeaders().setAccept("application/json");
            }
        });
    }

    public static class AuthResponse {
        @Key public String token;
    }

    public boolean login(String username, String password){
        requestFactory = transport.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                request.setParser(new JsonObjectParser(jsonFactory));
                request.getHeaders().setAccept("application/json");
            }
        });
        Map<String, String> json = new HashMap<>();
        json.put("username", username);
        json.put("password", password);
        HttpContent content = new JsonHttpContent(jsonFactory, json);
        GenericUrl url = new GenericUrl(baseUrl + "api/token-auth/");
        try {
            HttpRequest request = requestFactory.buildPostRequest(url, content);
            HttpResponse response = request.execute();
            if (response.getStatusCode() != 200)
                return false;

            AuthResponse authResponse = response.parseAs(AuthResponse.class);
            if(authResponse.token == null)
                return false;

            setAuthToken(authResponse.token);
            return true;
        } catch(IOException e) {
            return false;
        }
    }

    public static abstract class SmartHomeObject implements Serializable {
        @Key public Integer id;
    }

    public static class User extends SmartHomeObject {
        @Key public String username;
    }

    public static class House extends SmartHomeObject {
        @Key public int owner;
    }

    public static abstract class Device extends SmartHomeObject {
        @Key public Boolean favourite;
        @Key public Boolean state;
        @Key public String device;
    }
    public static class Lamp extends Device {
        @Key public String name;
        @Key public Integer intensity;
        @Key public Boolean dimmable;
        @Key public Integer room;
    }

    public static class Door extends Device {
        @Key public Integer room1;
        @Key public Integer room2;
    }

    public static class RTV extends Device {
        @Key public String name;
        @Key public Integer volume;
        @Key public Integer room;
    }

    public static class Room extends SmartHomeObject {
        @Key public String name;
        @Key @JsonString public Float temperature;
        @Key @JsonString public Float humidity;
        @Key public Boolean people;
        @Key public Boolean favourite;
        @Key public Integer house;
        @Key public String device;
    }

    public static class MapEntry extends SmartHomeObject {
        @Key public String name;
        @Key public String type;
        @Key public Integer posX;
        @Key public Integer posY;
        @Key public Integer house;
    }

    public List<SmartHomeObject> getList(String typeStr, Type type) {
        GenericUrl url = new GenericUrl(baseUrl + "api/" + typeStr + "/");
        try {
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse response = request.execute();
            if (response.getStatusCode() != 200) {
                System.err.println(String.format("Server returned code %d", response.getStatusCode()));
                return null;
            }
            return (List<SmartHomeObject>)response.parseAs(type);

        } catch(IOException e) {
            System.err.println(e.getLocalizedMessage());
            return null;
        }

    }
    public SmartHomeObject getObject(String typeStr, Class type, int id) {
        GenericUrl url = new GenericUrl(String.format("%sapi/%s/%d/", baseUrl, typeStr, id));
        try {
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse response = request.execute();
            if (response.getStatusCode() != 200) {
                System.err.println(String.format("Server returned code %d", response.getStatusCode()));
                return null;
            }
            return (SmartHomeObject)response.parseAs(type);

        } catch(IOException e) {
            System.err.println(e.getLocalizedMessage());
            return null;
        }

    }

    public SmartHomeObject putObject(String typeStr, int id, SmartHomeObject data) {
        GenericUrl url = new GenericUrl(String.format("%sapi/%s/%d/", baseUrl, typeStr, id));
        HttpContent content = new JsonHttpContent(jsonFactory, data);
        try {
            HttpRequest request = requestFactory.buildPutRequest(url, content);
            HttpResponse response = request.execute();
            if (response.getStatusCode() != 200) {
                System.err.println(String.format("Server returned code %d", response.getStatusCode()));
                return null;
            }
            return response.parseAs(data.getClass());
        } catch(IOException e) {
            System.err.println(e.getLocalizedMessage());
            return null;
        }
    }
    public boolean postObject(String typeStr, SmartHomeObject data) {
        GenericUrl url = new GenericUrl(String.format("%sapi/%s/", baseUrl, typeStr));
        HttpContent content = new JsonHttpContent(jsonFactory, data);
        try {
            HttpRequest request = requestFactory.buildPostRequest(url, content);
            HttpResponse response = request.execute();
            if (response.getStatusCode() != 200 && response.getStatusCode() != 201)
            {
                System.err.println(String.format("Server returned code %d", response.getStatusCode()));
                return false;
            }
            return true;
        } catch(IOException e) {
            System.err.println(e.getLocalizedMessage());
            return false;
        }
    }
    public boolean deleteObject(String typeStr, int id) {
        GenericUrl url = new GenericUrl(String.format("%sapi/%s/%d/", baseUrl, typeStr, id));
        try {
            HttpRequest request = requestFactory.buildDeleteRequest(url);
            HttpResponse response = request.execute();
            if (response.getStatusCode() != 200 && response.getStatusCode() != 204)
            {
                System.err.println(String.format("Server returned code %d", response.getStatusCode()));
                return false;
            }
            return true;
        } catch(IOException e) {
            System.err.println(e.getLocalizedMessage());
            return false;
        }
    }}
