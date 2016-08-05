/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.s3uploader;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gatter
 */
public class S3StreamUploader {

    /**
     * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielfeld.de
     */
    public static void main(String[] args) {

        final String existingBucketName = args[0];
        final String keyName = args[1];
        String basepath = args[2];
        String pipeName = args[3];

        // Login data for s3
        BasicAWSCredentials bac = new BasicAWSCredentials("AKIAJMT62A6ETOWVVRLQ", "8+moivyZWyZlQ/Z7fZ9y2yYZq1yTvNmV4NrLm+dX");
        final AmazonS3 s3Client = new AmazonS3Client(bac);


        // Create a list of UploadPartResponse objects. You get one of these
        // for each part upload.
        final List<PartETag> partETags = new ArrayList<PartETag>();
        // initupload data
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(existingBucketName, keyName);
        final InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);
        // queue for thread communication
        final BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);

        // the uploading thread
        Runnable uploadRunnable = new Runnable() {
            public void run() {
                int chunk = 1;
                try {
                    String nextFilename;
                    // while done signal is not send
                    while (!(nextFilename = queue.take()).equals("done")) {
                        // use queue input as filename
                        File file = new File(nextFilename);
                        // upload file
                        UploadPartRequest uploadRequest = new UploadPartRequest()
                                .withBucketName(existingBucketName).withKey(keyName)
                                .withUploadId(initResponse.getUploadId()).withPartNumber(chunk)
                                .withFile(file)
                                .withPartSize(file.length());

                        // Upload part and add response to our list.
                        partETags.add(
                                s3Client.uploadPart(uploadRequest).getPartETag());
                        chunk++;
                        //remove chunk file
                        file.delete();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
                            existingBucketName, keyName, initResponse.getUploadId()));
                } catch (Exception e) {
                    s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
                            existingBucketName, keyName, initResponse.getUploadId()));
                }

            }
        };
        // start the uploading thread
        Thread uploadThread = new Thread(uploadRunnable);
        uploadThread.start();
        
        FileOutputStream out = null;
        try {

            // init bacis data for reading from pipe
            FileInputStream pipe = new FileInputStream(basepath + "/" + pipeName);
            int chunknumber = 1;
            int chunkCharsNumber = 5* 1024 * 1024; // 5 MB chuncks
            int len = -1;
            int read = 0;
            byte[] buffer = new byte[1024];
            // first chunk file
            out = new FileOutputStream(basepath + "/resultupload.chunk" + chunknumber);
            // read till stream ends
            while ((len = pipe.read(buffer)) != -1) {
                // add to chunk file
                out.write(buffer, 0, len);
                read+=len;
                // is the chunkfile full? if yes set all for next chunk
                if (read >= chunkCharsNumber) {
                    out.close();
                    try {
                        // tell upload thread this file is ready.
                        queue.put(basepath + "/resultupload.chunk" + chunknumber);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    chunknumber++;
                    out = new FileOutputStream(basepath + "/resultupload.chunk" + chunknumber);
                    read = 0;

                }
            }
            out.close();
            // set last chunk and wait for upload to die
            try {
                queue.put(basepath + "/resultupload.chunk" + chunknumber);
                // end signal
                queue.put("done");
                uploadThread.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            // tell s3 to join files
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
                    existingBucketName,
                    keyName,
                    initResponse.getUploadId(),
                    partETags);
            s3Client.completeMultipartUpload(compRequest);

        } catch (Exception ex) {
             s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
                            existingBucketName, keyName, initResponse.getUploadId()));
             System.out.println(ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(S3StreamUploader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
