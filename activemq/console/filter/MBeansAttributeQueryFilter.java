// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.filter;

import javax.management.MBeanAttributeInfo;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.IntrospectionException;
import javax.management.ReflectionException;
import java.io.IOException;
import java.util.Iterator;
import javax.management.ObjectName;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectInstance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.management.MBeanServerConnection;

public class MBeansAttributeQueryFilter extends AbstractQueryFilter
{
    public static final String KEY_OBJECT_NAME_ATTRIBUTE = "Attribute:ObjectName:";
    private MBeanServerConnection jmxConnection;
    private Set attribView;
    
    public MBeansAttributeQueryFilter(final MBeanServerConnection jmxConnection, final Set attribView, final MBeansObjectNameQueryFilter next) {
        super(next);
        this.jmxConnection = jmxConnection;
        this.attribView = attribView;
    }
    
    @Override
    public List query(final List queries) throws Exception {
        return this.getMBeanAttributesCollection(this.next.query(queries));
    }
    
    protected List getMBeanAttributesCollection(final Collection result) throws IOException, ReflectionException, InstanceNotFoundException, NoSuchMethodException, IntrospectionException {
        final List mbeansCollection = new ArrayList();
        for (final Object mbean : result) {
            if (mbean instanceof ObjectInstance) {
                try {
                    mbeansCollection.add(this.getMBeanAttributes(((ObjectInstance)mbean).getObjectName(), this.attribView));
                }
                catch (InstanceNotFoundException ex) {}
            }
            else {
                if (!(mbean instanceof ObjectName)) {
                    throw new NoSuchMethodException("Cannot get the mbean attributes for class: " + mbean.getClass().getName());
                }
                try {
                    mbeansCollection.add(this.getMBeanAttributes((ObjectName)mbean, this.attribView));
                }
                catch (InstanceNotFoundException ex2) {}
            }
        }
        return mbeansCollection;
    }
    
    protected AttributeList getMBeanAttributes(final ObjectInstance obj, final Set attrView) throws ReflectionException, InstanceNotFoundException, IOException, IntrospectionException {
        return this.getMBeanAttributes(obj.getObjectName(), attrView);
    }
    
    protected AttributeList getMBeanAttributes(final ObjectName objName, final Set attrView) throws IOException, ReflectionException, InstanceNotFoundException, IntrospectionException {
        String[] attribs;
        if (attrView == null || attrView.isEmpty()) {
            final MBeanAttributeInfo[] infos = this.jmxConnection.getMBeanInfo(objName).getAttributes();
            attribs = new String[infos.length];
            for (int i = 0; i < infos.length; ++i) {
                if (infos[i].isReadable()) {
                    attribs[i] = infos[i].getName();
                }
            }
        }
        else {
            attribs = new String[attrView.size()];
            int count = 0;
            final Iterator j = attrView.iterator();
            while (j.hasNext()) {
                attribs[count++] = j.next();
            }
        }
        final AttributeList attribList = this.jmxConnection.getAttributes(objName, attribs);
        attribList.add(0, new Attribute("Attribute:ObjectName:", objName));
        return attribList;
    }
}
