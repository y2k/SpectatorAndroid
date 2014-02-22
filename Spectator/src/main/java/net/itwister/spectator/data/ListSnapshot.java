package net.itwister.spectator.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = ListSnapshot.TABLE_NAME)
public class ListSnapshot {

	public static final String TABLE_NAME = "subscriptions_snapshots";

	public static final String LIST_ID = "subscriptionId";
	public static final String TYPE = "type";
	public static final String QUERY = "query";
	public static final String SNAPSHOT_ID = "snapshotId";

	@DatabaseField(columnName = LIST_ID)
	public int listId;

	@DatabaseField(columnName = TYPE)
	public int type;

	@DatabaseField(columnName = SNAPSHOT_ID)
	public int snapshotId;

	@DatabaseField(columnName = QUERY)
	public String query;

	public ListSnapshot() {}

	public ListSnapshot(int listId, int type, int snapshotId, String query) {
		this.listId = listId;
		this.type = type;
		this.snapshotId = snapshotId;
		this.query = query;
	}
}