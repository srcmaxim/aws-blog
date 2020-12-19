package io.srcmaxim.blog;

import software.amazon.awscdk.core.App;

public class BlogApp extends App {

    BlogApp() {

        var blogPipelineStack = new BlogPipelineStack(this, "BlogPipelineStack", null);
        var blogDeployPipelineStack = new BlogDeployPipelineStack(this, "BlogDeployPipelineStack", null);

    }

    public static void main(final String[] args) {

        BlogApp blogApp = new BlogApp();
        blogApp.synth();

    }

}
