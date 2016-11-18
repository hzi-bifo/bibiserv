package de.unibi.cebitec.bibiserv.client.manager;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Properties;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author jkrueger
 */
public class ManagerClient {

    private Properties bibiserv2_manager;
    private static File pwdfile = new File(System.getProperty("user.home") + "/.bibiserv2_manager");
    private WebResource res = null;

    public ManagerClient() throws Exception {
        this(null, null, null);
    }

    /**
     * Initialize the ManagerClient
     *
     *
     * @param server - server to be used, overwrites ~/.bibiserv2_manager
     * settings
     * @param port - server to be used, overwrites ~/.bibiserv2_manager settings
     * @param ssl - server to be used, overwrites ~/.bibiserv2_manager settings
     * @throws Exception
     */
    public ManagerClient(String server, String port, Boolean ssl) throws Exception {
        bibiserv2_manager = readBiBiServ2PropertiesFile();
        if (server == null || port == null || ssl == null) {
            server = bibiserv2_manager.getProperty("server");
            port = bibiserv2_manager.getProperty("port");
            ssl = new Boolean(bibiserv2_manager.getProperty("ssl"));
            if (server == null || port == null || ssl == null) {
                System.err.println("Properties 'server', 'port' and 'ssl' must be set within the '" + pwdfile + "'!");
                System.exit(1);

            }
        }

        // create url
        String url = (ssl ? "https" : "http") + "://" + server + ":" + port + "/rest";

        Client client = Client.create(createClientConfig());

        res = client.resource(url);

    }

    /**
     * Deploy an application to server
     *
     * @param zipfile
     * @throws Exception
     */
    public void deploy(File zipfile) throws Exception {
        /* read zipfile into bytestream */
        InputStream in = new FileInputStream(zipfile);
        byte[] bytes = new byte[(int) zipfile.length()];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = in.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        in.close();

        try {
            res.path("manager").
                    type(MediaType.APPLICATION_OCTET_STREAM).
                    header("authorization", "basic "+bibiserv2_manager.get("role") + ":" + bibiserv2_manager.get("password")).
                    put(bytes);
            System.out.println("successful!");
        } catch (UniformInterfaceException e) {
            String answer = is2string(e.getResponse().getEntityInputStream());
            if (answer == null || answer.isEmpty()) {
                System.err.println(e.getMessage());
            } else {
                System.err.println(answer);
            }
        }

    }

    /**
     * Undeploy an application from server
     *
     * @param name
     * @throws ManagerException_Exception
     */
    public void undeploy(String name) {
        try {
            res.path("manager").
                    type(MediaType.TEXT_PLAIN).
                    header("authorization", "basic "+bibiserv2_manager.get("role") + ":" + bibiserv2_manager.get("password")).
                    put(name);
            System.out.println("successful!");
        } catch (UniformInterfaceException e) {
            String answer = is2string(e.getResponse().getEntityInputStream());
            if (answer == null || answer.isEmpty()) {
                System.err.println(e.getMessage());
            } else {
                System.err.println(answer);
            }
        }
    }

    /**
     * Static method that look for an Java Properties file
     * (System.getProperty("user.home") + "/.bibiserv2_manager") containing two
     * Java properties 'role' and 'password'. Throws an exception with enhanced
     * error message, if properties file doesn't exists, can't be read as
     * property file or doesn;t contain not all the both necessary properties.
     * Return a Properties object with at least two properties ('role' and
     * 'password').
     *
     *
     *
     * @return Return a Properties object with at least two properties ('role'
     * and 'password').
     * @throws Throws an Exception if properties file can't be read (see above).
     */
    public static Properties readBiBiServ2PropertiesFile() throws Exception {

        // check if ~/.bibiserv2_manager file exists and contains a user, passwort pair


        String message = "Create a password(property)file '" + pwdfile.toString() + "' containing two Java properties;\n"
                + "role and password\n\n"
                + "Example:\n"
                + "# bibimainapp manager admin role [required] \n"
                + "role=dummy\n"
                + "# bibimainapp manager admin role password [required] \n"
                + "password=my_password\n"
                + "# server host name \n"
                + "server=bibiserv2.cebitec.uni-bielefeld.de\n"
                + "# server port\n"
                + "port=443\n"
                + "# use ssl\n"
                + "ssl=true\n\n";


        if (!pwdfile.exists() || !pwdfile.isFile()) {
            throw new Exception("Passwordfile '" + pwdfile.toString() + "' not found ...\n" + message);
        }
        Properties pwdprop = new Properties();
        pwdprop.load(new FileReader(pwdfile));

        if (!(pwdprop.containsKey("role") && pwdprop.containsKey("password"))) {
            throw new Exception("Passwordfile '" + pwdfile.toString() + "' found, but contains wrong or no properties ...\n" + message);
        }
        if (pwdprop.containsKey("server")) {
            if (!pwdprop.containsKey("port")) {
                pwdprop.setProperty("port", "80");
            }

        }

        return pwdprop;
    }

    public static void main(String args[]) {
        try {
            // get home dir


            if (!(args.length == 2 || args.length == 5)) {
                System.err.println("usage: java " + ManagerClient.class.getName() + "\n"
                        + "\t deploy <BAR-File> (<server> <port> <ssl[true/false]>)\n"
                        + "\t undeploy name/id (<server> <port> <ssl[true|false]>)\n\n"
                        + "(If server, port and ssl are not given the values from ~/.bibiserv2_manager are used instead!");
                System.exit(1);
            }

            ManagerClient app;
            if (args.length == 2) {
                app = new ManagerClient();
            } else {
                app = new ManagerClient(args[2], args[3], new Boolean(args[4]));
            }
            switch (args[0]) {
                case "deploy":
                    app.deploy(new File(args[1]));
                    break;
                case "undeploy":
                    app.undeploy(args[1]);
                    break;
                default:
                    System.err.println("Unsupported command '" + args[0] + "'!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ClientConfig createClientConfig() throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    }};

// Install the all-trusting trust manager

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());


        ClientConfig config = new DefaultClientConfig();
        config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(
                new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                }, sc));
        return config;
    }

    private static String is2string(InputStream is) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            return e.getMessage();
        }
    }
}
