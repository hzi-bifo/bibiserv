package de.unibi.cebitec.bibiserv.web.administration.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * The authorities that are used in the role based authentication system.
 */
public enum Authority {

    ROLE_ADMIN("Administrator", "ADM"),
    ROLE_DEVELOPER("Developer", "DEV"),
    ROLE_USER("User", "USER");
    private String displayName;
    private String shortDisplayName;

    Authority(String displayName, String shortDisplayName) {
        this.displayName = displayName;
        this.shortDisplayName = shortDisplayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortDisplayName() {
        return shortDisplayName;
    }
}
