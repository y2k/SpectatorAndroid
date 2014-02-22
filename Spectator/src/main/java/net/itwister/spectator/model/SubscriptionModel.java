package net.itwister.spectator.model;

import java.sql.SQLException;
import java.util.List;

import net.itwister.spectator.data.Subscription;
import android.database.Cursor;
import bindui.Task;

public interface SubscriptionModel {

	void clearUnreadCountAsync(int subscriptionId);

	Task<Void> create(String title, String source, boolean isRss);

    Task<Void> editTask(int subscriptionId, String title);

    /**
     * @see #editTask(int, String)
     */
    @Deprecated
	void edit(int subscriptionId, String title) throws Exception;

	int getNumberOfNewSnapshots();

	int getNumberOfUpdatedSubscriptions();

	Subscription getSubscriptionById(int subscriptionId);

    /**
     * @see #getSubscriptionsFromDatabaseTask()
     */
    @Deprecated
	Cursor getSubscriptionsFromDatabase() throws SQLException;

    Task<Cursor> getSubscriptionsFromDatabaseTask();

	List<Subscription> getUpdatedSubscriptions() throws SQLException;

	void syncSubscriptions() throws Exception;

    Task<Void> delete(int subscriptionId);
}