package net.itwister.spectator.broadcast;

import java.io.Closeable;

import net.itwister.spectator.App;
import net.itwister.spectator.view.snapshots.SnapshotListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class SearchReceiver extends BroadcastReceiver implements Closeable {

	private static final String ACTION_FILTER = "SearchReceiver";
	private static final String ARG_QUERY = "query";

	private final Context context;
	private final int listId;

	public SearchReceiver(Context context, int listId) {
		this.context = context;
		this.listId = listId;

		LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver(this, new IntentFilter(ACTION_FILTER));
	}

	@Override
	public void close() {
		LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(this);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String q = intent.getStringExtra(ARG_QUERY);
		Intent i = SnapshotListActivity.createIntent(this.context, listId, q, q);
		this.context.startActivity(i);
	}

	public static Intent intent(String query) {
		return new Intent(ACTION_FILTER).putExtra(ARG_QUERY, query);
	}

	public static void send(String query) {
		LocalBroadcastManager.getInstance(App.getInstance()).sendBroadcast(intent(query));
	}
}