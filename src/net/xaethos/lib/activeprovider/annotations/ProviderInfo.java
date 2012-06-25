package net.xaethos.lib.activeprovider.annotations;

import net.xaethos.lib.activeprovider.content.ActiveMigration;
import net.xaethos.lib.activeprovider.models.Model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProviderInfo {
    String databaseName();
    Class<? extends Model>[] models();
    Class<? extends ActiveMigration>[] migrations();
}
