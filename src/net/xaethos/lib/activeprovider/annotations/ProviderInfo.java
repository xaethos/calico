package net.xaethos.lib.activeprovider.annotations;

import net.xaethos.lib.activeprovider.content.ActiveMigration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProviderInfo {
    String databaseName();
    Class<?>[] models();
    Class<? extends ActiveMigration>[] migrations();
}
