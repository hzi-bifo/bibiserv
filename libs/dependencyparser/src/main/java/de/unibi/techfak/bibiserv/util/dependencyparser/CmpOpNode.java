/*
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
All rights reserved.

The contents of this file are subject to the terms of the Common
Development and Distribution License("CDDL") (the "License"). You
may not use this file except in compliance with the License. You can
obtain a copy of the License at http://www.sun.com/cddl/cddl.html

See the License for the specific language governing permissions and
limitations under the License.  When distributing the software, include
this License Header Notice in each file.  If applicable, add the following
below the License Header, with the fields enclosed by brackets [] replaced
by your own identifying information:

"Portions Copyrighted 2011 BiBiServ"

Contributor(s):
 */
package de.unibi.techfak.bibiserv.util.dependencyparser;

import java.util.List;

/**
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class CmpOpNode extends OpNode {

    public CmpOpNode(Operations operation) {
        super(operation);
    }

    public CmpOpNode(Operations operation, LeafNode a) {
        super(operation, a);
    }

    public CmpOpNode(Operations operation, LeafNode a, LeafNode b) {
        super(operation, a, b);
    }

    @Override
    public boolean evaluate() throws DependencyException {
        missingconstraints.clear();
        fullfilledconstraints.clear();

        boolean t_a = a.evaluate();
        boolean t_b = b.evaluate();

        boolean result = true;

        if (t_a & t_b) {

            List<Object> v_a_l = ((LeafNode) a).getValues();
            List<Object> v_b_l = ((LeafNode) b).getValues();

            for (Object v_a : v_a_l) {
                for (Object v_b : v_b_l) {
                    boolean intermediate_result;
                    switch (operation) {
                        case EQ:
                            intermediate_result =  compare(v_a, v_b, 0);
                            break;
                        case NE:
                            intermediate_result =  !compare(v_a, v_b, 0);
                            break;
                        case LT:
                            intermediate_result =  compare(v_a, v_b, -1);
                            break;
                        case LE:
                            intermediate_result =  (compare(v_a, v_b, 0) | compare(v_a, v_b, -1));
                            break;
                        case GT:
                            intermediate_result =  compare(v_a, v_b, 1);
                            break;
                        case GE:
                            intermediate_result =  (compare(v_a, v_b, 0) | compare(v_a, v_b, 1));
                            break;
                        default:
                            throw new DependencyException("Unsupported Operation '" + operation.name() + "'", DependencyExceptionEnum.unsupportedOperation, operation.name());
                    }
                    if (!intermediate_result && a.getClass().equals(Const.class) && b.getClass().equals(Const.class)) {
                        throw new DependencyException("Dependency can't be solved (" + v_a + " " + operation.name() + " " + v_b + ")!", DependencyExceptionEnum.unsolveableDependency, v_a + " " + operation.name() + " " + v_b);
                    }
                    result = result & intermediate_result;
                }
            }
        } else {
            result = false;
        }

        // Nach Definition sind moegliche Kindsknoten fuer a nur ID's und fuer b LeafKnoten (also ID's oder Konstanten)

        fullfilledconstraints.putAll(a.getFullfilledConstraints());
        fullfilledconstraints.putAll(b.getFullfilledConstraints());
        missingconstraints.putAll(a.getMissingConstraints());
        missingconstraints.putAll(b.getMissingConstraints());

        if (result) {
            fullfilledconstraints.put((Id) a, new Constraints(operation, (LeafNode) b));
        } else {
            missingconstraints.put((Id) a, new Constraints(operation, (LeafNode) b));
        }

        return result;
    }

    /**
    Helper method, to represent a clear Source structure ;-)
    @param a - Object representing an id
    @param b - Object representing an id

    @param comp - one of 0(equals), <0(lesserthan) , >0 (greater than)

    @return Returns true if a matches b or c */
    private boolean compare(Object a, Object b, int comp) throws DependencyException {

        if (a == null || b == null) {
            return false;
        }

        String n_a = a.getClass().getName();
        String n_b = b.getClass().getName();


        if (n_a.equals(n_b)) {
            return (Integer.signum(((Comparable) a).compareTo((Comparable) b)) == comp);
        } else {
            // compare int with float
            if (a instanceof Integer) {
                if (b instanceof Float) {
                    return (Integer.signum((new Float(((Integer) a).floatValue())).compareTo((Float) b)) == comp);
                } else if (b instanceof String) {
                    return (Integer.signum((a.toString()).compareTo((String) b)) == comp);
                } else if (b instanceof Boolean) {
                    return ((Integer) a == 0 ? false : true) & (Boolean) b;
                } else {
                    throw new DependencyException("Unsupported type \"" + n_b + "\" can't compared to \"" + n_a + "\"!", DependencyExceptionEnum.unsupportedCompare, n_b+":"+n_a);
                }
            } else if (a instanceof Float) {
                if (b instanceof Integer) {
                    return (Integer.signum(((Float) a).compareTo(new Float(((Integer) b).floatValue()))) == comp);
                } else if (b instanceof String) {
                    return (Integer.signum((a.toString()).compareTo((String) b)) == comp);
                } else if (b instanceof Boolean) {
                    return ((Float) a == 0f ? false : true) & (Boolean) b;
                } else {
                    throw new DependencyException("Unsupported type \"" + n_b + "\" can't compared to \"" + n_a + "\"!", DependencyExceptionEnum.unsupportedCompare, n_b+":"+n_a);
                }

            } else if (a instanceof String) {
                return (Integer.signum(((String) a).compareTo(b.toString())) == comp);
            } else if (a instanceof Boolean) {
                if (b instanceof Integer) {
                    return !((Boolean) a ^ ((Integer) b == 0 ? false : true));
                } else if (b instanceof Float) {
                    return !((Boolean) a ^ ((Float) b == 0 ? false : true));
                } else if (b instanceof String) {
                    return !((Boolean) a ^ Boolean.parseBoolean((String) b));
                } else {
                    throw new DependencyException("Unsupported type \"" + n_b + "\" can't compared to \"" + n_a + "\"!", DependencyExceptionEnum.unsupportedCompare, n_b+":"+n_a);
                }

            } else {
                throw new DependencyException("Unsupported type \"" + n_a + "\" can't compared to \"" + n_b + "\"!", DependencyExceptionEnum.unsupportedCompare, n_b+":"+n_a);
            }
        }

    }
}
