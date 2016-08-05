
package de.unibi.techfak.bibiserv.web.beans.session;


/**
 * Saves the data of one entry in the current toolchain.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class ToolChainItem {
    

    private String function; // the function that should redirect to the next stage
    private int inputIndex; // the index of the input that should be set
    private String nextFunctionUrl;  // url too to redirect to    
    private Class controllerClass; // class of the controller of the next function
    private String controllerExpression; // expression of the controller of the next function
    private String nextFunctionName; // name of function to redirect to
  
    // chain info
    private String chaintitle; // title of the chain to display when it is active

    public ToolChainItem(String function, int inputIndex, String nextFunctionUrl, Class controllerClass, String controllerExpression, String nextFunctionName, String chaintitle) {
        this.function = function;
        this.inputIndex = inputIndex;
        this.nextFunctionUrl = nextFunctionUrl;
        this.controllerClass = controllerClass;
        this.controllerExpression = controllerExpression;
        this.chaintitle = chaintitle;
        this.nextFunctionName = nextFunctionName;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public int getInputIndex() {
        return inputIndex;
    }

    public void setInputIndex(int inputIndex) {
        this.inputIndex = inputIndex;
    }

    public String getNextFunctionUrl() {
        return nextFunctionUrl;
    }

    public void setNextFunctionUrl(String nextFunctionUrl) {
        this.nextFunctionUrl = nextFunctionUrl;
    }

    public Class getControllerClass() {
        return controllerClass;
    }

    public void setControllerClass(Class controllerClass) {
        this.controllerClass = controllerClass;
    }

    public String getControllerExpression() {
        return controllerExpression;
    }

    public void setControllerExpression(String controllerExpression) {
        this.controllerExpression = controllerExpression;
    }

    public String getChaintitle() {
        return chaintitle;
    }

    public void setChaintitle(String chaintitle) {
        this.chaintitle = chaintitle;
    }

    public String getNextFunctionName() {
        return nextFunctionName;
    }

    public void setNextFunctionName(String nextFunctionName) {
        this.nextFunctionName = nextFunctionName;
    }
    
    
}
