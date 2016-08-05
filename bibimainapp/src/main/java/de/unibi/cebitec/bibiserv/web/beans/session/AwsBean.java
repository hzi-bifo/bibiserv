/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.web.beans.session;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import de.unibi.cebitec.bibiserv.utils.ValidationConnection;
import de.unibi.cebitec.bibiserv.utils.connect.AWSValidationConnection;
import de.unibi.cebitec.bibiserv.utils.connect.URLValidationConnection;
import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import de.unibi.techfak.bibiserv.web.beans.AWSFileData;
import de.unibi.techfak.bibiserv.web.beans.session.AwsBeanInterface;
import de.unibi.techfak.bibiserv.web.beans.session.MessagesInterface;
import de.unibi.techfak.bibiserv.web.beans.session.UserInterface;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author afrischk
 * @author Thomas Gatter - tgatter(at)techfak.uni-bielefeld.de
 */
public class AwsBean implements InitializingBean, AwsBeanInterface {

    // logger for messages
     private static Logger log = Logger.getLogger(AwsBean.class);
    //user data
    private UserInterface user;
    private AmazonS3Client s3_user;
    private String id;
    private String accessKey;
    private String secretKey;
    private String sessiontoken;
    
    
    // buckets, locations and files
    private HashMap<String, String> bucket_locations;
    private HashMap<String, List<String>> bucket_files;
    private List<String> buckets;
    
    private MessagesInterface messages;
    
    private boolean awsCredentialsSet = false;

    
    // all needed patterns for URL and Bucket Validation
    private final static Pattern bucket_pattern_us = Pattern.compile("[\\.\\-\\_\\w]{3,255}");
    private final static Pattern bucket_pattern_other = Pattern.compile("^[a-z0-9]{1}[a-z\\-0-9]*[a-z0-9]{1}");
    private final static Pattern bucket_pattern_other2 = Pattern.compile("^[a-z0-9]{1}[\\.a-z\\-0-9]+[a-z0-9]{1}");
    private final static Pattern fst_part_url_pattern = Pattern.compile("https://(.+)\\.(s3)\\.amazonaws\\.com/(.+)");
    //eventuell noch anzupassen
    private final static Pattern snd_part_url_pattern = Pattern.compile("Expires=\\d+\\&AWSAccessKeyId=[A-Z0-9]+\\&Signature=.+");
    private final static Pattern bucketUrl_pattern = Pattern.compile("https://(.+)\\.(s3)\\.amazonaws\\.com/");
    private final static Pattern bucketUrl_pattern2 = Pattern.compile("https://(s3|s3-ap-southeast-1|s3-ap-northeast-1|s3-eu-west-1|s3-us-west-1|s3-us-west-2|s3-us-east-1)\\.amazonaws\\.com/([^/]+)/");

    private final static Pattern s3_public_url_virtual_hosted_style_pattern = Pattern.compile("https://(.+)\\.(s3)\\.amazonaws\\.com/(.+)");
    private final static Pattern s3_public_url_path_style_pattern = Pattern.compile("https://(s3|s3-ap-southeast-1|s3-ap-northeast-1|s3-eu-west-1|s3-us-west-1|s3-us-west-2|s3-us-east-1)\\.amazonaws\\.com/([^/]+)/(.+)");              
    private final static Pattern no_file_pattern = Pattern.compile(".*/");
    
    
    private final static Pattern object_full_pattern = Pattern.compile("[^/]+(/[^/]+)*($|/$)");
    
    @Override
    public void afterPropertiesSet() throws Exception {
        accessKey = "";
        secretKey = "";
        sessiontoken = "";
        
        s3_user = getAwsCredentialsFromDB();
        
        bucket_locations = new HashMap<String, String>();
        bucket_files = new HashMap<String,List<String>>();
        buckets = new ArrayList<String>();
        
    }
    
    public void reloadCredentials() {
        accessKey = "";
        secretKey = "";
        sessiontoken = "";
        s3_user = getAwsCredentialsFromDB();
    }

    
    //######################## CREDENTIALS ########################
    
