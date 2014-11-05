package com.guruhb.travelplanner;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by guruhb@gmail.com on 04-11-2014.
 */
public class SearchLocationAdapter extends BaseAdapter implements Filterable {

    final private Context mContext;
    private List<SuggestLocation> mSuggestedLocations = new ArrayList<SuggestLocation>();

    public SearchLocationAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mSuggestedLocations.size();
    }

    @Override
    public SuggestLocation getItem(int position) {
        return mSuggestedLocations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.searchitem, parent, false);
        }
        TextView textView = (TextView)convertView.findViewById(R.id.searchListItemText);
        textView.setText(getItem(position).getFullName());
        Log.v("SearchLocationAdapter", "getView position : " + position + " text : " + getItem(position).getFullName());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
               FilterResults results = new FilterResults();
                       if(constraint != null) {

                               mSuggestedLocations = getLocationsFromServer(constraint.toString());
                           results.values = mSuggestedLocations;
                           results.count = mSuggestedLocations.size();
                       }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count > 0) {
                    notifyDataSetChanged();
                }else {
                    notifyDataSetInvalidated();
                }
            }

        };
        return filter;
    }

    private List<SuggestLocation> getLocationsFromServer(String s)  {
        Log.v("SearchLocationAdapter", "getLocationsFromSever : " + s);



        try {
            //e.g : http://api.goeuro.com/api/v2/position/suggest/de/hamburg
            final String getLocationUrl = "http://api.goeuro.com/api/v2/position/suggest/";
            String systemLocale = Locale.getDefault().getLanguage();
            String jsonRequestUrl = getLocationUrl + systemLocale + "/" + s + "/";
            Log.v("SearchLocationAdapter", "System Locale : " + systemLocale);

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(jsonRequestUrl);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String respData = httpClient.execute(httpGet, responseHandler);
            List<SuggestLocation> slist;
            slist = parseLocationData(respData);
            if(slist != null)
                return slist;
            Log.v("SearchLocationAdapter", "JSON data : " + respData);
        } catch (Exception e) {
            Log.w("SearchLocationAdapter", "exception ");
        }
        return null;
    }

    private List<SuggestLocation> parseLocationData(String respData) {
       try {
           JSONArray respArray = new JSONArray(respData);
           JSONObject respObj = null;
           Log.v("parseLocationData", "JSONArray respArray.length() : " + respArray.length());
           if(respArray.length() > 0) {
               List<SuggestLocation> slist = new ArrayList<SuggestLocation>();

               for (int i = 0; i < respArray.length(); i++) {
                   String name = ((JSONObject) respArray.get(i)).getString("name");
                   String fullName = ((JSONObject) respArray.get(i)).getString("fullName");
                   String type = ((JSONObject) respArray.get(i)).getString("type");
                   String countryCode = ((JSONObject) respArray.get(i)).getString("countryCode");
                   long distance = 0;
                   try {
                       distance = ((JSONObject) respArray.get(i)).getLong("distance");
                   }catch (JSONException e) {
                       e.printStackTrace();
                       Log.v("parseLocationData", "Get Distance failed" + name);
                   }
                   JSONObject geoObj = ((JSONObject) respArray.get(i)).getJSONObject("geo_position");
                   double lat = 0.0;//FIXME : define default value
                   double lng = 0.0;//FIXME : define default value
                   if(geoObj != null) {
                       lat = geoObj.getDouble("latitude");
                       lng = geoObj.getDouble("longitude");
                   }
                    //String name, String fullName, String type, double lat, double lng, String countryCode, long distance
                   SuggestLocation sl = new SuggestLocation(name, fullName, type, lat, lng, countryCode, distance);
                   if(!slist.add(sl)) {
                       Log.v("parseLocationData", "list append failed" + name);
                   }
                   Log.v("parseLocationData", "Name  : " + name);
               }
               return slist;
           }else {
               Log.v("parseLocationData", "JSONArray failed : " + respArray.toString());
           }
       }catch(JSONException e) {
           Log.v("parseLocationData", "jsonObject : ");
           e.printStackTrace();
       }
        return null;
    }
}
