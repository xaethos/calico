package net.xaethos.lib.activeprovider.content;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public abstract class ProviderMigration {

	/////////////// Inner classes ///////////////

    /*
	public static class Column {

		public static Column primary(String name) {
			return new Column(name, "INTEGER", "PRIMARY KEY");
		}

		public static Column text(String name) {
			return new Column(name, "TEXT");
		}

		public static Column numeric(String name) {
			return new Column(name, "NUMERIC");
		}

		public static Column integer(String name) {
			return new Column(name, "INTEGER");
		}

		public static Column real(String name) {
			return new Column(name, "REAL");
		}

		public static Column blob(String name) {
			return new Column(name, "BLOB");
		}

		public final String name;
		public final String type;
		public final String constraints[];

		public Column(String name, String type, String... constraints) {
			this.name = name;
			this.type = type;
			this.constraints = constraints;
		}

		public String getColumnDef() {
			StringBuilder sb = new StringBuilder(name);
			sb.append(" ").append(type);
			for (String constraint : constraints) {
				sb.append(" ").append(constraint);
			}
			return sb.toString();
		}

	}
	*/

	/////////////// Abstract methods ///////////////

	public abstract boolean onUpgrade(SQLiteDatabase db);

	/////////////// Instance fields ///////////////

//	private SQLiteDatabase mDatabase;

	/////////////// Instance methods ///////////////

	public void upgrade(SQLiteDatabase db) throws MigrationException {
		if (db == null) throw new NullPointerException();

        db.beginTransaction();
        try {
            if (!onUpgrade(db)) throw new MigrationException();
            db.execSQL("INSERT INTO " + CalicoProvider.MIGRATIONS_TABLE + " VALUES (?)",
                    new String[]{ this.getClass().getSimpleName() });
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
	}

	///// Migration helpers

	/**
	 * Creates a new table. Tables created through this method automatically
	 * have a first column named after {@linkplain BaseColumns#_ID} that is
	 * {@code INTEGER PRIMARY KEY}. Other columns will be added after this id
	 * column, in the order they appear in {@code columns}.
	 * @param tableName The name of the table to create.
	 * @param columns   The definition of the columns to add.
	 *
	protected void createTable(String tableName, Column... columns) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE ").append(tableName);
		sql.append("(").append(Column.primary(BaseColumns._ID).getColumnDef());
		for (Column column : columns) {
			sql.append(",").append(column.getColumnDef());
		}
		sql.append(");");

		mDatabase.execSQL(sql.toString());
	}
    */

	/** Drops the table called {@code tableName}. *
	protected void dropTable(String tableName) {
		StringBuilder sql = new StringBuilder();
		sql.append("DROP TABLE ").append(tableName);
		sql.append(";");
		mDatabase.execSQL(sql.toString());
	}
    */

	/** Renames the table called {@code oldName} to {@code newName}. *
	protected void renameTable(String oldName, String newName) {
		StringBuilder sql = new StringBuilder();
		sql.append("ALTER TABLE ").append(oldName);
		sql.append(" RENAME TO ").append(newName);
		sql.append(";");
		mDatabase.execSQL(sql.toString());
	}
     */

	/**
	 * Adds a new column at the end of the table called {@code tableName}.
	 * @param tableName The name of the table to alter.
	 * @param column    The definition of the column to add.
	 *
	protected void addColumn(String tableName, Column column) {
		StringBuilder sql = new StringBuilder();
		sql.append("ALTER TABLE ").append(tableName);
		sql.append(" ADD COLUMN ").append(column.getColumnDef());
		sql.append(";");
		mDatabase.execSQL(sql.toString());
	}
     */

    /*
	protected void dropColumn(String tableName, String... columnNames) {
		// TODO stub
	}
	*/

}