    private AmazonS3Client getAwsCredentialsFromDB() {
        AmazonS3Client s3client = null;



        Connection con =  null;
        try {
            

            con = BiBiTools.getDataSource().getConnection();
            Statement stmt = con.createStatement();


            String selectCredentials = "SELECT keyname,accesskey,secretkey,sessiontoken FROM awscredentials where userid='" + user.getId() + "' AND isset=1";
            ResultSet res = stmt.executeQuery(selectCredentials);

            if (res.next()) {
                String k = res.getString("keyname");
                String a = res.getString("accesskey");
                String s = res.getString("secretkey");
                String st = res.getString("sessiontoken");
                
                id = k;
                accessKey = a;
                secretKey = s;
                sessiontoken = st;
                
                if (st.isEmpty()) {
                    BasicAWSCredentials bac = new BasicAWSCredentials(a, s);
                    s3client = new AmazonS3Client(bac);
                    awsCredentialsSet = true;
                } else {
                    BasicSessionCredentials bsc = new BasicSessionCredentials(a, s, st);
                    s3client = new AmazonS3Client(bsc);
                    awsCredentialsSet = true;
                }

            } else {
                BasicAWSCredentials bac = null;
                s3client = new AmazonS3Client(bac);
                awsCredentialsSet = false;
                
                id = "";
                accessKey = "";
                secretKey = "";
                sessiontoken = "";
            }
            res.close();
            stmt.close();

        } catch (DBConnectionException | SQLException e) {
            log.error(e.getMessage());
            awsCredentialsSet = false;
            id = "";
            accessKey = "";
            secretKey = "";
            sessiontoken = "";
        }finally {

            try {
                if (con != null) {
                    con.close();

                }
            } catch (SQLException ex) {
                log.error(ex.getMessage());
            }
        }

        return s3client;
    }
    
    //######################## LOAD BUCKTES AND FILES ########################

    @Override
    public String refresh(){
        
        buckets.clear();
        bucket_locations.clear();
        bucket_files.clear();
        String loadListMsg;
        
        List<Bucket> s3buckets = new ArrayList<Bucket>();

        try {
            s3buckets = s3_user.listBuckets();

            for (Bucket b : s3buckets) {
                String bname = b.getName();
                buckets.add(bname);
                bucket_locations.put(bname, s3_user.getBucketLocation(bname));
            }
            loadListMsg = messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.LISTBUCKETS");
        } catch (AmazonServiceException ase) {
            log.error("Caught an AmazonServiceException, "
                    + "which means your request made it "
                    + "to Amazon S3, but was rejected with an error response "
                    + "for some reason.");
            log.error("Buckets could not be listed.");
            loadListMsg = messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.NOLISTBUCKETS");
            log.error(ase.getMessage(), ase);

        } catch (AmazonClientException ace) {
            log.error("Caught an AmazonClientException, "
                    + "which means the client encountered "
                    + "an internal error while trying to communicate"
                    + " with S3, "
                    + "such as not being able to access the network.");
            log.error("Buckets could not be listed.");
            loadListMsg = messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.NOLISTBUCKETS");
            log.error(ace.getMessage(), ace);
        }
        return loadListMsg;
    }
 
    @Override
    public HashMap<Integer, String> loadS3ObjectList(String bucket) {
        return loadS3ObjectList(bucket,false);
    }    
    
    @Override
    public HashMap<Integer, String> loadS3ObjectListForced(String bucket) {
        return loadS3ObjectList(bucket,true);
    }    

