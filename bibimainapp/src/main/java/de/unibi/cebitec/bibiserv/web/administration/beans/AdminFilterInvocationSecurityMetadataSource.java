package de.unibi.cebitec.bibiserv.web.administration.beans;

import de.unibi.cebitec.bibiserv.web.administration.beans.modulemanager.Module;
import de.unibi.cebitec.bibiserv.web.administration.beans.modulemanager.ModuleDatabase;
import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.AntUrlPathMatcher;
import org.springframework.security.web.util.UrlMatcher;

/**
 * Provides the kind of metadata that is required to determine if a user is
 * authorized to access the admin area and/or specific modules. (This class is
 * derived from Springs' DefaultFilterInvocationSecurityMetadataSource)
 *
 * NOTE: In this class the order in that the statements are executed does really
 * matter!
 */
public class AdminFilterInvocationSecurityMetadataSource implements InitializingBean, FilterInvocationSecurityMetadataSource {

    private static final Set<String> HTTP_METHODS = new HashSet<>(Arrays.asList("DELETE", "GET", "HEAD", "OPTIONS", "POST", "PUT", "TRACE"));
    protected final Logger logger = Logger.getLogger(AdminFilterInvocationSecurityMetadataSource.class);
    /**
     * Database access to get module role restrictions.
     */
    private ModuleDatabase moduleDatabase;
    //Using LinkedHashMap to preserve insertion order:
    private Map<String, Map<Object, Collection<ConfigAttribute>>> httpMethodMap = new LinkedHashMap<>();
    private UrlMatcher urlMatcher;
    private boolean stripQueryStringFromUrls;
    private Map<String, Collection<ConfigAttribute>> staticRules;
    private static final String ADMIN_URL_BASE = "/admin";

    public AdminFilterInvocationSecurityMetadataSource() {
        this.urlMatcher = new AntUrlPathMatcher(true);
        //Using LinkedHashMap again to preserve insertion order:
        staticRules = new LinkedHashMap<>();

        /*
         * Initializing static url interceptions that always apply.
         */
        //from more specific ...

        Collection<ConfigAttribute> moduleManagerRolesAllowed = new ArrayList<>();
        moduleManagerRolesAllowed.add(new SecurityConfig(Authority.ROLE_ADMIN.toString()));
        staticRules.put(ADMIN_URL_BASE + "/moduleManager/**", moduleManagerRolesAllowed);

        Collection<ConfigAttribute> adminAreaRolesAllowed = new ArrayList<>();
        adminAreaRolesAllowed.add(new SecurityConfig(Authority.ROLE_ADMIN.toString()));
        adminAreaRolesAllowed.add(new SecurityConfig(Authority.ROLE_DEVELOPER.toString()));
        adminAreaRolesAllowed.add(new SecurityConfig(Authority.ROLE_USER.toString()));
        staticRules.put(ADMIN_URL_BASE + "/**", adminAreaRolesAllowed);
        
        //... to less specific
    }

    public void recreateFilters() {
        //clear old data (inportant!)
        this.httpMethodMap.clear();

        Map<String, Module> modulesMap = moduleDatabase.getAll();
        //from more specific ...

        /*
         * Adding one url interception for each module.
         */
        for (Module module : modulesMap.values()) {
            Collection<ConfigAttribute> roles = new ArrayList<>();
            if (module.isActive()) {
                for (Authority role : module.getInfo().getRoleRestrictions()) {
                    roles.add(new SecurityConfig(role.name()));
                }
            }
            StringBuilder urlPattern = new StringBuilder(ADMIN_URL_BASE + "/");
            urlPattern.append(module.getId());
            urlPattern.append("/web/**");
            addSecureUrl(urlPattern.toString(), null, roles);
        }

        /*
         * Adding static url interceptions to the list. We assume that they are
         * always less specific than the module entrys we inserted before.
         */
        for (Map.Entry<String, Collection<ConfigAttribute>> staticRule : staticRules.entrySet()) {
            addSecureUrl(staticRule.getKey(), null, staticRule.getValue());
        }

        //... to less specific
    }

