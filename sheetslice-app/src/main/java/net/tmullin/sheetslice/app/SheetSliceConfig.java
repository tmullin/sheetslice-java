package net.tmullin.sheetslice.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class SheetSliceConfig extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    public SheetSlice sheetslice;

    public static class SheetSlice {
        @Valid
        @NotNull
        @JsonProperty
        public Aws aws;
    }

    public static class Aws {
        @Valid
        @NotNull
        @JsonProperty
        public Map<String, AwsCredential> credentials;

        @Valid
        @NotNull
        @JsonProperty
        public S3 s3;
    }

    public static class AwsCredential {
        @NotNull
        @JsonProperty
        public String accessKeyId;

        @NotNull
        @JsonProperty
        public String secretKey;
    }

    public static class S3 {
        @NotNull
        @JsonProperty
        public String bucket;

        @NotNull
        @JsonProperty
        public String prefix;
    }
}
