package me.speeddeveloper.beupto.parser;

import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.UnknownFormatConversionException;

/**
 * Created by speedDeveloper on 19.03.2016.
 */
public abstract class XmlParser {

    public static HashMap<String, Class<? extends XmlParser>> SUPPORTED_FORMATS = new HashMap<String, Class<? extends XmlParser>>();

    public abstract Object parseObject(Uri uri, XmlPullParser parser) throws Exception;


    public static final String TAG = XmlParser.class.getSimpleName();

    public static void registerParser(String format, Class<? extends XmlParser> parser) {
        SUPPORTED_FORMATS.put(format, parser);

    }

    String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }


    String readInlineTag(XmlPullParser parser, String attrName, String conditionAttrName, String conditionValueName)
            throws IOException, XmlPullParserException {
        String link = null;
        parser.require(XmlPullParser.START_TAG, null, "enclosure");
        String tag = parser.getName();
        if (conditionAttrName != null && conditionValueName != null) {
            String relType = parser.getAttributeValue(null, conditionAttrName);
            if (relType != null && relType.startsWith(conditionValueName)) {
                link = parser.getAttributeValue(null, attrName);
            } else {
                return null;
            }

        } else {
            link = parser.getAttributeValue(null, attrName);
        }
        while (true) {
            if (parser.nextTag() == XmlPullParser.END_TAG) break;
            // Intentionally break; consumes any remaining sub-tags.
        }
        return link;
    }


    String readTag(XmlPullParser parser, String tag, String namespace)
            throws IOException, XmlPullParserException {


        parser.require(XmlPullParser.START_TAG, namespace, tag);

        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespace, tag);

        return result;
    }


    public static Object parse(Uri uri, InputStream stream) throws XmlPullParserException, IOException, ParseException, IllegalAccessException, InstantiationException, Exception {
        XmlPullParser p = makeParser(stream);
        Class e = findType(p);

        XmlParser parser = (XmlParser) e.newInstance();

        Object o = parser.parseObject(uri, p);

        return o;
    }


    public static XmlPullParser makeParser(InputStream stream) throws XmlPullParserException, IOException, ParseException {
        XmlPullParser parser = null;

        parser = Xml.newPullParser();

        parser.setInput(stream, null);


        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        return parser;


    }


    void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }

        }

    }


    public static Class findType(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {


        parser.nextTag();

        String name = parser.getName();


        for (HashMap.Entry<String, Class<? extends XmlParser>> format : SUPPORTED_FORMATS.entrySet()) {
            if (format.getKey().equalsIgnoreCase(name)) {
                return format.getValue();


            }
        }
        throw new UnknownFormatConversionException(name + " format is unknown. No parsing method found.");


    }


}
