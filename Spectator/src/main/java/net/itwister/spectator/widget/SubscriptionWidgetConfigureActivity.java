package net.itwister.spectator.widget;

import net.itwister.spectator.model.WidgetModel;
import net.itwister.spectator.view.common.base.SpectatorActivity;
import net.itwister.spectator.view.home.menu.MenuFragment;
import net.itwister.spectator.view.home.menu.MenuFragment.MenuFragmentHost;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import javax.inject.Inject;

import bindui.annotations.InjectExtra;

public class SubscriptionWidgetConfigureActivity extends SpectatorActivity implements MenuFragmentHost {

	//	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	//	EditText mAppWidgetText;

	//	private static final String PREFS_NAME = "net.itwister.spectator.SubscriptionWidget";
	//	private static final String PREF_PREFIX_KEY = "appwidget_";

	@InjectExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)
	private int appWidgetId;

	@Inject
	private WidgetModel model;

	//	View.OnClickListener mOnClickListener = new View.OnClickListener() {
	//
	//		@Override
	//		public void onClick(View v) {
	//			final Context context = SubscriptionWidgetConfigureActivity.this;
	//
	//			// When the button is clicked, store the string locally
	//			String widgetText = mAppWidgetText.getText().toString();
	//			saveTitlePref(context, mAppWidgetId, widgetText);
	//
	//			// It is the responsibility of the configuration activity to update the app widget
	//			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
	//			SubscriptionWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);
	//
	//			// Make sure we pass back the original appWidgetId
	//			Intent resultValue = new Intent();
	//			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
	//			setResult(RESULT_OK, resultValue);
	//			finish();
	//		}
	//	};

	//	public SubscriptionWidgetConfigureActivity() {
	//		super();
	//	}

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);

		// Set the result to CANCELED.  This will cause the widget host to cancel
		// out of the widget placement if the user presses the back button.
		setResult(RESULT_CANCELED);

		FragmentManager fm = getSupportFragmentManager();
		if (fm.findFragmentById(android.R.id.content) == null) {
			fm.beginTransaction().add(android.R.id.content, MenuFragment.newInstance(true)).commit();
		}

		//		mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);
		//		findViewById(R.id.add_button).setOnClickListener(mOnClickListener);
		//
		//		// Find the widget id from the intent.
		//		Intent intent = getIntent();
		//		Bundle extras = intent.getExtras();
		//		if (extras != null) {
		//			mAppWidgetId = extras.getInt(
		//					AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		//		}
		//
		//		// If this activity was started with an intent without an app widget ID, finish with an error.
		//		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
		//			finish();
		//			return;
		//		}
		//
		//		mAppWidgetText.setText(loadTitlePref(SubscriptionWidgetConfigureActivity.this, mAppWidgetId));
	}

	@Override
	public void onSubscriptionSelected(int id, String title) {
		model.setSubsciptionForWidget(appWidgetId, id);
		SubscriptionWidget.invalidate(this, appWidgetId);

		setResult(RESULT_OK, new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId));
		finish();
	}

	//	static void deleteTitlePref(Context context, int appWidgetId) {
	//		SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
	//		prefs.remove(PREF_PREFIX_KEY + appWidgetId);
	//		prefs.commit();
	//	}

	//	static void saveSubscriptionId(Context context, int appWidgetId, int subscriptionId) {
	//		SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
	//		prefs.putInt(PREF_PREFIX_KEY + appWidgetId, subscriptionId).commit();;
	//	}

	//	// Read the prefix from the SharedPreferences object for this widget.
	//	// If there is no preference saved, get the default from a resource
	//	static String loadTitlePref(Context context, int appWidgetId) {
	//		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
	//		String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
	//		if (titleValue != null) {
	//			return titleValue;
	//		} else {
	//			return context.getString(R.string.appwidget_text);
	//		}
	//	}
	//
	//	// Write the prefix to the SharedPreferences object for this widget
	//	static void saveTitlePref(Context context, int appWidgetId, String text) {
	//		SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
	//		prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
	//		prefs.commit();
	//	}
}