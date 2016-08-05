
package de.unibi.cebitec.bibiserv.web.beans.session;

import de.unibi.techfak.bibiserv.web.beans.session.RequestOpenUrlQueueBeanInterface;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext; 


/**
 * This session bean manages a queue of urls that have to be opened in new windows.
 * This is a slight hack Illumina BaseSpace windows.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class RequestOpenUrlQueueBean implements RequestOpenUrlQueueBeanInterface {
    
    private static Logger log = Logger.getLogger(IlluminaBean.class);
    BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

    @Override
    public void openURL(String url) {
        queue.offer(url);
    }
    
    @Override
    public void requestNextLaunch() {
        
         String url="";
        // yes this has to be here, I hate it, too
        synchronized(this) {
            if(queue.isEmpty()) {
                return;
            }
            try {
                url = queue.take();
            } catch (InterruptedException ex) {
                //just ignore, no waiting here anyway thanks to synchronized
            }
        }
        
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("window.open(\""+url+"\");");
    }
    
}
