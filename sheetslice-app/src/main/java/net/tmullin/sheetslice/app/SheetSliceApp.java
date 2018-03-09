package net.tmullin.sheetslice.app;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.tmullin.sheetslice.app.guice.SheetSliceModule;
import ru.vyarus.dropwizard.guice.GuiceBundle;

public class SheetSliceApp extends Application<SheetSliceConfig> {
    public static void main(String... args) throws Exception {
        new SheetSliceApp().run(args);
    }

    @Override
    public void initialize(Bootstrap<SheetSliceConfig> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
                bootstrap.getConfigurationSourceProvider(),
                new EnvironmentVariableSubstitutor(false)
        ));

        bootstrap.addBundle(new AssetsBundle("/public", "/static", "index.html", "static"));
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(GuiceBundle.<SheetSliceConfig>builder()
                .useWebInstallers()
                .enableAutoConfig(getClass().getPackage().getName())
                .modules(new SheetSliceModule())
                .build());
    }

    @Override
    public void run(SheetSliceConfig configuration, Environment environment) throws Exception {
        environment.getApplicationContext().setMaxFormContentSize(10 * 1024 * 1024);
    }
}
