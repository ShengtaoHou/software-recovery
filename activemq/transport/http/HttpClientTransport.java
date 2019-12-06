// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.http;

import org.slf4j.LoggerFactory;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.AuthScope;
import org.apache.http.HttpHost;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.activemq.util.ServiceStopper;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpHead;
import java.io.DataInput;
import java.io.InterruptedIOException;
import org.apache.http.Header;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.DataInputStream;
import org.apache.http.params.HttpParams;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.command.ShutdownInfo;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.activemq.util.ByteArrayOutputStream;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.client.methods.HttpPost;
import java.io.IOException;
import org.apache.activemq.transport.FutureResponse;
import java.net.URI;
import org.apache.activemq.transport.util.TextWireFormat;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpClient;
import org.apache.activemq.util.IdGenerator;
import org.slf4j.Logger;

public class HttpClientTransport extends HttpTransportSupport
{
    public static final int MAX_CLIENT_TIMEOUT = 30000;
    private static final Logger LOG;
    private static final IdGenerator CLIENT_ID_GENERATOR;
    private HttpClient sendHttpClient;
    private HttpClient receiveHttpClient;
    private final String clientID;
    private boolean trace;
    private HttpGet httpMethod;
    private volatile int receiveCounter;
    private int soTimeout;
    private boolean useCompression;
    protected boolean canSendCompressed;
    private int minSendAsCompressedSize;
    
    public HttpClientTransport(final TextWireFormat wireFormat, final URI remoteUrl) {
        super(wireFormat, remoteUrl);
        this.clientID = HttpClientTransport.CLIENT_ID_GENERATOR.generateId();
        this.soTimeout = 30000;
        this.useCompression = false;
        this.canSendCompressed = false;
        this.minSendAsCompressedSize = 0;
    }
    
    public FutureResponse asyncRequest(final Object command) throws IOException {
        return null;
    }
    
    @Override
    public void oneway(final Object command) throws IOException {
        if (this.isStopped()) {
            throw new IOException("stopped.");
        }
        final HttpPost httpMethod = new HttpPost(this.getRemoteUrl().toString());
        this.configureMethod((AbstractHttpMessage)httpMethod);
        final String data = this.getTextWireFormat().marshalText(command);
        byte[] bytes = data.getBytes("UTF-8");
        if (this.useCompression && this.canSendCompressed && bytes.length > this.minSendAsCompressedSize) {
            final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            final GZIPOutputStream stream = new GZIPOutputStream(bytesOut);
            stream.write(bytes);
            stream.close();
            httpMethod.addHeader("Content-Type", "application/x-gzip");
            if (HttpClientTransport.LOG.isTraceEnabled()) {
                HttpClientTransport.LOG.trace("Sending compressed, size = " + bytes.length + ", compressed size = " + bytesOut.size());
            }
            bytes = bytesOut.toByteArray();
        }
        final ByteArrayEntity entity = new ByteArrayEntity(bytes);
        httpMethod.setEntity((HttpEntity)entity);
        HttpClient client = null;
        HttpResponse answer = null;
        try {
            client = this.getSendHttpClient();
            final HttpParams params = client.getParams();
            HttpConnectionParams.setSoTimeout(params, this.soTimeout);
            answer = client.execute((HttpUriRequest)httpMethod);
            final int status = answer.getStatusLine().getStatusCode();
            if (status != 200) {
                throw new IOException("Failed to post command: " + command + " as response was: " + answer);
            }
            if (command instanceof ShutdownInfo) {
                try {
                    this.stop();
                }
                catch (Exception e) {
                    HttpClientTransport.LOG.warn("Error trying to stop HTTP client: " + e, e);
                }
            }
        }
        catch (IOException e2) {
            throw IOExceptionSupport.create("Could not post command: " + command + " due to: " + e2, e2);
        }
        finally {
            if (answer != null) {
                EntityUtils.consume(answer.getEntity());
            }
        }
    }
    
    @Override
    public Object request(final Object command) throws IOException {
        return null;
    }
    
    private DataInputStream createDataInputStream(final HttpResponse answer) throws IOException {
        final Header encoding = answer.getEntity().getContentEncoding();
        if (encoding != null && "gzip".equalsIgnoreCase(encoding.getValue())) {
            return new DataInputStream(new GZIPInputStream(answer.getEntity().getContent()));
        }
        return new DataInputStream(answer.getEntity().getContent());
    }
    
    @Override
    public void run() {
        if (HttpClientTransport.LOG.isTraceEnabled()) {
            HttpClientTransport.LOG.trace("HTTP GET consumer thread starting: " + this);
        }
        final HttpClient httpClient = this.getReceiveHttpClient();
        final URI remoteUrl = this.getRemoteUrl();
        while (!this.isStopped() && !this.isStopping()) {
            this.configureMethod((AbstractHttpMessage)(this.httpMethod = new HttpGet(remoteUrl.toString())));
            HttpResponse answer = null;
            try {
                answer = httpClient.execute((HttpUriRequest)this.httpMethod);
                final int status = answer.getStatusLine().getStatusCode();
                Label_0314: {
                    if (status != 200) {
                        if (status == 408) {
                            HttpClientTransport.LOG.debug("GET timed out");
                            try {
                                Thread.sleep(1000L);
                                break Label_0314;
                            }
                            catch (InterruptedException e2) {
                                this.onException(new InterruptedIOException());
                            }
                        }
                        this.onException(new IOException("Failed to perform GET on: " + remoteUrl + " as response was: " + answer));
                    }
                    ++this.receiveCounter;
                    final DataInputStream stream = this.createDataInputStream(answer);
                    final Object command = this.getTextWireFormat().unmarshal(stream);
                    if (command == null) {
                        HttpClientTransport.LOG.debug("Received null command from url: " + remoteUrl);
                    }
                    else {
                        this.doConsume(command);
                    }
                    stream.close();
                }
            }
            catch (IOException e) {
                this.onException(IOExceptionSupport.create("Failed to perform GET on: " + remoteUrl + " Reason: " + e.getMessage(), e));
            }
            finally {
                if (answer != null) {
                    try {
                        EntityUtils.consume(answer.getEntity());
                    }
                    catch (IOException ex) {}
                }
            }
        }
    }
    
