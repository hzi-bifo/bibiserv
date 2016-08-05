/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Benjamin Paassen, CeBiTec,
 * http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2012 Benjamin Paassen"
 *
 * Contributor(s): Benjamin Paassen
 *
 */ 
package de.unibi.cebitec.bibiserv.search.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class contains general utility functions for Collections.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class Collections {

    /**
     * Analyses two collections if the first contains all elements of the
     * second.
     *
     * Therefore this returns true if the second collection is a subset of the
     * first.
     *
     * @param first The collection that may contain all elements of the other
     * one.
     * @param second The collection that may be contained by the other one.
     * @return true if the second collection is a subset of the first.
     */
    public static <X> boolean containsAll(Collection<X> first, Collection<X> second) {
        for (X key : second) {
            if (!first.contains(key)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Analyses two collections if the first contains no elements of the
     * second.
     *
     * Therefore this returns true if the two collections are disjoint.
     *
     * @param first The collection that may contain elements of the other
     * one.
     * @param second The collection which elements may be contained by the
     * first one.
     * @return true if the two collections are disjoint.
     */
    public static <X> boolean containsNone(Collection<X> first, Collection<X> second) {
        for (X key : second) {
            if (first.contains(key)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method allows you to genericly transform collections.
     *
     * @param <Y> target class of the transformation.
     * @param collection a collection of objects.
     * @param transformation the transformation action that transforms
     * elements of the given collection into your desired target class.
     * @return A copy of the transformed collection.
     */
    public static <X, Y> ArrayList<Y> transform(Collection<? extends X> collection,
            TransformationAction<X, Y> transformation) {
        ArrayList<Y> returnSet = new ArrayList<>();
        for (X object : collection) {
            returnSet.add(transformation.doTransformation(object));
        }
        return returnSet;
    }
}
