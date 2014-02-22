package net.itwister.spectator.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "screenshots")
public class Screenshot {

	@DatabaseField(columnName = "snapshot_id")
	public int snapshotId;

	@DatabaseField(columnName = "image_url")
	public String image;

	public Screenshot() {}

	public Screenshot(String image) {
		this.image = image;
	}
}