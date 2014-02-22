package net.itwister.spectator.model;

import net.itwister.spectator.model.helpers.SpectatorTask;
import android.database.Cursor;
import android.graphics.Bitmap;

import bindui.Task;

public interface WidgetModel {

	/** Удалить информацию о всех виджетах и обновить их (нужно при выходе из аккаунта). */
	Task<Void> deleteAllWidgetTask();

	/**
	 * [АСИНХРОННО] Удалить настройки виджетов, их списки снимков и неиспользуемые миниаютры.
	 * @param widgetIds ID виджетов которые нужно удалить.
	 * @see #deleteWidgetAndCleanupTask(int[])
	 */
	@Deprecated
    Task<Void> deleteWidgetAndCleanup(int[] widgetIds);

	/**
	 * [ТАСКА] Удалить настройки виджетов, их списки снимков и неиспользуемые миниаютры.
	 * @param widgetIds ID виджетов которые нужно удалить.
	 */
    Task<Void> deleteWidgetAndCleanupTask(int[] widgetIds);

	int getCountOfSnapshots(int widgetId) throws Exception;

	int getDatabaseSubscriptionId(int widgetId);

	/**
	 * Список снимков виджета.
	 * @param widgetId ID виджета.
	 */
	Cursor getSnapshots(int widgetId) throws Exception;

	int getSubscriptionIdForWidget(int widgetId);

	Bitmap getThumbnailForShapshot(int snapshotId);

	void setSubsciptionForWidget(int widgetId, int subscriptionId);

	/**
	 * Синхронизировать снимки для виджета и его картинки.
	 * @param widgetId ID виджета.
	 */
	void sync(int widgetId) throws Exception;
}