package io.srcmaxim.blog.config;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "meta")
public class MetaConfig {

    public String buildNumber;
    public String sourceVersion;

}
