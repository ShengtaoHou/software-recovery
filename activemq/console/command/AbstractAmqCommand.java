// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import org.apache.activemq.ActiveMQConnectionFactory;
import java.net.URISyntaxException;
import java.util.Iterator;
import javax.jms.JMSException;
import java.util.ArrayList;
import javax.jms.Connection;
import java.util.List;
import javax.jms.ConnectionFactory;
import java.net.URI;

public abstract class AbstractAmqCommand extends AbstractCommand
{
    private URI brokerUrl;
    private ConnectionFactory factory;
    private String factoryClassString;
    private String username;
    private String password;
    private PasswordFactory passwordFactory;
    private final List<Connection> connections;
    private String passwordFactoryClassString;
    
    public AbstractAmqCommand() {
        this.connections = new ArrayList<Connection>();
    }
    
    protected Connection createConnection() throws JMSException {
        return this.createConnection(this.getUsername(), this.getPassword());
    }
    
    protected Connection createConnection(final String username, final String password) throws JMSException {
        if (this.getBrokerUrl() == null) {
            this.context.printException(new IllegalStateException("You must specify a broker URL to connect to using the --amqurl option."));
            return null;
        }
        final ConnectionFactory factory = this.getConnectionFactory();
        Connection conn;
        if (null == username && null == password) {
            conn = factory.createConnection();
        }
        else {
            conn = factory.createConnection(username, password);
        }
        this.connections.add(conn);
        conn.start();
        return conn;
    }
    
    protected void closeAllConnections() {
        final Iterator<Connection> i = this.connections.iterator();
        while (i.hasNext()) {
            try {
                i.next().close();
            }
            catch (Exception ex) {}
        }
        this.connections.clear();
    }
    
    @Override
    protected void handleOption(final String token, final List tokens) throws Exception {
        if (token.equals("--amqurl")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("Broker URL not specified."));
                tokens.clear();
                return;
            }
            if (this.getBrokerUrl() != null) {
                this.context.printException(new IllegalArgumentException("Multiple broker URL cannot be specified."));
                tokens.clear();
                return;
            }
            final String strBrokerUrl = tokens.remove(0);
            try {
                this.setBrokerUrl(new URI(strBrokerUrl));
            }
            catch (URISyntaxException e) {
                this.context.printException(e);
                tokens.clear();
            }
        }
        else if (token.equals("--factory")) {
            this.factoryClassString = tokens.remove(0);
        }
        else if (token.equals("--passwordFactory")) {
            this.passwordFactoryClassString = tokens.remove(0);
        }
        else if (token.equals("--password")) {
            this.password = tokens.remove(0);
        }
        else if (token.equals("--user")) {
            this.username = tokens.remove(0);
        }
        else {
            super.handleOption(token, tokens);
        }
    }
    
    protected void setBrokerUrl(final URI brokerUrl) {
        this.brokerUrl = brokerUrl;
    }
    
    protected void setBrokerUrl(final String address) throws URISyntaxException {
        this.brokerUrl = new URI(address);
    }
    
    protected URI getBrokerUrl() {
        return this.brokerUrl;
    }
    
    public ConnectionFactory getConnectionFactory() {
        if (this.factory == null && this.factoryClassString != null) {
            try {
                final Class klass = Class.forName(this.factoryClassString);
                if (this.getUsername() != null || this.getPassword() != null) {
                    this.factory = klass.getConstructor(String.class, String.class, URI.class).newInstance(this.getUsername(), this.getPassword(), this.getBrokerUrl());
                }
                else {
                    this.factory = klass.getConstructor(URI.class).newInstance(this.getBrokerUrl());
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (this.factory == null) {
            if (this.getUsername() != null || this.getPassword() != null) {
                this.factory = new ActiveMQConnectionFactory(this.getUsername(), this.getPassword(), this.getBrokerUrl());
            }
            else {
                this.factory = new ActiveMQConnectionFactory(this.getBrokerUrl());
            }
        }
        return this.factory;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setFactory(final ConnectionFactory factory) {
        this.factory = factory;
    }
    
    public void setUsername(final String username) {
        this.username = username;
    }
    
    public String getPassword() {
        if (null == this.password) {
            return null;
        }
        return this.getPasswordFactory().getPassword(this.password);
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public PasswordFactory getPasswordFactory() {
        if (this.passwordFactory == null && this.passwordFactoryClassString != null) {
            try {
                final Class klass = Class.forName(this.passwordFactoryClassString);
                this.passwordFactory = klass.newInstance();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (this.passwordFactory == null) {
            this.passwordFactory = DefaultPasswordFactory.factory;
        }
        return this.passwordFactory;
    }
    
    public void setPasswordFactory(final PasswordFactory passwordFactory) {
        this.passwordFactory = passwordFactory;
    }
}
