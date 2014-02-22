package net.itwister.spectator.model;

import java.sql.SQLException;

import net.itwister.spectator.data.Snapshot;
import net.itwister.spectator.data.WebContent;
import net.itwister.spectator.data.generic.SnapshotWrapper;
import net.itwister.spectator.model.SyncModel.SyncObject;
import android.database.Cursor;
import bindui.Task;

public interface SnapshotModel {

	public static final int STASH_SUBSCRIPTION_ID = -1;

	/**
	 * Запрос списка снимков.
	 * @see #getTask(int, String)
	 */
	@Deprecated
	Cursor get(int listId, String query) throws SQLException;

    /**
     * @see #getCountTask(net.itwister.spectator.model.SyncModel.SyncObject)
     */
    @Deprecated
	int getCount(SyncObject syncObject) throws SQLException;

    Task<Integer> getCountTask(SyncObject syncObject);

	Snapshot getSnapshotByPosition(int subscriptionId, String query, int orderId) throws SQLException;

	Task<SnapshotWrapper> getSnapshotTask(SyncObject syncObject);

	Task<Cursor> getTask(int listId, String query);

	/**
	 * Запросить с вэба список снимков и сохранить их в базу.
	 * 
	 * @see {{@link #sync(boolean, int, String, int, int)}
	 * 
	 * @param reset Очистить базу перед записью.
	 * @param subId ID подписки.
	 * @param query Поисковая строка.
	 */
	void sync(boolean reset, int subId, String query) throws Exception;

	/**
	 * Запросить с вэба список снимков и сохранить их в базу под новым ID.
	 * 
	 * @see {{@link #sync(boolean, int, String)}
	 * 
	 * @param reset Очистить базу перед записью.
	 * @param subId ID подписки.
	 * @param query Поисковая строка.
	 * @param databaseSubId ID подписки при сохранение в базу.
	 * @param pageSize TODO
	 */
	void sync(boolean reset, int subId, String query, int databaseSubId, int pageSize) throws Exception;

	Task<Void> syncTask(SyncObject syncObject);

    Task<WebContent> getContent(int snapshotId, int type);
}