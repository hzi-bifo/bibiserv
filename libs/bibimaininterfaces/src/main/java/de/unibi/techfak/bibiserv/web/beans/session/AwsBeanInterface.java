/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
 * All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License("CDDL") (the "License"). You
 * may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.sun.com/cddl/cddl.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.  When distributing the software, include
 * this License Header Notice in each file.  If applicable, add the following
 * below the License Header, with the fields enclosed by brackets [] replaced
 *  by your own identifying information:
 *
 * "Portions Copyrighted 2012 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.techfak.bibiserv.web.beans.session;

import de.unibi.cebitec.bibiserv.utils.ValidationConnection;
import de.unibi.techfak.bibiserv.web.beans.AWSFileData;
import java.util.HashMap;
import java.util.List;
/**
 * Interface AwsBeanInterface provides a couple of methods for AWS S3
 * interaction.
 *
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *
 */
public interface AwsBeanInterface {

    /**
     * Load all possible buckets and reset credentials and files.
     * @return A possible localized Error-Message  or empty if everything was OK.
     */
    public String refresh();
    
    /**
     * Returns all found buckets by refresh call.
     * Empty list if refresh was not called.
     * @return 
     */
    public List<String> getBuckets();
    
    /**
     * Load all filenames for a given buckets and saves them. This function has 
     * no effect if it was called before with the same argument.  
     * @param bucket Bucket to load filenames from.
     * @return A Map of possible errors.
     */
    public HashMap<Integer, String> loadS3ObjectList(String bucket);
    
    /**
     * Load all filenames for a given buckets and saves them.
     * It will overwrite already cached data.
     * @param bucket Bucket to load filenames from.
     * @return A Map of possible errors.
     */
    public HashMap<Integer, String> loadS3ObjectListForced(String bucket);
    
    
    /**
     * Returns the names of all files contained in this bucket.
     * This will only return the correct files if loadS3ObjectList was called
     * with the same argument before calling this function. If loadS3ObjectList
     * was not called prior to this, the function will return an empty list.
     * @param bucket Bucket to get filenames from.
     * @return all filenames contained in the bucket
     */
    public List<String> getS3ObjectList(String bucket);
    
    /**
     * Returns the location of the bucket.
     * @param selected_item_bucket name of the bucket to return the location of
     * @return location of the bucket
     */
    public String getBucketLocation(String selected_item_bucket);
    
    /**
     * Generates and saves the url corresponding to bucket and file in data
     * @param data Data-Object to generate url for and set it.
     * @return A Map of possible errors.
     */
    public HashMap<Integer, String> saveS3urlSelect(AWSFileData data);
    
    /**
     * Validates a given url and saves the result in data
     * @param s3url_to_public_object String of the url
     * @param data Object to store results in
     * @return A Map of possible errors.
     */
    public HashMap<Integer, String> validate_s3_url_awsbean(String s3url_to_public_object, AWSFileData data);
    
    /**
     * Returns a connection based upon s3:// protocoll
     * @param data basis data
     * @return connection based upon s3:// protocoll
     */
    public ValidationConnection getAWSObject(AWSFileData data);
    
    /**
     * Returns a connection based upon http:// protocoll
     * @param data basis data
     * @return connection based upon http:// protocoll
     */
    public ValidationConnection getConnectionObject(AWSFileData data);
    
    
    /**
     * Return whether the given object/filename is valid filename for aws
     * @param object filename to check
     * @return true if the given string is a valid filename
     */
    public boolean validateObjectName(String object);
    
    /**
     * Trys to open a testupload to given set of bucket and filename.
     * @param bucket Bucket to test upload to.
     * @param filename Filename to test upload to.
     * @return true if testupload was succesfull.
     */
    public boolean testUpload(String bucket, String filename);
    /**
     * Is there an AWS-Key specified by the user? 
     * @return 
     */
    public boolean isAwsCredentialsSet();
    
    public MessagesInterface getMessages();
    public void setMessages(MessagesInterface messages);
    
    /**
     * Returns the current accesskey or empty string if none is set.
     * @return 
     */
    public String getAccessKey();
    /**
     * Returns the current secretkey or empty string if none is set
     * @return 
     */
    public String getSecretKey();
    /**
     * Returns the current SessionToken or empty string if none is set
     * @return 
     */
    public String getSessionToken();
}