    private HashMap<Integer, String> loadS3ObjectList(String bucket, boolean force) {
        
         HashMap<Integer, String> msg = new HashMap<Integer, String>();
        
         // already has been loaded
        if(!force && bucket_files.containsKey(bucket)) {
            return msg;
        }
       
        // not cached, read it from amazon
        List<String> files = new ArrayList<String>();
        List<S3ObjectSummary> objects;
        ObjectListing obj_listing;
        try {
            obj_listing = s3_user.listObjects(bucket);
            objects = obj_listing.getObjectSummaries();
            for (S3ObjectSummary o : objects) {
                String oname = o.getKey();
                if (checkObject(oname)) {
                    files.add(oname);
                }
            }
        } catch (AmazonServiceException ase) {
            log.error("Caught an AmazonServiceException, "
                    + "which means your request made it "
                    + "to Amazon S3, but was rejected with an error response "
                    + "for some reason.");
            log.error("Objects could not be listed.");
            msg.put(3, messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.NOLISTOBJECTS"));
            log.error(ase.getMessage(), ase);
        } catch (AmazonClientException ace) {
            log.error("Caught an AmazonClientException, "
                    + "which means the client encountered "
                    + "an internal error while trying to communicate"
                    + " with S3, "
                    + "such as not being able to access the network.");
            log.error("Objects could not be listed.");
            msg.put(3, messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.NOLISTOBJECTS"));
            log.error(ace.getMessage(), ace);
        }
        
        bucket_files.put(bucket, files);
        return msg;
    }
    
    @Override
    public List<String> getS3ObjectList(String bucket) {
        
        // already cached?
        if(bucket_files.containsKey(bucket)) {
            return bucket_files.get(bucket);
        }
        return new ArrayList<String>();         
    }
    
    
    @Override
    public String getBucketLocation(String selected_item_bucket) {
        return bucket_locations.get(selected_item_bucket);
    }
    
    @Override
    public List<String> getBuckets() {
        return buckets;
    }
    
    private boolean checkObject(String object) {
        Matcher m = no_file_pattern.matcher(object);
        return !m.matches();
    }

    
    @Override
    public boolean validateObjectName(String object){
        Matcher m = object_full_pattern.matcher(object);
        return m.matches();
    }
    
    @Override
    public boolean testUpload(String bucket, String filename){
        
        try {
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucket, filename);
        InitiateMultipartUploadResult initResponse = s3_user.initiateMultipartUpload(initRequest);
        
        s3_user.abortMultipartUpload(new AbortMultipartUploadRequest(bucket, filename, initResponse.getUploadId()));
        } catch(AmazonClientException ex) {
            return false;
        }
        return true;
    }
    
    
    //######################## URL SAVING ########################

    @Override
    public HashMap<Integer, String> saveS3urlSelect(AWSFileData data) {
        HashMap<Integer, String> msg = new HashMap<Integer, String>();
        String url = s3_user.getResourceUrl(data.getBucket(), data.getFile());
        if (!url.isEmpty()) {
            url = url.replaceAll("%252F", "/");
            try {
                data.setS3url(new URL(url));

                log.info("Successfully saved link: " + data.getS3url().toString());
               // msg.put(1, messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.SAVEURL") + " " + s3url.toString());
            } catch (MalformedURLException ex) {
                msg.put(3,messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.SAVEURLFAILED"));
                log.error("Maleformed URL: " + ex.getMessage());
                data.setS3url(null);
            }
        }else{
            msg.put(3,messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.SAVEURLFAILED"));
            data.setS3url(null);
        }
        return msg;
    }




    //######################## VALIDATION AND INFORMATION EXTRACTION OF PUBLIC URL ########################
    

    @Override
    public HashMap<Integer, String> validate_s3_url_awsbean(String s3url_to_public_object, AWSFileData data) {

        //initialize message
        HashMap<Integer, String> msg = new HashMap<Integer, String>();
        // clean URL
        String tmp_s3url = s3url_to_public_object.replaceAll("%2F", "/");

        // Tests if the URL contains a signature with accesskey
        if (isPresignedUrl(tmp_s3url)) {
            // split into file url (first part) and parameters (second part)
            String[] split_presigned_url = tmp_s3url.split("\\?");
            // is signature OK and does Url define a correct file?
            if (split_presigned_url.length == 2 && validate_fst_part_presigned_url(split_presigned_url[0], data) && validate_snd_part_presigned_url(split_presigned_url[1])) {

                // yes then set url, file and bucket have been set while testing!
                try {
                    data.setS3url(new URL(s3url_to_public_object));
                    msg.put(1, messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.SAVEURL") + " " + data.getS3url().toString() + " " + messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.SUCCESSFULS3URLVALIDATION") + "\nS3-Object: " + data.getFile() + "\nS3-Bucket: " + data.getBucket());
                    log.info("S3Url validation successful.");
                } catch (MalformedURLException ex) {
                    msg.put(3, messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.SAVEURLFAILED"));
                    log.error(ex.getMessage(), ex);
                    data.setS3url(null);
                    data.setFile("");
                    data.setBucket("");
                }
            } else {
                msg.put(2, messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.FAILEDS3URLVALIDATION"));
                log.error("S3-Url validation failed.");
                data.setS3url(null);
                data.setFile("");
                data.setBucket("");
            }

            // https://ratetest.s3.amazonaws.com/coli.ffn?Expires=1337777510&AWSAccessKeyId=AKIAIYTLKGEMS7WJOG7A&Signature=1utW83fdzg2KVxsrYC155m0V1mg%3D
        } else if (isBucketSpecified(tmp_s3url, data)) { // no signature found, test if this is specifies a bucket

            msg.put(1, messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.POSSIBLEBUCKET")+"\nS3-Bucket: "+data.getBucket());
            log.info("Link specifies public bucket.");

        } else { // this should be a normal S3URL or something completely off

            if(s3url_to_public_object.endsWith("/")){
                    msg.clear();
                    msg.put(2, messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.FAILEDS3URLVALIDATION"));
                    log.error("S3-Url validation failed.");
                    data.setS3url(null);
                    data.setFile("");
                    data.setBucket("");
                    return msg;
            }
            String[] split_public_url = s3url_to_public_object.split("/");

            // there need to be 4 parts because 3 parts are created by // after the protocoll and at least one / is needed for seperation of bucket and file
            if (split_public_url.length >= 4) {
                int index;
                String bucketname;
                String region;
                  
                Matcher public_s3_url_path_style_matcher = s3_public_url_path_style_pattern.matcher(s3url_to_public_object);
                Matcher public_s3_url_virtual_hosted_style_matcher = s3_public_url_virtual_hosted_style_pattern.matcher(s3url_to_public_object);
                //|s3-ap-southeast-1|s3-ap-northeast-1|s3-eu-west-1|s3-us-west-1|s3-us-west-2|s3-us-east-1
               
                // if the url does not match one of the two variations of s3-urls abort
                // index
                if (public_s3_url_path_style_matcher.matches()) {
                    index = 4;
                    bucketname = public_s3_url_path_style_matcher.group(2);
                    region = public_s3_url_path_style_matcher.group(1);
                } else if (public_s3_url_virtual_hosted_style_matcher.matches()) {
                    index = 3;
                    bucketname = public_s3_url_virtual_hosted_style_matcher.group(1);
                    region = public_s3_url_virtual_hosted_style_matcher.group(2);
                } else {
                    msg.put(2, messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.FAILEDS3URLVALIDATION"));
                    log.error("S3-Url validation failed.");
                    data.setS3url(null);
                    data.setFile("");
                    data.setBucket("");
                    return msg;

                } 
                // set the bucket and file of they are valid
                if (validateObjectsName(split_public_url, false,index, data) && validateBucketName(bucketname,region, data)) {
                    try {
                        data.setS3url(new URL(s3url_to_public_object));
                        msg.put(1, messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.SAVEURL") + " " + data.getS3url().toString() + " " + messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.SUCCESSFULS3URLVALIDATION")+ "\nS3-Object: " +data.getBucket() + "\nS3-Bucket: "+data.getFile());
                        log.info("S3Url validation successful.");
                    } catch (MalformedURLException ex) {
                            msg.clear();
                            msg.put(3, messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.SAVEURLFAILED"));
                            log.error(ex.getMessage(), ex);
                            data.setS3url(null);
                            data.setFile("");
                            data.setBucket("");
                    }
                } else {
                    msg.put(2, messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.FAILEDS3URLVALIDATION"));
                    log.error("S3-Url validation failed.");
                    data.setS3url(null);
                    data.setFile("");
                    data.setBucket("");
                }

            } else {
                msg.put(2, messages.property("de.unibi.techfak.bibiserv.bibimainapp.input.FAILEDS3URLVALIDATION"));
                log.error("S3-Url validation failed.");
                data.setS3url(null);
                data.setFile("");
                data.setBucket("");
            }
        }
        return msg;
    }
    
    private boolean validateBucketName(String bucketname, String region, AWSFileData data) {

       
        boolean everything_matches = true;
        String[] split_bucketname = bucketname.split("\\.");

        if (!region.equals("s3")) {
            if (bucketname.length() <= 63 && bucketname.length() >= 3) {
                for (int i = 0; i < split_bucketname.length; i++) {
                    Matcher bucket_matcher_other = bucket_pattern_other.matcher(split_bucketname[i]);                    
                    if (!bucket_matcher_other.matches()) {

                        return !everything_matches;
                    }

                }
                Matcher bucket_matcher_other2 = bucket_pattern_other2.matcher(bucketname);
                if(!bucket_matcher_other2.matches()){
                    return !everything_matches;
                }
                
            }else{
                return !everything_matches;
            }
        } else {
            if (bucketname.length() <= 255) {
                Matcher bucket_matcher_us = bucket_pattern_us.matcher(bucketname);
                if (!bucket_matcher_us.matches()) {
                    return !everything_matches;

                }
            }else{
                return !everything_matches;
            }
        }
        data.setBucket(bucketname);
        return everything_matches;
    }

    private boolean validateObjectsName(String[] split_url, boolean isPresigned, int index, AWSFileData data) {
        boolean everything_matches = true;
        String objectname = "";
        Pattern objects_pattern = Pattern.compile(".+");
        if (isPresigned) {
            for (int i = 0; i < split_url.length; i++) {
                Matcher object_matcher = objects_pattern.matcher(split_url[i]);
                if (i == (split_url.length - 1)) {
                    objectname += split_url[i];
                } else {
                    objectname += split_url[i] + "/";
                }
                if (!object_matcher.matches()) {
                    return !everything_matches;
                }
            }
            data.setFile(objectname);
        } else {
            for (int i = index; i < split_url.length; i++) {                
                Matcher object_matcher = objects_pattern.matcher(split_url[i]);

                if (i == (split_url.length - 1)) {
                    objectname += split_url[i];
                } else {
                    objectname += split_url[i] + "/";
                }                
                if (!object_matcher.matches()) {                   
                    return !everything_matches;
                    
                }
            }
            data.setFile(objectname);
        }
        return everything_matches;
    }

    private boolean validate_fst_part_presigned_url(String fst_part_url, AWSFileData data) {
        if(fst_part_url.endsWith("/")){
            return false;        
        }

        Matcher fst_part_url_matcher = fst_part_url_pattern.matcher(fst_part_url);

        if (fst_part_url_matcher.matches()) {
            String bucketname = fst_part_url_matcher.group(1);
            String[] objectname = fst_part_url_matcher.group(3).split("/");

            //index 0 is dummy variable, doesnt matter in that case
            if (validateBucketName(bucketname,fst_part_url_matcher.group(2), data) && validateObjectsName(objectname, true,0, data)) {
                return true;
            }

        }
        return false;
    }
//https://ratetest.s3.amazonaws.com/test%2Ftest2%2Ftest3%2Fcoli.ffn?Expires=1338293042&AWSAccessKeyId=AKIAIYTLKGEMS7WJOG7A&Signature=DNlfGtxlPvsG4yfrCxex8fU3z%2FU%3D

    private boolean validate_snd_part_presigned_url(String snd_part_url) {
        boolean valid = true;

        Matcher snd_part_url_matcher = snd_part_url_pattern.matcher(snd_part_url);

        if (snd_part_url_matcher.matches()) {

            return valid;
        }
        return !valid;
    }

    /**
     * Tests id the URL contains a signature with accesskey
     * @param url
     * @return 
     */
    private boolean isPresignedUrl(String url) {
        Pattern expires_pattern = Pattern.compile("Expires=");
        Pattern awsAccessKey_pattern = Pattern.compile("AWSAccessKeyId=");
        Pattern signature_pattern = Pattern.compile("Signature=");

        Matcher presigned_expires_matcher = expires_pattern.matcher(url);
        Matcher presigned_awsAccessKey_matcher = awsAccessKey_pattern.matcher(url);
        Matcher presigned_signature_matcher = signature_pattern.matcher(url);

        if (presigned_expires_matcher.find() && presigned_awsAccessKey_matcher.find() && presigned_signature_matcher.find()) {
            return true;
        }

        return false;
    }

    private boolean isBucketSpecified(String url, AWSFileData data) {

   
        Matcher bucketUrl_matcher = bucketUrl_pattern.matcher(url);
        Matcher bucketUrl_matcher2 = bucketUrl_pattern2.matcher(url);

        if (bucketUrl_matcher.matches()) {

            String region = bucketUrl_matcher.group(2);
            String bucketname = bucketUrl_matcher.group(1);
            // also saves bucked name
            if (validateBucketName(bucketname, region, data)) {
                data.setFile("");
                return true;
            }

        } else if (bucketUrl_matcher2.matches()) {

            String region = bucketUrl_matcher2.group(1);
            String bucketname = bucketUrl_matcher2.group(2);
            // also saves bucket name
            if (validateBucketName(bucketname, region, data)) {
                data.setFile("");
                return true;
            }
        }
        return false;
    }

    
    
    
    
    //######################## CONNECTION WIZARD ########################
       

    @Override
    public ValidationConnection getAWSObject(AWSFileData data) {

        StringBuilder sb = new StringBuilder();
        if (!data.getBucket().isEmpty() && !data.getFile().isEmpty()) {
            return new AWSValidationConnection(s3_user, data.getBucket(), data.getFile());
        }
        return null;
    }

    @Override
    public ValidationConnection getConnectionObject(AWSFileData data) {

        URL url;
        if (data.getS3url() != null) {
            url = data.getS3url();
            return new URLValidationConnection(url);
        }
        return null;
    }

    
    //######################## REST ########################
    
    
    @Override
    public boolean isAwsCredentialsSet() {
        return awsCredentialsSet;
    }


    @Override
    public MessagesInterface getMessages() {
        return messages;
    }

    @Override
    public void setMessages(MessagesInterface messages) {
        this.messages = messages;
    }

    @Override
    public String getAccessKey(){
        return accessKey;
    }
    
    @Override
    public String getSecretKey(){
        return secretKey;
    }
    

    public String getSessionToken(){
        return sessiontoken;
    }

    public UserInterface getUser() {
        return user;
    }

    public void setUser(UserInterface user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
