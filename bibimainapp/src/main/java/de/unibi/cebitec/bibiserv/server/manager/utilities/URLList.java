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
package de.unibi.cebitec.bibiserv.server.manager.utilities;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

/**
 *  This class is a simple implementation for an URL list implementing
 *  the Enumeration interface.
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class URLList extends ArrayList<URL>implements Enumeration<URL>{

    private int marker = -1;

    @Override
    public boolean add(URL e) {
        reset();
        return super.add(e);
    }

    @Override
    public void add(int index, URL element) {
        reset();
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends URL> c) {
        reset();
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends URL> c) {
        reset();
        return super.addAll(index, c);
    }

    @Override
    public void clear() {
        super.clear();
        reset();
    }

    @Override
    public boolean remove(Object o) {
        boolean tmp = super.remove((URL)o);
        reset();
        return tmp;
    }

    @Override
    public URL remove(int index) {
        URL url = super.remove(index);
        reset();
        return url;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean tmp = super.removeAll(c);
        reset();
        return tmp;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
        reset();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean tmp = super.retainAll(c);
        reset();
        return tmp;
    }










    /**
     * Implementation of Interface Enumeration.
     *
     * @return
     */
    @Override
    public boolean hasMoreElements() {
        if (marker >= -1 && marker < size()){
            return true;
        }
        return false;
    }

    /**
     * Implementation of Interface Enumeration.
     *
     * @return
     */
    @Override
    public URL nextElement() {
        try {
            URL tmp = get(marker);
            marker ++;
            return tmp;
        } catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    /**
     * Reset the internal used marker to handle the implemented methods
     * <b>hasMoreElements</b> and <b>nextElement</b>
     *
     */
    public void reset(){
        if (size() > 0) {
            marker = 0;
        } else {
            marker = -1;
        }
    }

    /**
     * Returns the first element from URL List and NULL in the case
     *  of an empty list.
     *
     * @return Return the first element from URL list.
     */
    public URL firstElement() {
        try {
            return get(0);
        } catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    /**
     * Return the last element fom URL List and NULL in the case of an
     * empty list.
     *
     * @return Return the last element from URL list.
     */
    public URL getLastElement(){
        try {
            return get(size()-1);
        } catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    /**
     * Add an enumeration of URL's to current URL List.
     *
     * @param t - Enumeration&lt;URL&gt; to be added
     */
    public void addEnumeration(Enumeration<URL> t){
        while (t.hasMoreElements()){
            add(t.nextElement());
        }
    }

}
