package net.itwister.spectator.model;

import java.io.Serializable;
import java.sql.SQLException;

import android.content.BroadcastReceiver;
import bindui.InjectService;

public interface SyncModel {

	SyncObject createSyncSnapshots(int subId, String query);

	SyncObject createSyncSubscriptions();

	boolean isLastSyncSuccess(SyncObject sync);

	boolean isSyncInProgress(SyncObject sync);

	void registerObserverReceiver(BroadcastReceiver observer);

	void startSync(SyncObject sync);

	void syncSubscriptionsAndShowNotification() throws Exception;

	public static class SyncObject implements Serializable {

		private static final long serialVersionUID = 6963904204698774681L;

		public int subId;
		public String query;
		public SyncTarget target;
		public boolean reset;

		public int position;
		public int snapshotId;

		public SyncObject() {}

		public SyncObject(int snapshotId) {
			this.snapshotId = snapshotId;
		}

		public SyncObject(int subId, String query) {
			this.subId = subId;
			this.query = query;
		}

		public SyncObject(SyncObject list, int position) {
			this.subId = list.subId;
			this.query = list.query;
			this.target = list.target;
			this.reset = list.reset;
			this.position = position;
		}

		/** Вернуть ID снимка (если он не задан явно, то расчитывается на основе позиции и др параметров). */
		public int getSnapshotId() throws SQLException { // TODO Оптимизировать метод
			if (snapshotId > 0) return snapshotId;
			SnapshotModel model = InjectService.getInstance(SnapshotModel.class);
			return model.getSnapshotByPosition(subId, query, position).id;
		}
	}

	public enum SyncTarget {

		Subscriptions, Snapshots, Search
	}
}