    /*
     * PostConstruct call used to create all filters at startup once.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        recreateFilters();
    }

    /*
     * ########################################################################
     * NOTE: All code below this line is derived from Springs'
     * DefaultFilterInvocationSecurityMetadataSource as is.
     */
    private void addSecureUrl(String pattern, String method, Collection<ConfigAttribute> attrs) {
        Map<Object, Collection<ConfigAttribute>> mapToUse = getRequestMapForHttpMethod(method);

        mapToUse.put(urlMatcher.compile(pattern), attrs);

        if (logger.isDebugEnabled()) {
            logger.debug("Added URL pattern: " + pattern + "; attributes: " + attrs
                    + (method == null ? "" : " for HTTP method '" + method + "'"));
        }
    }

    private Map<Object, Collection<ConfigAttribute>> getRequestMapForHttpMethod(String method) {
        if (method != null && !HTTP_METHODS.contains(method)) {
            throw new IllegalArgumentException("Unrecognised HTTP method: '" + method + "'");
        }

        Map<Object, Collection<ConfigAttribute>> methodRequestMap = httpMethodMap.get(method);

        if (methodRequestMap == null) {
            methodRequestMap = new LinkedHashMap<Object, Collection<ConfigAttribute>>();
            httpMethodMap.put(method, methodRequestMap);
        }

        return methodRequestMap;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<ConfigAttribute>();

        for (Map.Entry<String, Map<Object, Collection<ConfigAttribute>>> entry : httpMethodMap.entrySet()) {
            for (Collection<ConfigAttribute> attrs : entry.getValue().values()) {
                allAttributes.addAll(attrs);
            }
        }

        return allAttributes;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) {
        if ((object == null) || !this.supports(object.getClass())) {
            throw new IllegalArgumentException("Object must be a FilterInvocation");
        }

        String url = ((FilterInvocation) object).getRequestUrl();
        String method = ((FilterInvocation) object).getHttpRequest().getMethod();

        return lookupAttributes(url, method);
    }

    public final Collection<ConfigAttribute> lookupAttributes(String url, String method) {
        if (stripQueryStringFromUrls) {
            int firstQuestionMarkIndex = url.indexOf("?");

            if (firstQuestionMarkIndex != -1) {
                url = url.substring(0, firstQuestionMarkIndex);
            }
        }

        if (urlMatcher.requiresLowerCaseUrl()) {
            url = url.toLowerCase();

            if (logger.isDebugEnabled()) {
                logger.debug("Converted URL to lowercase, from: '" + url + "'; to: '" + url + "'");
            }
        }

        Collection<ConfigAttribute> attributes = extractMatchingAttributes(url, httpMethodMap.get(method));

        if (attributes == null) {
            attributes = extractMatchingAttributes(url, httpMethodMap.get(null));
        }

        return attributes;
    }

    private Collection<ConfigAttribute> extractMatchingAttributes(String url, Map<Object, Collection<ConfigAttribute>> map) {
        if (map == null) {
            return null;
        }

        final boolean debug = logger.isDebugEnabled();

        for (Map.Entry<Object, Collection<ConfigAttribute>> entry : map.entrySet()) {
            Object p = entry.getKey();
            boolean matched = urlMatcher.pathMatchesUrl(entry.getKey(), url);

            if (debug) {
                logger.debug("Candidate is: '" + url + "'; pattern is " + p + "; matched=" + matched);
            }

            if (matched) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    protected UrlMatcher getUrlMatcher() {
        return urlMatcher;
    }

    public boolean isConvertUrlToLowercaseBeforeComparison() {
        return urlMatcher.requiresLowerCaseUrl();
    }

    public void setStripQueryStringFromUrls(boolean stripQueryStringFromUrls) {
        this.stripQueryStringFromUrls = stripQueryStringFromUrls;
    }

    public ModuleDatabase getModuleDatabase() {
        return moduleDatabase;
    }

    public void setModuleDatabase(ModuleDatabase moduleDatabase) {
        this.moduleDatabase = moduleDatabase;
    }
}