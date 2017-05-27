package me.speeddeveloper.beupto.parser;

import android.net.Uri;
import android.nfc.FormatException;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.speeddeveloper.beupto.model.Entry;
import me.speeddeveloper.beupto.model.Feed;
import me.speeddeveloper.beupto.model.Media;

/**
 * Created by speedDeveloper on 19.03.2016.
 */



public class Rss2Parser extends XmlParser{


    public static final String TAG = Rss2Parser.class.getSimpleName();

    // Rss 2 date format
    public static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

    @Override
    public Object parseObject(Uri sourceUri, XmlPullParser parser) throws Exception {




        Feed feed = new Feed();
        if(sourceUri != null) feed.setUri(sourceUri.toString());
        List<Entry> entryList= new ArrayList<Entry>();
        parser.nextTag();


        parser.require(XmlPullParser.START_TAG, "", "channel");


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            // Current Tag
            String namespace = parser.getNamespace() != null ? parser.getNamespace() : "";
            String tagName = namespace + parser.getName();








            switch(tagName) {

                // required
                case "title":           feed.setTitle(readTag(parser, tagName, ""));
                    break;
                case "link":
                    feed.setUri(readTag(parser, tagName, ""));
                    break;
                case "description":     feed.setDescription(readTag(parser, tagName, null));
                    break;
                case "lastBuildDate":
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
                    Date date = simpleDateFormat.parse(readTag(parser,tagName, ""));
                    feed.setLastModifiedDate(date.toString());
                    break;
                case "item":
                    Log.d(TAG, "test5");
                    entryList.add(readEntry(parser));
                    break;
                default: skip(parser);

            }




        }
        for(Entry e : entryList){
            if(e.getDate() == null)
                e.setDate(feed.getLastModifiedDate());
        }

        feed.setEntryList(entryList);

        if(feed.getTitle().trim().isEmpty())
            throw new FormatException("title must be specified when using rss format, no rss feed format");


        return feed;
    }


    private Entry readEntry(XmlPullParser parser)
            throws Exception {
        parser.require(XmlPullParser.START_TAG, "", "item");


        Entry entry = new Entry();

        while (parser.next() != XmlPullParser.END_TAG) {
            Log.d(TAG, "test2");
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String namespace = parser.getNamespace() != null ? parser.getNamespace() : "";
            String tagName = namespace + parser.getName();
            Log.d(TAG, tagName);
            switch(tagName) {


                case "title":
                    String title = readTag(parser, tagName, null);
                    if(title != null)
                        entry.setTitle(title.trim());
                    break;
                case "description":
                    String description = readTag(parser, tagName, null);
                    if(description != null)
                        entry.setDescription(description.trim());
                    break;
                case "http://purl.org/rss/1.0/modules/content/encoded":

                    entry.setContent(readTag(parser, parser.getName(), parser.getNamespace()));
                    break;
                case "link":            entry.setUri(readTag(parser, tagName, null));
                    break;
                case "image":
                    Log.d(TAG, "test");
                    String url = readImageUrl(parser, tagName);

                    entry.setThumbnailUri(url);
                    break;
                case "enclosure":

                    String[] enclosure = readEnclosure(parser);

                    if (enclosure[0] != null && enclosure[1] != null) {
                        Media.Format format = null;
                        if(enclosure[1].startsWith("image/"))
                            entry.setThumbnailUri(Uri.parse(enclosure[0]).toString());
                        else if(enclosure[1].startsWith("audio/"))
                            entry.addMedia(new Media(Uri.parse(enclosure[0]), Media.Format.AUDIO));
                        else if(enclosure[1].startsWith("video/"))
                            entry.addMedia(new Media(Uri.parse(enclosure[0]), Media.Format.VIDEO));


                        // TODO what if it's an unknown format
                    }

                    break;
                case "pubDate":
                    // <pubDate>Sun, 19 May 2002 15:21:36 GMT</pubDate>
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
                    Date date = simpleDateFormat.parse(readTag(parser, tagName,null));
                    entry.setDate(date.toString());
                    break;
                case "http://search.yahoo.com/mrss/":


                default:            skip(parser);

            }




        }

        if(entry.getTitle().trim().isEmpty())
            throw new FormatException("entry title must be specified when using rss format, no rss feed format");



        return entry;
    }


    private String readImageUrl(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "image");
        String url = null;
        d: while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            switch (name){
                case "url": String url1 = readText(parser);
                    url = url1;
                    break d;
                default:skip(parser);
            }
        }


        return url;
    }

    // first in Array returns url, second type
    String[] readEnclosure(XmlPullParser parser)
            throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, "enclosure");

        String relType = parser.getAttributeValue(null, "type");
        String link = parser.getAttributeValue(null, "url");

        while (true) {
            if (parser.nextTag() == XmlPullParser.END_TAG) break;
            // Intentionally break; consumes any remaining sub-tags.
        }
        return new String[]{link, relType};
    }


}
