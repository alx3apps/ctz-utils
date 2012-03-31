package ru.concerteza.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.UnhandledException;
import org.xml.sax.InputSource;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static ru.concerteza.util.CtzConstants.UTF8_CHARSET;

/**
 * User: alexey
 * Date: 6/9/11
 */
public class CtzStringUtils {
    public static List<String> split(Splitter splitter, String str) {
        return ImmutableList.copyOf(splitter.split(str));
    }

    public String prettifyXml(String xml) {
        Preconditions.checkNotNull(xml);
        byte[] inBytes = xml.getBytes(UTF8_CHARSET);
        InputStream in = new ByteArrayInputStream(inBytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        prettifyXml(in, out);
        byte[] outBytes = out.toByteArray();
        return new String(outBytes, UTF8_CHARSET);
    }

    public void prettifyXml(InputStream xml, OutputStream out) {
        prettifyXml(xml, out, CtzConstants.UTF8);
    }

    // http://stackoverflow.com/questions/139076/how-to-pretty-print-xml-from-java/4472580#4472580
    public void prettifyXml(InputStream xml, OutputStream out, String encoding) {
        try {
            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.ENCODING, encoding);
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            Source xmlSource = new SAXSource(new InputSource(xml));
            StreamResult res = new StreamResult(out);
            serializer.transform(xmlSource, res);
        } catch (TransformerConfigurationException e) {
            throw new UnhandledException(e);
        } catch (TransformerException e) {
            throw new UnhandledException(e);
        }
    }
}
