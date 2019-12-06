// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.net.URLEncoder;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.net.URI;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class URISupport
{
    public static Map<String, String> parseQuery(String uri) throws URISyntaxException {
        try {
            uri = uri.substring(uri.lastIndexOf("?") + 1);
            final Map<String, String> rc = new HashMap<String, String>();
            if (uri != null && !uri.isEmpty()) {
                final String[] parameters = uri.split("&");
                for (int i = 0; i < parameters.length; ++i) {
                    final int p = parameters[i].indexOf("=");
                    if (p >= 0) {
                        final String name = URLDecoder.decode(parameters[i].substring(0, p), "UTF-8");
                        final String value = URLDecoder.decode(parameters[i].substring(p + 1), "UTF-8");
                        rc.put(name, value);
                    }
                    else {
                        rc.put(parameters[i], null);
                    }
                }
            }
            return rc;
        }
        catch (UnsupportedEncodingException e) {
            throw (URISyntaxException)new URISyntaxException(e.toString(), "Invalid encoding").initCause(e);
        }
    }
    
    public static Map<String, String> parseParameters(final URI uri) throws URISyntaxException {
        if (!isCompositeURI(uri)) {
            return (uri.getQuery() == null) ? emptyMap() : parseQuery(stripPrefix(uri.getQuery(), "?"));
        }
        final CompositeData data = parseComposite(uri);
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.putAll(data.getParameters());
        if (parameters.isEmpty()) {
            parameters = emptyMap();
        }
        return parameters;
    }
    
    public static URI applyParameters(final URI uri, final Map<String, String> queryParameters) throws URISyntaxException {
        return applyParameters(uri, queryParameters, "");
    }
    
    public static URI applyParameters(URI uri, final Map<String, String> queryParameters, final String optionPrefix) throws URISyntaxException {
        if (queryParameters != null && !queryParameters.isEmpty()) {
            final StringBuffer newQuery = (uri.getRawQuery() != null) ? new StringBuffer(uri.getRawQuery()) : new StringBuffer();
            for (final Map.Entry<String, String> param : queryParameters.entrySet()) {
                if (param.getKey().startsWith(optionPrefix)) {
                    if (newQuery.length() != 0) {
                        newQuery.append('&');
                    }
                    final String key = param.getKey().substring(optionPrefix.length());
                    newQuery.append(key).append('=').append(param.getValue());
                }
            }
            uri = createURIWithQuery(uri, newQuery.toString());
        }
        return uri;
    }
    
    private static Map<String, String> emptyMap() {
        return (Map<String, String>)Collections.EMPTY_MAP;
    }
    
    public static URI removeQuery(final URI uri) throws URISyntaxException {
        return createURIWithQuery(uri, null);
    }
    
    public static URI createURIWithQuery(final URI uri, final String query) throws URISyntaxException {
        String schemeSpecificPart = uri.getRawSchemeSpecificPart();
        int questionMark = schemeSpecificPart.lastIndexOf("?");
        if (questionMark < schemeSpecificPart.lastIndexOf(")")) {
            questionMark = -1;
        }
        if (questionMark > 0) {
            schemeSpecificPart = schemeSpecificPart.substring(0, questionMark);
        }
        if (query != null && query.length() > 0) {
            schemeSpecificPart = schemeSpecificPart + "?" + query;
        }
        return new URI(uri.getScheme(), schemeSpecificPart, uri.getFragment());
    }
    
    public static CompositeData parseComposite(final URI uri) throws URISyntaxException {
        final CompositeData rc = new CompositeData();
        rc.scheme = uri.getScheme();
        final String ssp = stripPrefix(uri.getRawSchemeSpecificPart().trim(), "//").trim();
        parseComposite(uri, rc, ssp);
        rc.fragment = uri.getFragment();
        return rc;
    }
    
    public static boolean isCompositeURI(final URI uri) {
        final String ssp = stripPrefix(uri.getRawSchemeSpecificPart().trim(), "//").trim();
        return ssp.indexOf(40) == 0 && checkParenthesis(ssp);
    }
    
    public static int indexOfParenthesisMatch(final String str, final int first) throws URISyntaxException {
        int index = -1;
        if (first < 0 || first > str.length()) {
            throw new IllegalArgumentException("Invalid position for first parenthesis: " + first);
        }
        if (str.charAt(first) != '(') {
            throw new IllegalArgumentException("character at indicated position is not a parenthesis");
        }
        int depth = 1;
        char[] array;
        char current;
        for (array = str.toCharArray(), index = first + 1; index < array.length; ++index) {
            current = array[index];
            if (current == '(') {
                ++depth;
            }
            else if (current == ')' && --depth == 0) {
                break;
            }
        }
        if (depth != 0) {
            throw new URISyntaxException(str, "URI did not contain a matching parenthesis.");
        }
        return index;
    }
    
    private static void parseComposite(final URI uri, final CompositeData rc, final String ssp) throws URISyntaxException {
        if (!checkParenthesis(ssp)) {
            throw new URISyntaxException(uri.toString(), "Not a matching number of '(' and ')' parenthesis");
        }
        final int initialParen = ssp.indexOf("(");
        String componentString;
        String params;
        if (initialParen == 0) {
            rc.host = ssp.substring(0, initialParen);
            int p = rc.host.indexOf("/");
            if (p >= 0) {
                rc.path = rc.host.substring(p);
                rc.host = rc.host.substring(0, p);
            }
            p = indexOfParenthesisMatch(ssp, initialParen);
            componentString = ssp.substring(initialParen + 1, p);
            params = ssp.substring(p + 1).trim();
        }
        else {
            componentString = ssp;
            params = "";
        }
        final String[] components = splitComponents(componentString);
        rc.components = new URI[components.length];
        for (int i = 0; i < components.length; ++i) {
            rc.components[i] = new URI(components[i].trim());
        }
        int p = params.indexOf("?");
        if (p >= 0) {
            if (p > 0) {
                rc.path = stripPrefix(params.substring(0, p), "/");
            }
            rc.parameters = parseQuery(params.substring(p + 1));
        }
        else {
            if (params.length() > 0) {
                rc.path = stripPrefix(params, "/");
            }
            rc.parameters = emptyMap();
        }
    }
    
    private static String[] splitComponents(final String str) {
        final List<String> l = new ArrayList<String>();
        int last = 0;
        int depth = 0;
        final char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            switch (chars[i]) {
                case '(': {
                    ++depth;
                    break;
                }
                case ')': {
                    --depth;
                    break;
                }
                case ',': {
                    if (depth == 0) {
                        final String s = str.substring(last, i);
                        l.add(s);
                        last = i + 1;
                        break;
                    }
                    break;
                }
            }
        }
        final String s2 = str.substring(last);
        if (s2.length() != 0) {
            l.add(s2);
        }
        final String[] rc = new String[l.size()];
        l.toArray(rc);
        return rc;
    }
    
    public static String stripPrefix(final String value, final String prefix) {
        if (value.startsWith(prefix)) {
            return value.substring(prefix.length());
        }
        return value;
    }
    
    public static URI stripScheme(final URI uri) throws URISyntaxException {
        return new URI(stripPrefix(uri.getSchemeSpecificPart().trim(), "//"));
    }
    
    public static String createQueryString(final Map<String, ?> options) throws URISyntaxException {
        try {
            if (options.size() > 0) {
                final StringBuffer rc = new StringBuffer();
                boolean first = true;
                for (final String key : options.keySet()) {
                    if (first) {
                        first = false;
                    }
                    else {
                        rc.append("&");
                    }
                    final String value = (String)options.get(key);
                    rc.append(URLEncoder.encode(key, "UTF-8"));
                    rc.append("=");
                    rc.append(URLEncoder.encode(value, "UTF-8"));
                }
                return rc.toString();
            }
            return "";
        }
        catch (UnsupportedEncodingException e) {
            throw (URISyntaxException)new URISyntaxException(e.toString(), "Invalid encoding").initCause(e);
        }
    }
    
    public static URI createRemainingURI(final URI originalURI, final Map<String, String> params) throws URISyntaxException {
        String s = createQueryString(params);
        if (s.length() == 0) {
            s = null;
        }
        return createURIWithQuery(originalURI, s);
    }
    
    public static URI changeScheme(final URI bindAddr, final String scheme) throws URISyntaxException {
        return new URI(scheme, bindAddr.getUserInfo(), bindAddr.getHost(), bindAddr.getPort(), bindAddr.getPath(), bindAddr.getQuery(), bindAddr.getFragment());
    }
    
    public static boolean checkParenthesis(final String str) {
        boolean result = true;
        if (str != null) {
            int open = 0;
            int closed = 0;
            for (int i = 0; (i = str.indexOf(40, i)) >= 0; ++i, ++open) {}
            for (int i = 0; (i = str.indexOf(41, i)) >= 0; ++i, ++closed) {}
            result = (open == closed);
        }
        return result;
    }
    
    public static class CompositeData
    {
        private String host;
        private String scheme;
        private String path;
        private URI[] components;
        private Map<String, String> parameters;
        private String fragment;
        
        public URI[] getComponents() {
            return this.components;
        }
        
        public String getFragment() {
            return this.fragment;
        }
        
        public Map<String, String> getParameters() {
            return this.parameters;
        }
        
        public String getScheme() {
            return this.scheme;
        }
        
        public String getPath() {
            return this.path;
        }
        
        public String getHost() {
            return this.host;
        }
        
        public URI toURI() throws URISyntaxException {
            final StringBuffer sb = new StringBuffer();
            if (this.scheme != null) {
                sb.append(this.scheme);
                sb.append(':');
            }
            if (this.host != null && this.host.length() != 0) {
                sb.append(this.host);
            }
            else {
                sb.append('(');
                for (int i = 0; i < this.components.length; ++i) {
                    if (i != 0) {
                        sb.append(',');
                    }
                    sb.append(this.components[i].toString());
                }
                sb.append(')');
            }
            if (this.path != null) {
                sb.append('/');
                sb.append(this.path);
            }
            if (!this.parameters.isEmpty()) {
                sb.append("?");
                sb.append(URISupport.createQueryString(this.parameters));
            }
            if (this.fragment != null) {
                sb.append("#");
                sb.append(this.fragment);
            }
            return new URI(sb.toString());
        }
    }
}
