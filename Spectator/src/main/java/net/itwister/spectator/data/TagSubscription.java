package net.itwister.spectator.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = TagSubscription.TABLE_NAME)
public class TagSubscription {

	public static final String TABLE_NAME = "tags_subscriptions";

	public static final String TAG_ID = "tag_id";
	public static final String SUBSCRIPTION_ID = "subscription_id";

	@DatabaseField(columnName = TAG_ID)
	public int tagId;

	@DatabaseField(columnName = SUBSCRIPTION_ID)
	public int subscriptionId;
}