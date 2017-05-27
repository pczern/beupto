package me.speeddeveloper.beupto.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by speedDeveloper on 23.03.2016.
 */
public class SearchParameters {

    String search;
    List<String> isoCodes;
    int requiredSize;
    int approximatedTime;

    public SearchParameters(String search, List<String> isoCodes){
        this.search = search;
        this.isoCodes = isoCodes;
    }


    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public List<String> getIsoCodes() {
        return isoCodes;
    }

    public void setIsoCodes(ArrayList<String> isoCodes) {
        this.isoCodes = isoCodes;
    }



}
