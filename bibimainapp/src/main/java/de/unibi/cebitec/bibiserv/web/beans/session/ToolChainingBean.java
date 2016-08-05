
package de.unibi.cebitec.bibiserv.web.beans.session;

import java.util.LinkedList;
import java.util.Queue;
import de.unibi.techfak.bibiserv.web.beans.session.ToolChainingBeanInterface;
import de.unibi.techfak.bibiserv.web.beans.session.ToolChainItem;

/**
 * Handles an active toolchain. Only one toolchain can be kept up for one session!
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class ToolChainingBean implements ToolChainingBeanInterface {
    
    private Queue<ToolChainItem> chain = new LinkedList<>();

    /**
     * Reset the whole chain, completely emptying it.
     */
    @Override
    public void resetChain() {
        chain = new LinkedList<>();
    }
    
    /**
     * Adds an item to the end of the chain.
     * @param function the function that has to calculate the value
     * @param nextFunctionUrl url to redirect to
     * @param controllerClass class of the function controller to redirect to
     * @param controllerExpression expression of the function controller to redirect to
     */
    @Override
    public void addChainItem(ToolChainItem tci)  {
        chain.add(tci);
    }
    
    /**
     * Returns if the function is the next part in the current chain.
     * @param function current function to test
     * @return true: current function is the next part of the chain
     */
    @Override
    public boolean isChaining(String function) {
        if (chain.isEmpty()) {
            return false;
        }
        return chain.peek().getFunction().equals(function);
    } 
    
     /**
     * Return the next chainitem if the function is the next part in the current chain
     * or otherwise null.
     * @param function current function to test
     * @return 
     */
    @Override
    public ToolChainItem getChainItem(String function) {
         if (!isChaining(function)) {
            return null;
         }
         return chain.peek();
    } 
    
    /**
     * Removes the last chain item if the function corresponds to last chain item.
     * @param function 
     */
    @Override
    public void removeChainItem(String function) {
        if (chain.isEmpty()) {
            return;
        }
        if(chain.peek().getFunction().equals(function)) { // to hindern not qualfied tools from removing
            chain.remove();
        }
    }

    
    
}
