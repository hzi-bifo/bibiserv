/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 */ 

package de.unibi.techfak.bibiserv.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implementation of Interface javax.xml.namespace.NamespaceContext with some
 * useful, additional functions.
 * 
 * @author Jan Krueger (jkrueger@techfak.uni-bielefeld.de)
 */
public class NamespaceContext implements javax.xml.namespace.NamespaceContext {

    private Map<String, List<String>> contentmap;

    /**
     * Default constructor. Creates an empty NamespaceContext.
     */
    public NamespaceContext() {
        contentmap = new HashMap<String, List<String>>();
    }

    /**
     * Add a new Namespace (as namespaceURI/prefix pair) to
     * current scope.
     * 
     * @param namespaceURI - URI of namespace to add
     * @param prefix - Prefix of namespace to add
     */
    public void addNamespace(String namespaceURI, String prefix) {
        if (contentmap.containsKey(namespaceURI)) {
            List<String> tmp = contentmap.get(namespaceURI);
            tmp.add(prefix);
        } else {
            List<String> tmp = new ArrayList<String>();
            tmp.add(prefix);
            contentmap.put(namespaceURI, tmp);
        }
    }

    /**
     * Implementation of method getNamespaceURI from Interface
     * javax.xml.namespace.NamespaceContext. 
     * 
     *
     * @param prefix - Prefix of Namespace to lookup
     * @return Namespace URI bound to prefix in the current scope
     */
    public String getNamespaceURI(String prefix) {
        for (String k : contentmap.keySet()) {
            List<String> a = contentmap.get(k);
            for (String b : a) {
                if (b.equals(prefix)) {
                    return k;
                }
            }
        }
        return null;
    }

    /**
     * Implementation of method getNamespaceURI from Interface
     * javax.xml.namespace.NamespaceContext. 
     * Get prefix bound to Namespace URI in the current scope.
     * 
     * @param namespaceURI - URI of namespace to lookup
     * @return prefix bound to Namespace URI in current context
     */
    public String getPrefix(String namespaceURI) {
        if (contentmap.containsKey(namespaceURI)) {
            return contentmap.get(namespaceURI).get(0);
        }
        return null;
    }

    /**
     * Implementation of method getNamespaceURI from Interface
     * javax.xml.namespace.NamespaceContext. 
     * Get a Iterator over all prefixes bound to Namespace URI in the current scope.
     * 
     * @param namespaceURI - URI of namespace to lookup
     * @return Iterator over all prefix bound to Namespace URI in current context
     */
    public Iterator getPrefixes(String namespaceURI) {
        if (contentmap.containsKey(namespaceURI)) {
            return contentmap.get(namespaceURI).iterator();
        }
        return null;
    }
}
