package com.google.firebase.quickstart.database.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by concepcion on 9/11/17.
 */

    // [START post_class]
    @IgnoreExtraProperties
    public class Locations {

        public String uid;
        public Double lat;
        public Double lon;
        public String route;
        public String author;

        public int starCount = 0;
        public Map<String, Boolean> stars = new HashMap<>();

        public Locations() {
            // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        }

        public Locations(String uid, String author, Double lat, Double lon, String route) {
            this.uid = uid;
            this.author = author;
            this.lat = lat;
            this.lon = lon;
            this.route = route;
        }

        // [START post_to_map]
        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("uid", uid);
            result.put("author", author);
            result.put("lat", lat);
            result.put("lon", lon);
            result.put("route", route);
            result.put("starCount", starCount);
            result.put("stars", stars);

            return result;
        }
        // [END post_to_map]

    }
// [END post_class]