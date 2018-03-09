package net.tmullin.sheetslice.app.inject;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface S3 {
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface Bucket {}

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface Prefix {}
}
