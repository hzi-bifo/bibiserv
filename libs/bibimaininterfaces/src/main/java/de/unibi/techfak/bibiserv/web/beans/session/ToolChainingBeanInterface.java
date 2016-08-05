
package de.unibi.techfak.bibiserv.web.beans.session;

import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;

/**
 * Handles an active toolchain. Only one toolchain can be kept up for one session!
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public interface ToolChainingBeanInterface {
    
    
     /**
     * Reset the whole chain, completely emptying it.
     */
    public void resetChain();
    
    /**
     * Adds an item to the end of the chain.
     * @param function the function that has to calculate the value
     * @param nextFunctionUrl url to redirect to
     * @param controllerClass class of the function controller to redirect to
     * @param controllerExpression expression of the function controller to redirect to
     */
    public void addChainItem(ToolChainItem chainItem);
        
    
    /**
     * Returns if the function is the next part in the current chain.
     * @param function current function to test
     * @return true: current function is the next part of the chain
     */
    public boolean isChaining(String function);
    
    /**
     * Return the next chainitem if the function is the next part in the current chain
     * or otherwise null.
     * @param function current function to test
     * @return 
     */
    public ToolChainItem getChainItem(String function);
    
    /**
     * Removes the last chain item if the function corresponds to last chain item.
     * @param function 
     */
    public void removeChainItem(String function);
    
    
}
