package com.example.gpsspeedmeter;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class SpeedmeterWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//        Intent intent = new Intent();
//        Bundle extras = intent.getExtras();
//        if (extras != null) {
//            double speed = extras.getDouble("speed");
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.speedmeter_widget);
//            views.setTextViewText(R.id.appwidget_text, String.valueOf(speed));


            // Instruct the widget manager to update the widget
//            appWidgetManager.updateAppWidget(appWidgetId, views);



        // Construct the RemoteViews object

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}