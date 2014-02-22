package net.itwister.spectator.data;

import java.io.Serializable;
import java.util.Date;

import android.provider.BaseColumns;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Snapshot.TABLE_NAME)
public class Snapshot implements Serializable {

	private static final long serialVersionUID = 99961384757037334L;

	public static final String TABLE_NAME = "snapshots";

	public static final String ID = BaseColumns._ID;
	public static final String TITLE = "title";
	public static final String SOURCE = "source";
	public static final String THUMBNAIL = "thumbnail";
	public static final String CREATED = "created";
	public static final String HAS_CONTENT = "has_content";
	public static final String SUBSCRIPTION_NAME = "subscription_name";
	public static final String SUBSCRIPTION_ICON = "subscription_icon";
	public static final String HAS_SCREENSHOTS = "has_screenshots";
	public static final String HAS_REVISIONS = "has_revisions";
	public static final String SUBSCRIPTION_ID = "subscription_id";

	public static final String EXTRA_STASH = "stash";

	@DatabaseField(columnName = ID, id = true)
	public int id;

	@DatabaseField(columnName = TITLE)
	public String title;

	@DatabaseField(columnName = THUMBNAIL)
	public int thumbnail;

	@DatabaseField(columnName = SOURCE)
	public String source;

	@DatabaseField(columnName = CREATED, dataType = DataType.DATE_LONG)
	public Date updated;

	@DatabaseField(columnName = HAS_CONTENT)
	public boolean hasContent;

	@DatabaseField(columnName = SUBSCRIPTION_NAME)
	public String subscriptionName;

	@DatabaseField(columnName = SUBSCRIPTION_ICON)
	public int subscriptionIcon;

	@DatabaseField(columnName = HAS_SCREENSHOTS)
	public boolean hasScreenshots;

	@DatabaseField(columnName = HAS_REVISIONS)
	public boolean hasRevisions;

	@DatabaseField(columnName = SUBSCRIPTION_ID)
	public int subscriptionId;
}