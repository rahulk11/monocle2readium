package in.rahulkr.monocle2readium.generateFiles;

import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rk on 3/8/18.
 */

public class NavigationItem {
    private String title, src;
    private JSONObject jsonObj;
    private ArrayList<NavigationItem> children = new ArrayList<NavigationItem>();
    public static HashMap<String, String> srcTitleMap;

    public NavigationItem(String title, String src) {
        this.title = title;
        this.src = src;
        if (srcTitleMap == null)
            srcTitleMap = new HashMap<>();

        srcTitleMap.put(src, title);
    }

    public static void clearSrcTitleMap() {
        if (srcTitleMap != null)
            srcTitleMap.clear();
        srcTitleMap = null;
    }

    public void addChild(@NonNull NavigationItem child) {
        children.add(child);
    }

    public ArrayList<NavigationItem> getChildren() {
        return children;
    }

    public NavigationItem getChildAt(int index) {
        if (children.size() > index)
            return children.get(index);
        else
            return null;
    }

    public int childCount() {
        return children.size();
    }

    public String getTitle() {
        return title;
    }

    public String getSrc() {
        return src;
    }

    public void setJSONObj(JSONObject jsonObj) {
        this.jsonObj = jsonObj;
    }
}

