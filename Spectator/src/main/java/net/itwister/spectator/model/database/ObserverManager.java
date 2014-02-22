package net.itwister.spectator.model.database;

import net.itwister.spectator.App;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class ObserverManager {

	public static final Uri SUBSCRIPTIONS = Uri.parse("content://suscriptions");
	public static final Uri SNAPSHOT_LIST = Uri.parse("content://snapshots");
	public static final Uri TAGS = Uri.parse("content://tags");
	public static final Uri SNAPSHOT = Uri.parse("content://snapshot");

	private final ContentResolver contentResolver = App.getInstance().getContentResolver();

	public void notifySnapshot() {
		contentResolver.notifyChange(SNAPSHOT, null);
	}

	public void notifySnapshotList() {
		contentResolver.notifyChange(SNAPSHOT_LIST, null);
	}

	public void notifySubscriptions() {
		contentResolver.notifyChange(SUBSCRIPTIONS, null);
	}

	public Cursor registerSnapshotList(Cursor cursor) {
		cursor.setNotificationUri(contentResolver, SNAPSHOT_LIST);
		return cursor;
	}

	public void sendNotification(Uri url) {
		contentResolver.notifyChange(url, null);
	}
}