
package de.unibi.techfak.bibiserv.web.beans.session;

import de.unibi.techfak.bibiserv.util.Pair;
import java.util.List;

/**
 * A simple Interface to define functions for all Tool controllers.
 * At the moment used only for tool chaining.
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public interface ToolFunctionController {
    
    public List<InputBeanInterface> getInputs();
    
    public List<Pair<String, Pair<String, List<ToolChainItem>>>> getToolchainStarts(String input);
    
    public void startToolChain(String startUrl, List<ToolChainItem> chainitem);
    
}
