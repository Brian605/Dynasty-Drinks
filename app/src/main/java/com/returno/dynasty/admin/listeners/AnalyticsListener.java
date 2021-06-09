package com.returno.dynasty.admin.listeners;

import java.util.HashMap;

public interface AnalyticsListener {

    void onError(String message);

    void onAnalytics(HashMap<String, Integer> usersMap, HashMap<String, Integer> categoryMap, HashMap<String, Integer> dayCategoryMap, HashMap<String, Integer> drinksMap);
}
