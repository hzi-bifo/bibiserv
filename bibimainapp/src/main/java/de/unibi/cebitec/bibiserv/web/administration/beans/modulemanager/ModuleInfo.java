
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de, 
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
 * "Portions Copyrighted 2011 BiBiServ Curator Team"
 * 
 * Contributor(s):
 *	Christian Henke <chenke@cebitec.uni-bielefeld.de>
 * 
 */
package de.unibi.cebitec.bibiserv.web.administration.beans.modulemanager;

import de.unibi.cebitec.bibiserv.web.administration.beans.Authority;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import org.apache.log4j.Logger;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "module")
public class ModuleInfo {

    private final static Logger LOG = Logger.getLogger(ModuleInfo.class);
    @XmlElement(required = true)
    private String id;
    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String version;
    @XmlElement(required = true, name = "role-restrictions")
    private String roleRestrictions; // is CSV - internal ONLY
    @XmlTransient
    private Set<Authority> roleRestrictionsSet;
    @XmlElementWrapper(name = "authors")
    @XmlElement(required = true, name = "author")
    private List<ModuleAuthor> authors;
    @XmlElement(required = true)
    private String description;
    @XmlElementWrapper(name = "manual")
    @XmlElement(required = true, name = "chapter")
    private List<ModuleManualChapter> manual;

    public ModuleInfo() {
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        try {
            List<Authority> roleRestrictionsList = new ArrayList<Authority>();
            String[] rawRoles = this.roleRestrictions.split(",");
            for (String rawRole : rawRoles) {
                for (Authority auth : Authority.values()) {
                    if (rawRole.trim().equalsIgnoreCase(auth.name())) { // check role names, drop invalid ones
                        roleRestrictionsList.add(auth);
                    }
                }
            }
            //roleRestrictionsList.add(Authority.ROLE_ADMIN); // Admin has access to everything by default
            if (roleRestrictionsList.isEmpty()) {
                this.roleRestrictionsSet = new TreeSet<Authority>();
            } else {
                this.roleRestrictionsSet = EnumSet.copyOf(roleRestrictionsList);
            }
        } catch (Exception e) {
            LOG.error("Error while converting role string to enum.", e);
        }
        StringBuilder authorsAndVersion = new StringBuilder();

        authorsAndVersion.append(
                "Version: ");
        authorsAndVersion.append(
                this.version);
        authorsAndVersion.append(
                "; Author(s): ");
        for (int c = 0;
                c
                < this.authors.size();
                ++c) {
            if (c != 0) {
                authorsAndVersion.append(", ");
            }
            ModuleAuthor a = this.authors.get(c);
            authorsAndVersion.append(a.getFirstname());
            authorsAndVersion.append(" ");
            authorsAndVersion.append(a.getLastname());
            authorsAndVersion.append(" <");
            authorsAndVersion.append(a.getEmail());
            authorsAndVersion.append(">");
        }

        this.manual.add(
                0, new ModuleManualChapter("Version and Author(s)", authorsAndVersion.toString()));
    }

    void beforeMarshal(Marshaller m) {
        if (this.manual.size() > 0) {
            this.manual.remove(0); //strip Version and Author(s) from manual
        }
        // create role CSV from enums
        StringBuilder roleEnumCsv = new StringBuilder();
        boolean first = true;
        for (Authority role : this.roleRestrictionsSet) {
            if (!first) {
                roleEnumCsv.append(",");
            }
            roleEnumCsv.append(role.name());
            first = false;
        }
        this.roleRestrictions = roleEnumCsv.toString();
    }

    public String getName() {
        return name;
    }

    public List<ModuleAuthor> getAuthors() {
        return authors;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public List<ModuleManualChapter> getManual() {
        return manual;
    }

    public Set<Authority> getRoleRestrictions() {
        return this.roleRestrictionsSet;
    }

    public void setRoleRestrictions(Set<Authority> roleRestrictionsSet) {
        this.roleRestrictionsSet = roleRestrictionsSet;
    }

    public String getVersion() {
        return version;
    }
}
