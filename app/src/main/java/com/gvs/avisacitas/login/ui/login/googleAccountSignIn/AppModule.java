package com.gvs.avisacitas.login.ui.login.googleAccountSignIn;

import android.content.Context;
import com.gvs.avisacitas.model.sqlite.AvisacitasSQLiteOpenHelper;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

	@Provides
	@Singleton
	public AvisacitasSQLiteOpenHelper provideSQLiteOpenHelper(@ApplicationContext Context context) {
		return AvisacitasSQLiteOpenHelper.getInstance(context);
	}
}
