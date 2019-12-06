// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.discovery.http;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.io.PrintWriter;
import java.util.Map;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import javax.servlet.http.HttpServlet;

public class DiscoveryRegistryServlet extends HttpServlet
{
    private static final Logger LOG;
    long maxKeepAge;
    ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> serviceGroups;
    
    public DiscoveryRegistryServlet() {
        this.maxKeepAge = 3600000L;
        this.serviceGroups = new ConcurrentHashMap<String, ConcurrentHashMap<String, Long>>();
    }
    
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String group = req.getPathInfo();
        final String service = req.getHeader("service");
        DiscoveryRegistryServlet.LOG.debug("Registering: group=" + group + ", service=" + service);
        final ConcurrentHashMap<String, Long> services = this.getServiceGroup(group);
        services.put(service, System.currentTimeMillis());
    }
    
    private ConcurrentHashMap<String, Long> getServiceGroup(final String group) {
        ConcurrentHashMap<String, Long> rc = this.serviceGroups.get(group);
        if (rc == null) {
            rc = new ConcurrentHashMap<String, Long>();
            this.serviceGroups.put(group, rc);
        }
        return rc;
    }
    
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            long freshness = 30000L;
            final String p = req.getParameter("freshness");
            if (p != null) {
                freshness = Long.parseLong(p);
            }
            final String group = req.getPathInfo();
            DiscoveryRegistryServlet.LOG.debug("group=" + group);
            final ConcurrentHashMap<String, Long> services = this.getServiceGroup(group);
            final PrintWriter writer = resp.getWriter();
            final long now = System.currentTimeMillis();
            final long dropTime = now - this.maxKeepAge;
            final long minimumTime = now - freshness;
            final ArrayList<String> dropList = new ArrayList<String>();
            for (final Map.Entry<String, Long> entry : services.entrySet()) {
                if (entry.getValue() > minimumTime) {
                    writer.println(entry.getKey());
                }
                else {
                    if (entry.getValue() >= dropTime) {
                        continue;
                    }
                    dropList.add(entry.getKey());
                }
            }
            for (final String service : dropList) {
                services.remove(service);
            }
        }
        catch (Exception e) {
            resp.sendError(500, "Error occured: " + e);
        }
    }
    
    protected void doDelete(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String group = req.getPathInfo();
        final String service = req.getHeader("service");
        DiscoveryRegistryServlet.LOG.debug("Unregistering: group=" + group + ", service=" + service);
        final ConcurrentHashMap<String, Long> services = this.getServiceGroup(group);
        services.remove(service);
    }
    
    static {
        LOG = LoggerFactory.getLogger(HTTPDiscoveryAgent.class);
    }
}
