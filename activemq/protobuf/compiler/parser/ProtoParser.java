// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler.parser;

import java.util.Enumeration;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import org.apache.activemq.protobuf.compiler.ParserSupport;
import org.apache.activemq.protobuf.compiler.EnumFieldDescriptor;
import org.apache.activemq.protobuf.compiler.MethodDescriptor;
import org.apache.activemq.protobuf.compiler.ExtensionsDescriptor;
import org.apache.activemq.protobuf.compiler.FieldDescriptor;
import java.util.Map;
import java.util.List;
import org.apache.activemq.protobuf.compiler.ServiceDescriptor;
import java.util.ArrayList;
import org.apache.activemq.protobuf.compiler.EnumDescriptor;
import org.apache.activemq.protobuf.compiler.MessageDescriptor;
import org.apache.activemq.protobuf.compiler.OptionDescriptor;
import java.util.LinkedHashMap;
import org.apache.activemq.protobuf.compiler.ProtoDescriptor;
import java.util.Vector;

public class ProtoParser implements ProtoParserConstants
{
    public ProtoParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    public boolean lookingAhead;
    private boolean jj_semLA;
    private int jj_gen;
    private final int[] jj_la1;
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private final JJCalls[] jj_2_rtns;
    private boolean jj_rescan;
    private int jj_gc;
    private final LookaheadSuccess jj_ls;
    private Vector jj_expentries;
    private int[] jj_expentry;
    private int jj_kind;
    private int[] jj_lasttokens;
    private int jj_endpos;
    
