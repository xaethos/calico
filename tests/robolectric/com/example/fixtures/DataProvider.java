package com.example.fixtures;

import net.xaethos.lib.activeprovider.annotations.ProviderInfo;
import net.xaethos.lib.activeprovider.content.ActiveProvider;

@ProviderInfo(models = { Data.class })
public class DataProvider extends ActiveProvider {

//	final public ArrayList<Migration> migrations = new ArrayList<Migration>();

	@Override
	protected String getDatabaseName() {
		return "test.db";
	}

//	@Override
//	protected Migration[] getMigrations() {
//		return migrations.toArray(new Migration[migrations.size()]);
//	}

}
