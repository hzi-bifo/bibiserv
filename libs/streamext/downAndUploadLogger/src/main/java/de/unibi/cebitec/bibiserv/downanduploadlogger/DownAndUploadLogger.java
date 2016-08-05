package de.unibi.cebitec.bibiserv.downanduploadlogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author gatter
 */
public class DownAndUploadLogger {

    private static final SimpleDateFormat dbSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat idSdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
    private static final String ID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    // regex to detect if this is a progress line    

    /**
     * Arguments:
     *
     * 0 : host 1 : port 2 : user 3 : password
     *
     * 4 : filename: name to Display 5 : kind of logging: awsUpload, awsDownload
     * or urlDownload
     *
     * 6 : id for database update 7 : toolname for database update 8 : index (to
     * identify input)
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {


        // ################ test and get input ################

        if (args.length < 9) {
            System.err.print("Not enough parameters for logger.");
            System.exit(3);
        }

        String host = args[0];
        String port = args[1];
        String user = args[2];
        String password = args[3];

        String filename = args[4];
        String logginType = args[5];

        String statusID = args[6];
        String toolname = args[7];
        String index = args[8];


        // ################ generate uniqeqid ################

        Date curTime = new Date();
        //prepare date
        String myDate = idSdf.format(curTime);
        Random rand = new Random();
        StringBuffer myRnd = new StringBuffer();
        for (int n = 0; n < 5; n++) {//length is 5
            myRnd = myRnd.append(ID_CHARS.charAt(rand.nextInt(62)));
        }
        //create ID from hostname with date and rnd 
        String uniqueID = host + "_" + myDate + "_" + index + "_" + myRnd.toString();


        // ################ generate Strings ################

        String kindToken = "Download";
        if (logginType.equalsIgnoreCase("awsUpload")) {
            kindToken = "Upload";
        }

        boolean bibis3Output = false;
        if (logginType.equalsIgnoreCase("awsUpload") || logginType.equalsIgnoreCase("awsDownloadMultiThread")) {
            bibis3Output = true;
        }


        String history = "Started " + kindToken + " of " + filename;


        Connection connection = null;
        try {

            // ################ Get Inputstream and Derby Connection  ################

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            // open connection to database TODO: Connection
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            connection = DriverManager.getConnection("jdbc:derby://" + host + ":" + port + "/bibiserv2;user=" + user + ";password=" + password);

            // ################ remove possibly old entrys DB ################

            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM downloaduploadstatus WHERE statusid='" + statusID + "' AND toolname='" + toolname + "'" + " AND index=" + index);

            stmt.close();

            // ################ insert into DB ################

            Date createdDate = new Date();
            stmt = connection.createStatement();
            stmt.executeUpdate("INSERT INTO downloaduploadstatus "
                    + "(id,statusid, toolname, index, history, state, created, lastmod) VALUES"
                    + "('" + uniqueID + "',"
                    + "'" + statusID + "',"
                    + "'" + toolname + "',"
                    + index + ","
                    + "'" + history + "',"
                    + "'',"
                    + "'" + dbSdf.format(createdDate) + "',"
                    + "'" + dbSdf.format(createdDate) + "')");
            stmt.close();

            NumberFormat percentFormat = NumberFormat.getPercentInstance();
            percentFormat.setMaximumFractionDigits(2);

            int headcount = 0;
            String s;
            while ((s = in.readLine()) != null) {

                // ignore empty lines
                if (s.isEmpty()) {
                    continue;
                }

                // update everytime a line is read till stream ended
                Date changeDate = new Date();
                stmt = connection.createStatement();

                if (bibis3Output) {
                    if (s.startsWith("== ")) {
                        headcount++;

                        if (headcount > 2) {
                            //This header has to be placed in history, previous headers contain sensitive data and should not be shown
                            history += "<br/>" + s;
                            stmt.executeUpdate("UPDATE downloaduploadstatus SET history='" + history + "', lastmod='" + dbSdf.format(changeDate) + "' WHERE id='" + uniqueID + "' AND toolname='" + toolname + "'");
                        }

                    } else if (headcount > 2) {
                        stmt.executeUpdate("UPDATE downloaduploadstatus SET state='" + s + "', lastmod='" + dbSdf.format(changeDate) + "' WHERE id='" + uniqueID + "' AND toolname='" + toolname + "'");
                    }
                } else {
                     String[] content = s.trim().split("\\s+");
                     if (content.length > 1) {
                         try {
                             int size = Integer.parseInt(content[0]);
                             int downloaded = Integer.parseInt(content[1]);
                             double percent = (double) downloaded/ (double) size;
                             s = downloaded+" of "+size+" ("+percentFormat.format(percent)+")";
                             
                              stmt.executeUpdate("UPDATE downloaduploadstatus SET state='" + s + "', lastmod='" + dbSdf.format(changeDate) + "' WHERE id='" + uniqueID + "' AND toolname='" + toolname + "'");
                         } catch (NumberFormatException e) {
                         }
                     }

                }

                stmt.close();
            }

            if (!bibis3Output) {
                Date changeDate = new Date();
                stmt = connection.createStatement();
                stmt.executeUpdate("UPDATE downloaduploadstatus SET state='" + "Download successful!" + "', lastmod='" + dbSdf.format(changeDate) + "' WHERE id='" + uniqueID + "' AND toolname='" + toolname + "'");
                stmt.close();
            }
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException | IOException ex) {
            System.err.print(ex);
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.exit(3);
            }
            System.exit(3);
        }

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.exit(3);
        }

        System.exit(0);
    }
}
