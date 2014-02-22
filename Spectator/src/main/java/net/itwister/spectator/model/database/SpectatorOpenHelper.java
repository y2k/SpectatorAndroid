package net.itwister.spectator.model.database;

import javax.inject.Singleton;

import net.itwister.spectator.App;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

@Singleton
public class SpectatorOpenHelper extends OrmLiteSqliteOpenHelper {

	public SpectatorOpenHelper() {
		super(App.getInstance(), "spectator_v2.db", null, 2);
	}

	@Override
	public SQLiteDatabase getReadableDatabase() {
		return getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		database.execSQL("CREATE TABLE snapshots (has_revisions INTEGER NOT NULL DEFAULT 0, has_screenshots INTEGER NOT NULL DEFAULT 0, description VARCHAR, `created` BIGINT , `title` VARCHAR , `source` VARCHAR , `subscription_name` VARCHAR , `subscription_icon` INTEGER , `thumbnail` INTEGER , `_id` INTEGER , `has_content` SMALLINT, subscription_id INTEGER NOT NULL, PRIMARY KEY (`_id`) )");
		database.execSQL("CREATE TABLE tags (`name` VARCHAR , `_id` INTEGER , `thumbnail` INTEGER , PRIMARY KEY (`_id`) )");
		database.execSQL("CREATE TABLE tags_subscriptions (`subscription_id` INTEGER , `tag_id` INTEGER )");
		database.execSQL("CREATE TABLE subscriptions_snapshots (`query` VARCHAR , `subscriptionId` INTEGER , `snapshotId` INTEGER , `type` INTEGER )");
		database.execSQL("CREATE TABLE subscriptions (count INTEGER, `group` VARCHAR , `title` VARCHAR , `source` VARCHAR , `is_checked` SMALLINT , `thumbnail` INTEGER , `_id` INTEGER , PRIMARY KEY (`_id`) )");
		database.execSQL("CREATE TABLE screenshots (snapshot_id INTEGER, image_url VARCHAR)");

		database.execSQL("CREATE TABLE thumbnails (snapshot_id INTEGER NOT NULL PRIMARY KEY, image BLOB NOT NULL)");
		database.execSQL("CREATE TABLE stash (snapshot_id INTEGER NOT NULL PRIMARY KEY)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		if (oldVersion < 2) {
			database.execSQL("CREATE TABLE thumbnails (snapshot_id INTEGER NOT NULL PRIMARY KEY, image BLOB NOT NULL)");
		}
	}
}