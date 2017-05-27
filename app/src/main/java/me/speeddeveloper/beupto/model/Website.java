package me.speeddeveloper.beupto.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phili on 8/1/2016.
 */
public class Website extends Info {
    public static final Parcelable.Creator<Website> CREATOR
            = new Parcelable.Creator<Website>() {
        public Website createFromParcel(Parcel in) {
            return new Website(in);
        }

        public Website[] newArray(int size) {
            return new Website[size];
        }
    };

    private Document document;

    public Website(Parcel in){
        super(in);
    }
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
    }
    public int describeContents() {
        return 0;
    }

    public Website(Document document) {
        this.setTitle(document.title());
        this.setUri(document.location());
        this.setDocument(document);
        String description = Helper.getDescriptionFromMetaTag(document);
        if (description == null || description.trim().isEmpty()) {
            this.setDescription(document.body().text().substring(0, 200));
        }
        this.setDescription(document.text().substring(0, 200));
    }
    public Website(String title, String description, String uri, String thumbnailUri) {
        super(title, description, uri, thumbnailUri);
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    private static class Helper {

        static class Size {
            int width;
            int height;


            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public Size(int width, int height) {
                this.width = width;
                this.height = height;
            }



        }


        private static String getDescriptionFromMetaTag(Document document) {
            Elements metaTagElements = document.getElementsByTag("meta");
            String description = null;
            for (Element e : metaTagElements) {
                String attr = e.attr("name");
                if (attr.trim().isEmpty() == false && attr.equalsIgnoreCase("description")) {
                    description = e.attr("content");
                }
            }
            return description;
        }

        private static Size getSizeFromSizesAttributeValue(String value) {
            //    Log.d(TAG, value);
            if (value.trim().isEmpty() != true) {
                if (value.indexOf("x") != -1) {
                    String[] size = value.split("x");
                    int height = Integer.parseInt(size[0]);
                    int width = Integer.parseInt(size[1]);
                    return new Size(width, height);
                }
            }
            return null;
        }


        public static Uri getFaviconUri(Document document) {
            Uri uri = null;
            try {
                Elements elements = getElementListWithRelIconOrAppleTouchIconFromDocument(document);
                Uri url = getUrlOfIconWithBiggestSizeFromElementList(elements);
                if (url != null)
                    uri = Uri.parse(url.toString());
                else {
                    url = Uri.parse(document.location());
                    uri = Uri.parse("http://" + url.getHost() + "/favicon");
                }

                //  Log.d(TAG, "URI: " + uri.toString());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            } finally {
                return uri;
            }
        }


        private static Elements getElementListWithRelIconOrAppleTouchIconFromDocument(Document document) {

            Elements icons = document.getElementsByAttributeValueMatching("rel", "icon");
            Elements appleIcons = document.getElementsByAttributeValueMatching("rel", "apple\\-touch\\-icon");
            Elements allIcons = new Elements();
            allIcons.addAll(icons);
            allIcons.addAll(appleIcons);
            //   Log.d(TAG, "Size icons: " + icons.size());
            return icons;

        }

        private static Uri getUrlOfIconWithBiggestSizeFromElementList(Elements elementList) throws Exception {


            Uri urlOfBiggestImage = null;
            int biggestImageSizeSum = 0;
            for (Element e : elementList) {

                String attrSize = e.attr("sizes").trim();
                String href = e.attr("href").trim();

                if (attrSize != null && attrSize.isEmpty() == false && href != null && href.isEmpty() == false) {
                    //    Log.d(TAG, attrSize);
                    Size size = getSizeFromSizesAttributeValue(attrSize);
                    /*MyUtility.debug(TAG, "element", elementList.indexOf(e));
                    MyUtility.debug(TAG, "element height", size.getHeight());
                    MyUtility.debug(TAG, "element width", size.getWidth());*/
                    int sum = size.getHeight() * size.getWidth();
                    if (sum > biggestImageSizeSum) {
                        biggestImageSizeSum = sum;
                        // urlOfBiggestImage = MyUtility.createUri(href);
                    }
                } else if (biggestImageSizeSum == 0 && href != null && href.isEmpty() == false) {
                    // urlOfBiggestImage = MyUtility.createUri(href);
                }

            }

            return urlOfBiggestImage;
        }
    }

    public List<Uri> getFeedUris() {
        List<Uri> links = new ArrayList<Uri>();


        Elements rssLinks = this.getDocument().getElementsByAttributeValueMatching("type", "application\\/rss\\+xml");
        Elements atomLinks = this.getDocument().getElementsByAttributeValueMatching("type", "application\\/atom\\+xml");
        Elements allLinks = new Elements();
        allLinks.addAll(rssLinks);
        allLinks.addAll(atomLinks);

        Log.d(TAG, "allLinksSize: " + allLinks.size());

        for (Element linkTag : allLinks) {
            String link = linkTag.attr("href");
            if (link.trim().isEmpty() != true) {
                links.add(Uri.parse(link));
            }
        }


        return links;

    }

}
