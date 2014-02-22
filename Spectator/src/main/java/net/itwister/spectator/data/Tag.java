package net.itwister.spectator.data;

import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Tag.TABLE_NAME)
public class Tag {

	public static final String TABLE_NAME = "tags";

	public static final String ID = BaseColumns._ID;
	public static final String NAME = "name";
	public static final String THUMBNAIL = "thumbnail";

	@DatabaseField(id = true, columnName = ID)
	public int id;

	@DatabaseField(columnName = NAME)
	public String name;

	@DatabaseField(columnName = THUMBNAIL)
	public int thumbnail;

	public Tag() {}

	public Tag(int id, String name) {
		this.id = id;
		this.name = name;
	}
}