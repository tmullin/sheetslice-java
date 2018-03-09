package net.tmullin.sheetslice.app.guice;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.inject.Provides;
import net.tmullin.sheetslice.app.SheetSliceConfig;
import net.tmullin.sheetslice.app.inject.S3;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;

public class SheetSliceModule extends DropwizardAwareModule<SheetSliceConfig> {
    @Override
    protected void configure() {
        SheetSliceConfig config = configuration();
        bindConstant().annotatedWith(S3.Bucket.class).to(config.sheetslice.aws.s3.bucket);
        bindConstant().annotatedWith(S3.Prefix.class).to(config.sheetslice.aws.s3.prefix);
    }

    @Provides
    public AWSCredentials awsCredentials() {
        SheetSliceConfig.AwsCredential cred = configuration().sheetslice.aws.credentials.get("rw");
        return new BasicAWSCredentials(cred.accessKeyId, cred.secretKey);
    }

    @Provides
    public AmazonS3 s3(AWSCredentials cred) {
        AWSStaticCredentialsProvider credProvider = new AWSStaticCredentialsProvider(cred);
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(credProvider)
                .build();
    }
}