    public HttpClient getSendHttpClient() {
        if (this.sendHttpClient == null) {
            this.sendHttpClient = this.createHttpClient();
        }
        return this.sendHttpClient;
    }
    
    public void setSendHttpClient(final HttpClient sendHttpClient) {
        this.sendHttpClient = sendHttpClient;
    }
    
    public HttpClient getReceiveHttpClient() {
        if (this.receiveHttpClient == null) {
            this.receiveHttpClient = this.createHttpClient();
        }
        return this.receiveHttpClient;
    }
    
    public void setReceiveHttpClient(final HttpClient receiveHttpClient) {
        this.receiveHttpClient = receiveHttpClient;
    }
    
    @Override
    protected void doStart() throws Exception {
        if (HttpClientTransport.LOG.isTraceEnabled()) {
            HttpClientTransport.LOG.trace("HTTP GET consumer thread starting: " + this);
        }
        final HttpClient httpClient = this.getReceiveHttpClient();
        final URI remoteUrl = this.getRemoteUrl();
        final HttpHead httpMethod = new HttpHead(remoteUrl.toString());
        this.configureMethod((AbstractHttpMessage)httpMethod);
        final HttpOptions optionsMethod = new HttpOptions(remoteUrl.toString());
        final ResponseHandler<String> handler = (ResponseHandler<String>)new BasicResponseHandler() {
            public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
                for (final Header header : response.getAllHeaders()) {
                    if (header.getName().equals("Accepts-Encoding") && header.getValue().contains("gzip")) {
                        HttpClientTransport.LOG.info("Broker Servlet supports GZip compression.");
                        HttpClientTransport.this.canSendCompressed = true;
                        break;
                    }
                }
                return super.handleResponse(response);
            }
        };
        try {
            httpClient.execute((HttpUriRequest)httpMethod, (ResponseHandler)new BasicResponseHandler());
            httpClient.execute((HttpUriRequest)optionsMethod, (ResponseHandler)handler);
        }
        catch (Exception e) {
            throw new IOException("Failed to perform GET on: " + remoteUrl + " as response was: " + e.getMessage());
        }
        super.doStart();
    }
    
    @Override
    protected void doStop(final ServiceStopper stopper) throws Exception {
        if (this.httpMethod != null) {
            for (int i = 0; i < 3; ++i) {
                final Thread abortThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpClientTransport.this.httpMethod.abort();
                        }
                        catch (Exception ex) {}
                    }
                });
                abortThread.start();
                abortThread.join(2000L);
                if (abortThread.isAlive() && !this.httpMethod.isAborted()) {
                    abortThread.interrupt();
                }
            }
        }
    }
    
    protected HttpClient createHttpClient() {
        final DefaultHttpClient client = new DefaultHttpClient(this.createClientConnectionManager());
        if (this.useCompression) {
            client.addRequestInterceptor((HttpRequestInterceptor)new HttpRequestInterceptor() {
                public void process(final HttpRequest request, final HttpContext context) {
                    request.addHeader("Accept-Encoding", "gzip");
                }
            });
        }
        if (this.getProxyHost() != null) {
            final HttpHost proxy = new HttpHost(this.getProxyHost(), this.getProxyPort());
            client.getParams().setParameter("http.route.default-proxy", (Object)proxy);
            if (this.getProxyUser() != null && this.getProxyPassword() != null) {
                client.getCredentialsProvider().setCredentials(new AuthScope(this.getProxyHost(), this.getProxyPort()), (Credentials)new UsernamePasswordCredentials(this.getProxyUser(), this.getProxyPassword()));
            }
        }
        return (HttpClient)client;
    }
    
    protected ClientConnectionManager createClientConnectionManager() {
        return (ClientConnectionManager)new PoolingClientConnectionManager();
    }
    
    protected void configureMethod(final AbstractHttpMessage method) {
        method.setHeader("clientID", this.clientID);
    }
    
    public boolean isTrace() {
        return this.trace;
    }
    
    public void setTrace(final boolean trace) {
        this.trace = trace;
    }
    
    @Override
    public int getReceiveCounter() {
        return this.receiveCounter;
    }
    
    public int getSoTimeout() {
        return this.soTimeout;
    }
    
    public void setSoTimeout(final int soTimeout) {
        this.soTimeout = soTimeout;
    }
    
    public void setUseCompression(final boolean useCompression) {
        this.useCompression = useCompression;
    }
    
    public boolean isUseCompression() {
        return this.useCompression;
    }
    
    public int getMinSendAsCompressedSize() {
        return this.minSendAsCompressedSize;
    }
    
    public void setMinSendAsCompressedSize(final int minSendAsCompressedSize) {
        this.minSendAsCompressedSize = minSendAsCompressedSize;
    }
    
    static {
        LOG = LoggerFactory.getLogger(HttpClientTransport.class);
        CLIENT_ID_GENERATOR = new IdGenerator();
    }
}
