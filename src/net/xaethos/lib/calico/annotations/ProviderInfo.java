package net.xaethos.lib.calico.annotations;

import net.xaethos.lib.calico.content.ProviderMigration;
import net.xaethos.lib.calico.models.Model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProviderInfo {
    String databaseName();
    Class<? extends Model>[] models();
    Class<? extends ProviderMigration>[] migrations();
}
