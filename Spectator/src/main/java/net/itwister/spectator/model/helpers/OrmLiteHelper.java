package net.itwister.spectator.model.helpers;

import java.sql.SQLException;

import android.database.Cursor;

import com.j256.ormlite.android.AndroidCompiledStatement;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.support.CompiledStatement;

public class OrmLiteHelper {

	public static String arrayToWhereIn(int[] arguments) { // NO_UCD (unused code)
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arguments.length; i++) {
			if (i > 0) sb.append(", ");
			sb.append(arguments[i]);
		}
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	public static Cursor queryToCursor(QueryBuilder query, OrmLiteSqliteOpenHelper openHelper) throws SQLException {
		CompiledStatement stat = query.prepare().compile(openHelper.getConnectionSource().getReadOnlyConnection(), StatementType.SELECT);
		Cursor c = ((AndroidCompiledStatement) stat).getCursor();
		return c;
	}

	//	public static Cursor rawTagsJoinSubscripiptions(OrmLiteSqliteOpenHelper helper) {
	//		SQLiteDatabase db = helper.getReadableDatabase();
	//		return db.rawQuery("select t.*, group_concat(s.subscription_id) as subscriptions from tags t left join tags_subscriptions s on s.tag_id = t._id", null);
	//	}
}