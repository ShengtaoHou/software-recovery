// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.spring;

import org.springframework.core.io.ClassPathResource;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import org.springframework.core.io.UrlResource;
import org.springframework.util.ResourceUtils;
import org.springframework.core.io.FileSystemResource;
import java.io.File;
import org.springframework.core.io.Resource;

public class Utils
{
    public static Resource resourceFromString(final String uri) throws MalformedURLException {
        final File file = new File(uri);
        Resource resource;
        if (file.exists()) {
            resource = (Resource)new FileSystemResource(uri);
        }
        else {
            if (ResourceUtils.isUrl(uri)) {
                try {
                    resource = (Resource)new UrlResource(ResourceUtils.getURL(uri));
                    return resource;
                }
                catch (FileNotFoundException e) {
                    final MalformedURLException malformedURLException = new MalformedURLException(uri);
                    malformedURLException.initCause(e);
                    throw malformedURLException;
                }
            }
            resource = (Resource)new ClassPathResource(uri);
        }
        return resource;
    }
}
