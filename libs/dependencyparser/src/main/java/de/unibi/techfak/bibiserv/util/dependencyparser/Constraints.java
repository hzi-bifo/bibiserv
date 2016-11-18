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

import de.unibi.techfak.bibiserv.util.dependencyparser.OpNode.Operations;
import java.util.List;

/**
 *
 * @author Jan krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class Constraints implements Comparable<Constraints> {

    private OpNode.Operations operation = OpNode.Operations.NULL;
    private LeafNode value = null;

    public Constraints(OpNode.Operations operation, LeafNode value) {
        this.operation = operation;
        this.value = value;

    }

    public void setOperation(OpNode.Operations operation) {
        this.operation = operation;
    }

    public Operations getOperation() {
        return operation;
    }

    public void setValue(LeafNode value) {
        this.value = value;
    }

    public LeafNode getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(operation);
        sb.append(" ");
        sb.append(value.toString());

        return sb.toString();

    }

    public int compareTo(Constraints o) {
        // 1st critera its the operation
        int cmp = operation.compareTo(o.getOperation());
        if (cmp != 0) {
            return cmp;
        }
        // 2nd criteria is the value
        if (value.getClass().getName().equals(o.getValue().getClass().getName())) {
            // compare types
            if ((value instanceof Id) && o.getValue() instanceof Id) {
                return ((Id) value).compareTo((Id) o.getValue());
            }
            if ((value instanceof Id) && o.getValue() instanceof Const) {
                return -1;
            }
            if ((value instanceof Const) && o.getValue() instanceof Id) {
                return 1;
            }
            List la = ((Const) value).getValues();
            List lb = ((Const) (o.getValue())).getValues();
            // both constants (critera length)
            if (la.size() < lb.size()) {
                return -1;
            }

            if (la.size() > lb.size()) {
                return 1;
            }

            // same length -> compare list elements
            for (int c = 0; c < la.size(); ++c) {
                //
                Object a = la.get(c);
                Object b = lb.get(c);
                if (a instanceof Integer) {
                    if (b instanceof Integer) {
                        if ((Integer) a < (Integer) b) {
                            return -1;
                        } else if ((Integer) a > (Integer) b) {
                            return 1;

                        }
                    } else if (b instanceof Float) {
                        if ((Integer) a < (Float) b) {
                            return -1;
                        } else if ((Integer) a > (Float) b) {
                            return 1;
                        }
                    } else if (b instanceof String) {
                        cmp = ((Integer) a).toString().compareTo((String) b);
                        if (cmp != 0) {
                            return cmp;
                        }
                    } else if (b instanceof Boolean) {
                        return -1;
                    } else {
                        throw new RuntimeException("Unsupported Type '" + b.getClass().getCanonicalName() + "'.");
                    }
                } else if (a instanceof Float) {
                    if (b instanceof Integer) {
                        if ((Float) a < (Integer) b) {
                            return -1;
                        } else if ((Float) a > (Integer) b) {
                            return 1;

                        }
                    } else if (b instanceof Float) {
                        if ((Float) a < (Float) b) {
                            return -1;
                        } else if ((Float) a > (Float) b) {
                            return 1;
                        }
                    } else if (b instanceof String) {
                        cmp = ((Float) a).toString().compareTo((String) b);
                        if (cmp != 0) {
                            return cmp;
                        }
                    } else if (b instanceof Boolean) {
                        return -1;
                    } else {
                        throw new RuntimeException("Unsupported Type '" + b.getClass().getCanonicalName() + "'.");
                    }

                } else if (a instanceof String) {
                    if (b instanceof Integer) {
                        cmp = ((String) a).compareTo(((Integer) b).toString());
                        if (cmp != 0) {
                            return cmp;
                        }
                    } else if (b instanceof Float) {
                        cmp = ((String) a).compareTo(((Float) b).toString());
                        if (cmp != 0) {
                            return cmp;
                        }
                    } else if (b instanceof String) {
                        cmp = ((String) a).compareTo((String) b);
                        if (cmp != 0) {
                            return cmp;
                        }
                    } else if (b instanceof Boolean) {
                        cmp = ((String) a).compareTo(((Boolean) b).toString());
                        if (cmp != 0) {
                            return cmp;
                        }
                    } else {
                        throw new RuntimeException("Unsupported Type '" + b.getClass().getCanonicalName() + "'.");
                    }

                } else if (a instanceof Boolean) {
                    if (b instanceof Integer) {
                        return 1;
                    } else if (b instanceof Float) {
                        return 1;
                    } else if (b instanceof String) {
                        cmp = ((String) a).compareTo((String) b);
                        if (cmp != 0) {
                            return cmp;
                        }
                    } else if (b instanceof Boolean) {
                        cmp = ((String) a).compareTo(((Boolean) b).toString());
                        if (cmp != 0) {
                            return cmp;
                        }
                    } else {
                        throw new RuntimeException("Unsupported Type '" + b.getClass().getCanonicalName() + "'.");
                    }
                } else {
                    throw new RuntimeException("Unsupported Type '" + b.getClass().getCanonicalName() + "'.");
                }
            }


            return 0;

        }
        // maybe it's bett to throw an exception  ...
        System.err.println("Type isn't equal ...");
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Constraints)) {
            return false;
        }

        // compare operation
        if (!operation.equals(((Constraints) obj).getOperation())) {
            return false;
        }

        // test kind of value

        LeafNode otherValue = ((Constraints) obj).getValue();


        // value type must be the same
        if (!value.getClass().equals(otherValue.getClass())) {
            return false;
        }

        if (value instanceof Id) {
            return value.equals(otherValue);
        } else {

            // compare values
            List l1 = value.getValues();
            List l2 = otherValue.getValues();

            if (l1.size() != l2.size()) {
                return false;
            }

            for (int c = 0; c < l1.size(); ++c) {
                Object e1 = l1.get(c);
                Object e2 = l2.get(c);
                if (!e1.equals(e2)) {
                    return false;
                }
            }
            return true;
        }
        

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.operation != null ? this.operation.hashCode() : 0);
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
