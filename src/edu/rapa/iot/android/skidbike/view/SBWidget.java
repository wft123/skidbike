package edu.rapa.iot.android.skidbike.view;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;
import edu.rapa.iot.android.skidbike.R;
import edu.rapa.iot.android.skidbike.util.SBPreference;

public class SBWidget extends AppWidgetProvider {
	/*
	 * App Widget 을 위한 Provider
	 */
	private Context context;

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
	}

	// onUpdate는 위젯을 바탕화면서 설치시 한번 실행된다.
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		this.context = context;
		for(int i=0; i<appWidgetIds.length; i++){ 
            int appWidgetId = appWidgetIds[i];
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_sb);
            appWidgetUpdate(context, appWidgetIds[i], R.drawable.bin_rest);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        } 
	}

	// 클릭시 이벤트를 넘겨주기 위하여 pendingIntent를 만든다.
	static void appWidgetUpdate(Context context, int appWidgetId, int imgId) {

		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.widget_sb);
		AppWidgetManager wm = AppWidgetManager.getInstance(context);

		views.setImageViewResource(R.id.widgetBtn, imgId);
		
		Intent intentImage = new Intent(context, ImageService.class);
		intentImage.putExtra("wId", appWidgetId);
		PendingIntent pendingImage = PendingIntent.getService(context,
				appWidgetId, intentImage, 0); //
		views.setOnClickPendingIntent(R.id.widgetBtn, pendingImage);

		wm.updateAppWidget(appWidgetId, views);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
	}

}
