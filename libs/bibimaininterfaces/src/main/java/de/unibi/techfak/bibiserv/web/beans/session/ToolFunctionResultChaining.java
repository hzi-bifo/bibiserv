
package de.unibi.techfak.bibiserv.web.beans.session;

/**
 * Defines Methods for tool chaining of result pages.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public interface ToolFunctionResultChaining {
    
    //////////////// Chaining Extension /////////////////
    
    /**
     * Returns if a the result is the next part of a currently active toolchain. 
     * @return true: part of next toolchain
     */
    public boolean isChaining();
    
    /**
     * If possible, set the result for the next tool in chain and redirect.
     */
    public void advanceToolChain();
    
    /**
     * Returns the name of the next tool in the chain, possibly extended by the function name.
     * @return 
     */
    public String getNextChainToolname();
    
}
