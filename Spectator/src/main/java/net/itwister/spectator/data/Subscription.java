package net.itwister.spectator.data;

import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Subscription.TABLE_NAME)
public class Subscription {

	public static final String TABLE_NAME = "subscriptions";
	public static final String ID = BaseColumns._ID;
	public static final String SOURCE = "source";
	public static final String TITLE = "title";
	public static final String IS_CHECKED = "is_checked";
	public static final String GROUP = "group";
	public static final String THUMBNAIL = "thumbnail";
	public static final String COUNT = "count";

	@DatabaseField(columnName = SOURCE)
	public String source;

	@DatabaseField(columnName = ID, id = true)
	public int id;

	@DatabaseField(columnName = TITLE)
	public String title;

	@DatabaseField(columnName = IS_CHECKED)
	public boolean isChecked;

	@DatabaseField(columnName = GROUP)
	public String group;

	@DatabaseField(columnName = THUMBNAIL)
	public int thumbnail;

	@DatabaseField(columnName = COUNT)
	public int count;
}