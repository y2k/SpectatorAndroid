package net.itwister.spectator.model;

import net.itwister.spectator.model.SyncModel.SyncTarget;

public interface AnalyticsModel {

	/** Пользователь открыл настройки. */
	void eventOpenSettings();

	/**
	 * Открытие снимка из фрагмента FeedFragment.
	 * @param id Идентификатор снимка
	 */
	void eventOpenSnapshot(long id);

	/**
	 * Перезапрос списка который загрузился с ошибкой.
	 * @param target Тип списка (подписки или снимки)
	 * @param query Поисковый запрос
	 */
	void eventReloadErrorList(SyncTarget target, String query);

	/**
	 * Пользователь выполнил поиск.
	 * @param query Поисковый запрос
	 */
	void eventSearch(String query);

	/**
	 * Подабление снимка в "копилку".
	 * @param success Добавление прошло удачно
	 */
	void eventStashAdd(boolean success);

	/**
	 * Удаление снимка из "копилки".
	 * @param success Удаление прошло удачно
	 */
	void eventStashRemove(boolean success);

	/** Пользователь вышел из аккаунта. */
	void eventUserLogout();

	/** Пользователь перегрузил HomeActivity. */
	void eventUserRefreshHome();

	/** Пользователь нажал на кнопку отправить отзыв. */
	void eventUserWantSendFeedback();
}