
package de.unibi.techfak.bibiserv;

/**
 * This class is used to store all the additionally needed data for generating a
 * pipelining script.
 * 
 * Each script has the following setup content of this object in []:
 * 
 * #clean up
 *  clean(){
 *      [clean]
 *  }
 *  
 * [initDownload]
 * 
 * while false [whileheaderDownload]
 *  do
 *      [whilebodyDownload]
 *	sleep 1
 *  done
 * 
 *  [initStream]
 * 
 *  while false [whileheaderStream]
 *  do
 *      [whilebodyStream]
 *	sleep 1
 *  done
 * 
 *  [afterBody]
 * 
 *  clean
 *  true || exit
 *  set stuff
 * 
 * @author Thomas Gatter - tgatter(at)techfak.uni-bielefeld.de
 */
public class CmdLineInfo {
    
    /**
     * The content of the defined parts above.
     */
    private StringBuilder clean;
    private StringBuilder initStream;
    private StringBuilder initDownload;
    private StringBuilder whileheaderDownload;
    private StringBuilder whilebodyDownload;
    private StringBuilder whileheaderStream;
    private StringBuilder whilebodyStream;
    private StringBuilder afterBody;
    
    /**
     * General counter.
     */
    private int returnError;
    private int inputNumber;
    
    /**
     * S3config file generated
     */
    private boolean configGenerated;
    
    public CmdLineInfo(){
        clean = new StringBuilder();
        initStream = new StringBuilder();
        initDownload = new StringBuilder();
        whileheaderStream = new StringBuilder();
        whileheaderStream.append("false");
        whilebodyStream = new StringBuilder();
        whileheaderDownload = new StringBuilder();
        whileheaderDownload.append("false");
        whilebodyDownload = new StringBuilder();
        afterBody = new StringBuilder();
        
        // skip normal return codes 0-2
        // 3 for s3/wget errors
        // 4 for toolerror
        // 5 for upload error
        // 6+ for validation and conversion
        returnError = 6;
        
        configGenerated = false;
    }

    public StringBuilder getClean() {
        return clean;
    }

    public int getReturnError() {
        return returnError;
    }

    public int getInputNumber() {
        return inputNumber;
    }
    
    public void incReturnError(){
        returnError++;
    }
    
    public void incInputNumber(){
        inputNumber++;
    }

    public StringBuilder getAfterBody() {
        return afterBody;
    }

    public StringBuilder getInitStream() {
        return initStream;
    }

    public void setInitStream(StringBuilder initStream) {
        this.initStream = initStream;
    }

    public StringBuilder getInitDownload() {
        return initDownload;
    }

    public void setInitDownload(StringBuilder initDownload) {
        this.initDownload = initDownload;
    }

    public StringBuilder getWhileheaderDownload() {
        return whileheaderDownload;
    }

    public void setWhileheaderDownload(StringBuilder whileheaderDownload) {
        this.whileheaderDownload = whileheaderDownload;
    }

    public StringBuilder getWhilebodyDownload() {
        return whilebodyDownload;
    }

    public void setWhilebodyDownload(StringBuilder whilebodyDownload) {
        this.whilebodyDownload = whilebodyDownload;
    }

    public StringBuilder getWhileheaderStream() {
        return whileheaderStream;
    }

    public void setWhileheaderStream(StringBuilder whileheaderStream) {
        this.whileheaderStream = whileheaderStream;
    }

    public StringBuilder getWhilebodyStream() {
        return whilebodyStream;
    }

    public void setWhilebodyStream(StringBuilder whilebodyStream) {
        this.whilebodyStream = whilebodyStream;
    }
    
    /**
     * sets ConfigGenerated to true
     */
    public void setConfigGenerated(){
        configGenerated = true;
    }
    
    public boolean isConfigGenerated() {
        return configGenerated;
    }
    
}