    public final ProtoDescriptor ProtoDescriptor() throws ParseException {
        final ProtoDescriptor proto = new ProtoDescriptor();
        String packageName = null;
        final LinkedHashMap<String, OptionDescriptor> opts = new LinkedHashMap<String, OptionDescriptor>();
        final LinkedHashMap<String, MessageDescriptor> messages = new LinkedHashMap<String, MessageDescriptor>();
        final LinkedHashMap<String, EnumDescriptor> enums = new LinkedHashMap<String, EnumDescriptor>();
        final ArrayList<MessageDescriptor> extendsList = new ArrayList<MessageDescriptor>();
        final LinkedHashMap<String, ServiceDescriptor> services = new LinkedHashMap<String, ServiceDescriptor>();
        final ArrayList<String> imports = new ArrayList<String>();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 9: {
                    this.jj_consume_token(9);
                    packageName = this.PackageID();
                    this.jj_consume_token(27);
                    break;
                }
                case 12: {
                    this.jj_consume_token(12);
                    final OptionDescriptor optionD = this.OptionDescriptor();
                    this.jj_consume_token(27);
                    opts.put(optionD.getName(), optionD);
                    break;
                }
                case 8: {
                    this.jj_consume_token(8);
                    final String o = this.StringLitteral();
                    this.jj_consume_token(27);
                    imports.add(o);
                    break;
                }
                case 13: {
                    final MessageDescriptor messageD = this.MessageDescriptor(proto, null);
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 27: {
                            this.jj_consume_token(27);
                            break;
                        }
                        default: {
                            this.jj_la1[0] = this.jj_gen;
                            break;
                        }
                    }
                    messages.put(messageD.getName(), messageD);
                    break;
                }
                case 16: {
                    final EnumDescriptor enumD = this.EnumDescriptor(proto, null);
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 27: {
                            this.jj_consume_token(27);
                            break;
                        }
                        default: {
                            this.jj_la1[1] = this.jj_gen;
                            break;
                        }
                    }
                    enums.put(enumD.getName(), enumD);
                    break;
                }
                case 10: {
                    final ServiceDescriptor serviceD = this.ServiceDescriptor(proto);
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 27: {
                            this.jj_consume_token(27);
                            break;
                        }
                        default: {
                            this.jj_la1[2] = this.jj_gen;
                            break;
                        }
                    }
                    services.put(serviceD.getName(), serviceD);
                    break;
                }
                case 15: {
                    final MessageDescriptor extendD = this.ExtendDescriptor(proto, null);
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 27: {
                            this.jj_consume_token(27);
                            break;
                        }
                        default: {
                            this.jj_la1[3] = this.jj_gen;
                            break;
                        }
                    }
                    extendsList.add(extendD);
                    break;
                }
                default: {
                    this.jj_la1[4] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 8:
                case 9:
                case 10:
                case 12:
                case 13:
                case 15:
                case 16: {
                    continue;
                }
                default: {
                    this.jj_la1[5] = this.jj_gen;
                    this.jj_consume_token(0);
                    proto.setPackageName(packageName);
                    proto.setImports(imports);
                    proto.setOptions(opts);
                    proto.setMessages(messages);
                    proto.setEnums(enums);
                    proto.setServices(services);
                    proto.setExtends(extendsList);
                    return proto;
                }
            }
        }
    }
    
    public final MessageDescriptor MessageDescriptor(final ProtoDescriptor proto, final MessageDescriptor parent) throws ParseException {
        final LinkedHashMap<String, FieldDescriptor> fields = new LinkedHashMap<String, FieldDescriptor>();
        final LinkedHashMap<String, MessageDescriptor> messages = new LinkedHashMap<String, MessageDescriptor>();
        final LinkedHashMap<String, EnumDescriptor> enums = new LinkedHashMap<String, EnumDescriptor>();
        final ArrayList<MessageDescriptor> extendsList = new ArrayList<MessageDescriptor>();
        final LinkedHashMap<String, OptionDescriptor> opts = new LinkedHashMap<String, OptionDescriptor>();
        final MessageDescriptor rc = new MessageDescriptor(proto, parent);
        ExtensionsDescriptor extensionsD = null;
        this.jj_consume_token(13);
        final String name = this.ID();
        this.jj_consume_token(24);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 18:
                case 19:
                case 20: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 12: {
                            this.jj_consume_token(12);
                            final OptionDescriptor optionD = this.OptionDescriptor();
                            this.jj_consume_token(27);
                            opts.put(optionD.getName(), optionD);
                            continue;
                        }
                        case 18:
                        case 19:
                        case 20: {
                            final FieldDescriptor fieldD = this.FieldDescriptor(rc);
                            fields.put(fieldD.getName(), fieldD);
                            continue;
                        }
                        case 13: {
                            final MessageDescriptor messageD = this.MessageDescriptor(proto, rc);
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 27: {
                                    this.jj_consume_token(27);
                                    break;
                                }
                                default: {
                                    this.jj_la1[7] = this.jj_gen;
                                    break;
                                }
                            }
                            messages.put(messageD.getName(), messageD);
                            continue;
                        }
                        case 16: {
                            final EnumDescriptor enumD = this.EnumDescriptor(proto, rc);
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 27: {
                                    this.jj_consume_token(27);
                                    break;
                                }
                                default: {
                                    this.jj_la1[8] = this.jj_gen;
                                    break;
                                }
                            }
                            enums.put(enumD.getName(), enumD);
                            continue;
                        }
                        case 14: {
                            extensionsD = this.ExtensionsDescriptor(rc);
                            this.jj_consume_token(27);
                            continue;
                        }
                        case 15: {
                            final MessageDescriptor extendD = this.ExtendDescriptor(proto, rc);
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 27: {
                                    this.jj_consume_token(27);
                                    break;
                                }
                                default: {
                                    this.jj_la1[9] = this.jj_gen;
                                    break;
                                }
                            }
                            extendsList.add(extendD);
                            continue;
                        }
                        default: {
                            this.jj_la1[10] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    break;
                }
                default: {
                    this.jj_la1[6] = this.jj_gen;
                    this.jj_consume_token(25);
                    rc.setName(name);
                    rc.setFields(fields);
                    rc.setMessages(messages);
                    rc.setEnums(enums);
                    rc.setExtensions(extensionsD);
                    rc.setOptions(opts);
                    return rc;
                }
            }
        }
    }
    
    public final FieldDescriptor FieldDescriptor(final MessageDescriptor parent) throws ParseException {
        final LinkedHashMap<String, OptionDescriptor> opts = new LinkedHashMap<String, OptionDescriptor>();
        final LinkedHashMap<String, FieldDescriptor> fields = new LinkedHashMap<String, FieldDescriptor>();
        final FieldDescriptor rc = new FieldDescriptor(parent);
        final MessageDescriptor group = new MessageDescriptor(parent.getProtoDescriptor(), parent);
        final String rule = this.Rule();
        String type = null;
        String name = null;
        int tag = 0;
        Label_0533: {
            if (this.jj_2_1(5)) {
                type = this.PackageID();
                name = this.ID();
                this.jj_consume_token(26);
                tag = this.Integer();
                Label_0247: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 28: {
                            this.jj_consume_token(28);
                            OptionDescriptor optionD = this.OptionDescriptor();
                            opts.put(optionD.getName(), optionD);
                            while (true) {
                                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                    case 33: {
                                        this.jj_consume_token(33);
                                        optionD = this.OptionDescriptor();
                                        opts.put(optionD.getName(), optionD);
                                        continue;
                                    }
                                    default: {
                                        this.jj_la1[11] = this.jj_gen;
                                        this.jj_consume_token(29);
                                        break Label_0247;
                                    }
                                }
                            }
                            break;
                        }
                        default: {
                            this.jj_la1[12] = this.jj_gen;
                            break;
                        }
                    }
                }
                this.jj_consume_token(27);
            }
            else {
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 17: {
                        this.jj_consume_token(17);
                        name = this.ID();
                        this.jj_consume_token(26);
                        tag = this.Integer();
                        this.jj_consume_token(24);
                        while (true) {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 18:
                                case 19:
                                case 20: {
                                    final FieldDescriptor fieldD = this.FieldDescriptor(group);
                                    fields.put(fieldD.getName(), fieldD);
                                    continue;
                                }
                                default: {
                                    this.jj_la1[13] = this.jj_gen;
                                    this.jj_consume_token(25);
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                        case 27: {
                                            this.jj_consume_token(27);
                                            break;
                                        }
                                        default: {
                                            this.jj_la1[14] = this.jj_gen;
                                            break;
                                        }
                                    }
                                    type = name;
                                    group.setName(name);
                                    group.setFields(fields);
                                    rc.setGroup(group);
                                    break Label_0533;
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        this.jj_la1[15] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
            }
        }
        rc.setName(name);
        rc.setType(type);
        rc.setRule(rule);
        rc.setTag(tag);
        rc.setOptions(opts);
        return rc;
    }
    
    public final ServiceDescriptor ServiceDescriptor(final ProtoDescriptor proto) throws ParseException {
        final ArrayList<MethodDescriptor> methods = new ArrayList<MethodDescriptor>();
        this.jj_consume_token(10);
        final String name = this.ID();
        this.jj_consume_token(24);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 11: {
                    final MethodDescriptor method = this.MethodDescriptor(proto);
                    this.jj_consume_token(27);
                    methods.add(method);
                    continue;
                }
                default: {
                    this.jj_la1[16] = this.jj_gen;
                    this.jj_consume_token(25);
                    final ServiceDescriptor rc = new ServiceDescriptor(proto);
                    rc.setName(name);
                    rc.setMethods(methods);
                    return rc;
                }
            }
        }
    }
    
    public final MethodDescriptor MethodDescriptor(final ProtoDescriptor proto) throws ParseException {
        this.jj_consume_token(11);
        final String name = this.ID();
        this.jj_consume_token(30);
        final String input = this.PackageID();
        this.jj_consume_token(31);
        this.jj_consume_token(21);
        this.jj_consume_token(30);
        final String output = this.PackageID();
        this.jj_consume_token(31);
        final MethodDescriptor rc = new MethodDescriptor(proto);
        rc.setName(name);
        rc.setParameter(input);
        rc.setReturns(output);
        return rc;
    }
    
    public final OptionDescriptor OptionDescriptor() throws ParseException {
        final String name = this.ID();
        this.jj_consume_token(26);
        final String value = this.Value();
        final OptionDescriptor rc = new OptionDescriptor();
        rc.setName(name);
        rc.setValue(value);
        return rc;
    }
    
    public final MessageDescriptor ExtendDescriptor(final ProtoDescriptor proto, final MessageDescriptor parent) throws ParseException {
        final LinkedHashMap<String, FieldDescriptor> fields = new LinkedHashMap<String, FieldDescriptor>();
        final MessageDescriptor rc = new MessageDescriptor(proto, parent);
        this.jj_consume_token(15);
        final String name = this.ID();
        this.jj_consume_token(24);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 18:
                case 19:
                case 20: {
                    final FieldDescriptor fieldD = this.FieldDescriptor(rc);
                    fields.put(fieldD.getName(), fieldD);
                    continue;
                }
                default: {
                    this.jj_la1[17] = this.jj_gen;
                    this.jj_consume_token(25);
                    rc.setName(name);
                    rc.setFields(fields);
                    return rc;
                }
            }
        }
    }
    
    public final ExtensionsDescriptor ExtensionsDescriptor(final MessageDescriptor parent) throws ParseException {
        this.jj_consume_token(14);
        final int first = this.Integer();
        this.jj_consume_token(22);
        int last = 0;
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 34: {
                last = this.Integer();
                break;
            }
            case 23: {
                this.jj_consume_token(23);
                last = 536870911;
                break;
            }
            default: {
                this.jj_la1[18] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        final ExtensionsDescriptor rc = new ExtensionsDescriptor(parent);
        rc.setFirst(first);
        rc.setLast(last);
        return rc;
    }
    
    public final EnumDescriptor EnumDescriptor(final ProtoDescriptor proto, final MessageDescriptor parent) throws ParseException {
        final LinkedHashMap<String, EnumFieldDescriptor> fields = new LinkedHashMap<String, EnumFieldDescriptor>();
        final EnumDescriptor rc = new EnumDescriptor(proto, parent);
        final LinkedHashMap<String, OptionDescriptor> opts = new LinkedHashMap<String, OptionDescriptor>();
        this.jj_consume_token(16);
        final Token name = this.jj_consume_token(41);
        this.jj_consume_token(24);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 41: {
                    if (this.jj_2_2(2)) {
                        this.jj_consume_token(12);
                        final OptionDescriptor optionD = this.OptionDescriptor();
                        this.jj_consume_token(27);
                        opts.put(optionD.getName(), optionD);
                        continue;
                    }
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                        case 41: {
                            final EnumFieldDescriptor enumD = this.EnumFieldDescriptor(rc);
                            this.jj_consume_token(27);
                            fields.put(enumD.getName(), enumD);
                            continue;
                        }
                        default: {
                            this.jj_la1[20] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    break;
                }
                default: {
                    this.jj_la1[19] = this.jj_gen;
                    this.jj_consume_token(25);
                    rc.setName(name.image);
                    rc.setFields(fields);
                    rc.setOptions(opts);
                    return rc;
                }
            }
        }
    }
    
    public final EnumFieldDescriptor EnumFieldDescriptor(final EnumDescriptor parent) throws ParseException {
        int value = 0;
        final String name = this.ID();
        this.jj_consume_token(26);
        value = this.Integer();
        final EnumFieldDescriptor rc = new EnumFieldDescriptor(parent);
        rc.setName(name);
        rc.setValue(value);
        return rc;
    }
    
    public final int Integer() throws ParseException {
        final Token t = this.jj_consume_token(34);
        return Integer.parseInt(t.image);
    }
    
    public final String Rule() throws ParseException {
        Token t = null;
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 18: {
                t = this.jj_consume_token(18);
                break;
            }
            case 19: {
                t = this.jj_consume_token(19);
                break;
            }
            case 20: {
                t = this.jj_consume_token(20);
                break;
            }
            default: {
                this.jj_la1[21] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return t.image;
    }
    
    public final String Value() throws ParseException {
        String value = null;
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 40: {
                value = this.StringLitteral();
                break;
            }
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 41: {
                value = this.ID();
                break;
            }
            case 34: {
                final Token t = this.jj_consume_token(34);
                value = t.image;
                break;
            }
            case 38: {
                final Token t = this.jj_consume_token(38);
                value = t.image;
                break;
            }
            default: {
                this.jj_la1[22] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return value;
    }
    
    public final String ID() throws ParseException {
        Token t = null;
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 41: {
                t = this.jj_consume_token(41);
                break;
            }
            case 17: {
                t = this.jj_consume_token(17);
                break;
            }
            case 8: {
                t = this.jj_consume_token(8);
                break;
            }
            case 9: {
                t = this.jj_consume_token(9);
                break;
            }
            case 10: {
                t = this.jj_consume_token(10);
                break;
            }
            case 11: {
                t = this.jj_consume_token(11);
                break;
            }
            case 12: {
                t = this.jj_consume_token(12);
                break;
            }
            case 13: {
                t = this.jj_consume_token(13);
                break;
            }
            case 14: {
                t = this.jj_consume_token(14);
                break;
            }
            case 15: {
                t = this.jj_consume_token(15);
                break;
            }
            case 16: {
                t = this.jj_consume_token(16);
                break;
            }
            case 18: {
                t = this.jj_consume_token(18);
                break;
            }
            case 19: {
                t = this.jj_consume_token(19);
                break;
            }
            case 20: {
                t = this.jj_consume_token(20);
                break;
            }
            case 21: {
                t = this.jj_consume_token(21);
                break;
            }
            case 22: {
                t = this.jj_consume_token(22);
                break;
            }
            case 23: {
                t = this.jj_consume_token(23);
                break;
            }
            default: {
                this.jj_la1[23] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return t.image;
    }
    
    public final String PackageID() throws ParseException {
        final StringBuffer sb = new StringBuffer();
        String t = this.ID();
        sb.append(t);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 32: {
                    this.jj_consume_token(32);
                    t = this.ID();
                    sb.append(".");
                    sb.append(t);
                    continue;
                }
                default: {
                    this.jj_la1[24] = this.jj_gen;
                    return sb.toString();
                }
            }
        }
    }
    
    public final String StringLitteral() throws ParseException {
        final Token t = this.jj_consume_token(40);
        return ParserSupport.decodeString(t);
    }
    
    private final boolean jj_2_1(final int xla) {
        this.jj_la = xla;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !this.jj_3_1();
        }
        catch (LookaheadSuccess ls) {
            return true;
        }
        finally {
            this.jj_save(0, xla);
        }
    }
    
    private final boolean jj_2_2(final int xla) {
        this.jj_la = xla;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !this.jj_3_2();
        }
        catch (LookaheadSuccess ls) {
            return true;
        }
        finally {
            this.jj_save(1, xla);
        }
    }
    
    private final boolean jj_3R_14() {
        return this.jj_scan_token(32) || this.jj_3R_10();
    }
    
    private final boolean jj_3_2() {
        return this.jj_scan_token(12) || this.jj_3R_13();
    }
    
    private final boolean jj_3R_10() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(41)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(17)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(8)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(9)) {
                        this.jj_scanpos = xsp;
                        if (this.jj_scan_token(10)) {
                            this.jj_scanpos = xsp;
                            if (this.jj_scan_token(11)) {
                                this.jj_scanpos = xsp;
                                if (this.jj_scan_token(12)) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_scan_token(13)) {
                                        this.jj_scanpos = xsp;
                                        if (this.jj_scan_token(14)) {
                                            this.jj_scanpos = xsp;
                                            if (this.jj_scan_token(15)) {
                                                this.jj_scanpos = xsp;
                                                if (this.jj_scan_token(16)) {
                                                    this.jj_scanpos = xsp;
                                                    if (this.jj_scan_token(18)) {
                                                        this.jj_scanpos = xsp;
                                                        if (this.jj_scan_token(19)) {
                                                            this.jj_scanpos = xsp;
                                                            if (this.jj_scan_token(20)) {
                                                                this.jj_scanpos = xsp;
                                                                if (this.jj_scan_token(21)) {
                                                                    this.jj_scanpos = xsp;
                                                                    if (this.jj_scan_token(22)) {
                                                                        this.jj_scanpos = xsp;
                                                                        if (this.jj_scan_token(23)) {
                                                                            return true;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private final boolean jj_3R_11() {
        return this.jj_scan_token(34);
    }
    
    private final boolean jj_3R_9() {
        if (this.jj_3R_10()) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_14());
        this.jj_scanpos = xsp;
        return false;
    }
    
    private final boolean jj_3R_12() {
        return this.jj_scan_token(28);
    }
    
    private final boolean jj_3_1() {
        if (this.jj_3R_9()) {
            return true;
        }
        if (this.jj_3R_10()) {
            return true;
        }
        if (this.jj_scan_token(26)) {
            return true;
        }
        if (this.jj_3R_11()) {
            return true;
        }
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_12()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(27);
    }
    
    private final boolean jj_3R_13() {
        return this.jj_3R_10();
    }
    
    private static void jj_la1_0() {
        ProtoParser.jj_la1_0 = new int[] { 134217728, 134217728, 134217728, 134217728, 112384, 112384, 1961984, 134217728, 134217728, 134217728, 1961984, 0, 268435456, 1835008, 134217728, 131072, 2048, 1835008, 8388608, 16776960, 16776960, 1835008, 16776960, 16776960, 0 };
    }
    
    private static void jj_la1_1() {
        ProtoParser.jj_la1_1 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 4, 512, 512, 0, 836, 512, 1 };
    }
    
    public ProtoParser(final InputStream stream) {
        this(stream, null);
    }
    
    public ProtoParser(final InputStream stream, final String encoding) {
        this.lookingAhead = false;
        this.jj_la1 = new int[25];
        this.jj_2_rtns = new JJCalls[2];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new Vector();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new ProtoParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 25; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final InputStream stream) {
        this.ReInit(stream, null);
    }
    
    public void ReInit(final InputStream stream, final String encoding) {
        try {
            this.jj_input_stream.ReInit(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 25; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public ProtoParser(final Reader stream) {
        this.lookingAhead = false;
        this.jj_la1 = new int[25];
        this.jj_2_rtns = new JJCalls[2];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new Vector();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new ProtoParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 25; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final Reader stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 25; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public ProtoParser(final ProtoParserTokenManager tm) {
        this.lookingAhead = false;
        this.jj_la1 = new int[25];
        this.jj_2_rtns = new JJCalls[2];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new Vector();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 25; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final ProtoParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 25; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    private final Token jj_consume_token(final int kind) throws ParseException {
        final Token oldToken;
        if ((oldToken = this.token).next != null) {
            this.token = this.token.next;
        }
        else {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            ++this.jj_gen;
            if (++this.jj_gc > 100) {
                this.jj_gc = 0;
                for (int i = 0; i < this.jj_2_rtns.length; ++i) {
                    for (JJCalls c = this.jj_2_rtns[i]; c != null; c = c.next) {
                        if (c.gen < this.jj_gen) {
                            c.first = null;
                        }
                    }
                }
            }
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
    }
    
    private final boolean jj_scan_token(final int kind) {
        if (this.jj_scanpos == this.jj_lastpos) {
            --this.jj_la;
            if (this.jj_scanpos.next == null) {
                final Token jj_scanpos = this.jj_scanpos;
                final Token nextToken = this.token_source.getNextToken();
                jj_scanpos.next = nextToken;
                this.jj_scanpos = nextToken;
                this.jj_lastpos = nextToken;
            }
            else {
                final Token next = this.jj_scanpos.next;
                this.jj_scanpos = next;
                this.jj_lastpos = next;
            }
        }
        else {
            this.jj_scanpos = this.jj_scanpos.next;
        }
        if (this.jj_rescan) {
            int i = 0;
            Token tok;
            for (tok = this.token; tok != null && tok != this.jj_scanpos; tok = tok.next) {
                ++i;
            }
            if (tok != null) {
                this.jj_add_error_token(kind, i);
            }
        }
        if (this.jj_scanpos.kind != kind) {
            return true;
        }
        if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            throw this.jj_ls;
        }
        return false;
    }
    
    public final Token getNextToken() {
        if (this.token.next != null) {
            this.token = this.token.next;
        }
        else {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        ++this.jj_gen;
        return this.token;
    }
    
    public final Token getToken(final int index) {
        Token t = this.lookingAhead ? this.jj_scanpos : this.token;
        for (int i = 0; i < index; ++i) {
            if (t.next != null) {
                t = t.next;
            }
            else {
                final Token token = t;
                final Token nextToken = this.token_source.getNextToken();
                token.next = nextToken;
                t = nextToken;
            }
        }
        return t;
    }
    
    private final int jj_ntk() {
        final Token next = this.token.next;
        this.jj_nt = next;
        if (next == null) {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            return this.jj_ntk = nextToken.kind;
        }
        return this.jj_ntk = this.jj_nt.kind;
    }
    
    private void jj_add_error_token(final int kind, final int pos) {
        if (pos >= 100) {
            return;
        }
        if (pos == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = kind;
        }
        else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];
            for (int i = 0; i < this.jj_endpos; ++i) {
                this.jj_expentry[i] = this.jj_lasttokens[i];
            }
            boolean exists = false;
            final Enumeration e = this.jj_expentries.elements();
            while (e.hasMoreElements()) {
                final int[] oldentry = e.nextElement();
                if (oldentry.length == this.jj_expentry.length) {
                    exists = true;
                    for (int j = 0; j < this.jj_expentry.length; ++j) {
                        if (oldentry[j] != this.jj_expentry[j]) {
                            exists = false;
                            break;
                        }
                    }
                    if (exists) {
                        break;
                    }
                    continue;
                }
            }
            if (!exists) {
                this.jj_expentries.addElement(this.jj_expentry);
            }
            if (pos != 0) {
                this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;
            }
        }
    }
    
    public ParseException generateParseException() {
        this.jj_expentries.removeAllElements();
        final boolean[] la1tokens = new boolean[42];
        for (int i = 0; i < 42; ++i) {
            la1tokens[i] = false;
        }
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 25; ++i) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; ++j) {
                    if ((ProtoParser.jj_la1_0[i] & 1 << j) != 0x0) {
                        la1tokens[j] = true;
                    }
                    if ((ProtoParser.jj_la1_1[i] & 1 << j) != 0x0) {
                        la1tokens[32 + j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 42; ++i) {
            if (la1tokens[i]) {
                (this.jj_expentry = new int[1])[0] = i;
                this.jj_expentries.addElement(this.jj_expentry);
            }
        }
        this.jj_endpos = 0;
        this.jj_rescan_token();
        this.jj_add_error_token(0, 0);
        final int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int k = 0; k < this.jj_expentries.size(); ++k) {
            exptokseq[k] = this.jj_expentries.elementAt(k);
        }
        return new ParseException(this.token, exptokseq, ProtoParser.tokenImage);
    }
    
    public final void enable_tracing() {
    }
    
    public final void disable_tracing() {
    }
    
    private final void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 2; ++i) {
            try {
                JJCalls p = this.jj_2_rtns[i];
                do {
                    if (p.gen > this.jj_gen) {
                        this.jj_la = p.arg;
                        final Token first = p.first;
                        this.jj_scanpos = first;
                        this.jj_lastpos = first;
                        switch (i) {
                            case 0: {
                                this.jj_3_1();
                                break;
                            }
                            case 1: {
                                this.jj_3_2();
                                break;
                            }
                        }
                    }
                    p = p.next;
                } while (p != null);
            }
            catch (LookaheadSuccess lookaheadSuccess) {}
        }
        this.jj_rescan = false;
    }
    
    private final void jj_save(final int index, final int xla) {
        JJCalls p;
        for (p = this.jj_2_rtns[index]; p.gen > this.jj_gen; p = p.next) {
            if (p.next == null) {
                final JJCalls jjCalls = p;
                final JJCalls next = new JJCalls();
                jjCalls.next = next;
                p = next;
                break;
            }
        }
        p.gen = this.jj_gen + xla - this.jj_la;
        p.first = this.token;
        p.arg = xla;
    }
    
    static {
        jj_la1_0();
        jj_la1_1();
    }
    
    private static final class LookaheadSuccess extends Error
    {
    }
    
    static final class JJCalls
    {
        int gen;
        Token first;
        int arg;
        JJCalls next;
    }
}
