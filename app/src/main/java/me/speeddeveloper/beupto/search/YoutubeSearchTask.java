package me.speeddeveloper.beupto.search;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.toolbox.Volley;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.Thumbnail;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import me.speeddeveloper.beupto.model.YoutubeChannel;

/**
 * Created by phili on 8/1/2016.
 */
public abstract class YoutubeSearchTask extends SearchGroup<YoutubeChannel>{

    @Override
    public void cancel() {

    }
    public YoutubeSearchTask(Context context) {
        super(context);
    }




    @Override
    public void start(SearchParameters parameters) {

        try {
            YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("BeUpTo").build();

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the {{ Google Cloud Console }} for
            // non-authenticated requests. See:
            // {{ https://cloud.google.com/console }}
            String apiKey = "AIzaSyAKK8TkSWZZLSss8uyB4cr6NQHPztj2L_k";
            search.setKey(apiKey);
            search.setQ(parameters.getSearch());
            search.setType("channel");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
     /*       search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");*/

           // search.setFields("items(id/kind,id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
            search.setMaxResults(30L);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null && searchResultList.isEmpty() == false) {
                Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
                if (!iteratorSearchResults.hasNext()) {
                    Log.i(TAG, "There aren't any more results for the query " + parameters.getSearch());
                }
                while (iteratorSearchResults.hasNext()) {

                    SearchResult result = iteratorSearchResults.next();
                    ResourceId rId = result.getId();

                    // Confirm that the result represents a video. Otherwise, the
                    // item will not contain a video ID.

                    Thumbnail thumbnail = result.getSnippet().getThumbnails().getDefault();



                    /*    System.out.println(" Video Id" + rId.getVideoId());
                        System.out.println(" Title: " + result.getSnippet().getTitle());
                        System.out.println(" Thumbnail: " + thumbnail.getUrl());
                        System.out.println("\n-------------------------------------------------------------\n");*/
                    SearchResultSnippet snippet = result.getSnippet();

                    Log.d(TAG, "t: " + snippet.getChannelId());
                    addInfo(new YoutubeChannel(snippet.getTitle(), snippet.getDescription(), thumbnail.getUrl(), snippet.getChannelId()));

                /*infos.add(new YoutubeInfo(result.getSnippet().getTitle(), result.getSnippet().getDescription(), rId.getVideoId(), Uri.parse(thumbnail.getUrl())));*/
                }


            }
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
    }
}
