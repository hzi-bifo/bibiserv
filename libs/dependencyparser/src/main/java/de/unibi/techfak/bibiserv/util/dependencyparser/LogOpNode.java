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

/**
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class LogOpNode extends OpNode {

    public LogOpNode(Operations operation) {
        super(operation);
    }

    public LogOpNode(Operations operation, Node a) {
        super(operation, a);
    }

    public LogOpNode(Operations operation, Node a, Node b) {
        super(operation, a, b);
    }

    @Override
    public boolean evaluate() throws DependencyException {
        // evaluate child node
        boolean t_a, t_b = false;

        ConstraintHashMap local_fullfilledConstraints = new ConstraintHashMap();
        ConstraintHashMap local_missingConstraints = new ConstraintHashMap();

        // ---------- node 1 -----------
        t_a = a.evaluate();
        // local_fullfilledConstraints.put(null, null) @ToDO  Verknuepfungen von Teilelementen evtl. ebenfalls abspeichern dafuer

        local_fullfilledConstraints.putAll(a.getFullfilledConstraints());
        local_missingConstraints.putAll(a.getMissingConstraints());

        // ----------- node 2 ----------
        if (b != null) {
            t_b = b.evaluate();
            local_fullfilledConstraints.putAll(b.getFullfilledConstraints());
            local_missingConstraints.putAll(b.getMissingConstraints());

        }

        boolean result = false;
        switch (operation) {
            case AND:
                result = t_a & t_b;
                missingconstraints = local_missingConstraints;
                fullfilledconstraints = local_fullfilledConstraints;
                break;
            case OR:
                result = t_a | t_b;
                missingconstraints = local_missingConstraints;
                fullfilledconstraints = local_fullfilledConstraints;
                break;
            case XOR:
                result = t_a ^ t_b;
                missingconstraints = local_missingConstraints;
                fullfilledconstraints = local_fullfilledConstraints;
                break;
            case NOT:
                result = !t_a;
                missingconstraints = local_fullfilledConstraints;
                swap_cmp(missingconstraints);
                fullfilledconstraints = local_missingConstraints;
                swap_cmp(fullfilledconstraints);
                break;

            default:
                throw new DependencyException("Unsupported Operation '" + operation.name() + "'", DependencyExceptionEnum.unsupportedOperation, operation.name());

        }

        return result;
    }






    /**
     * private helper function that negates cmp operations (needed for logop not)
     *
     *
     * @param chm
     */
    private void swap_cmp(ConstraintHashMap chm) {
        for (Id id : chm.keySet()) {
            if (chm.get(id) != null) {
                for (Constraints c : chm.get(id)) {
                    switch (c.getOperation()) {
                        case EQ:
                            c.setOperation(Operations.NE);
                            break;
                        case NE:
                            c.setOperation(Operations.EQ);
                            break;
                        case GT:
                            c.setOperation(Operations.LE);
                            break;
                        case GE:
                            c.setOperation(Operations.LT);
                            break;
                        case LT:
                            c.setOperation(Operations.GE);
                            break;
                        case LE:
                            c.setOperation(Operations.GT);
                            break;
                    }
                }
            }
        }


    }
}
