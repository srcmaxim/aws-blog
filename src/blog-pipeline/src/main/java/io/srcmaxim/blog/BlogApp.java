package io.srcmaxim.blog;

import software.amazon.awscdk.core.App;

public class BlogApp extends App {

    BlogApp() {

        var blogPipelineStack = new BlogPipelineStack(this, "BlogPipelineStack", null);
        var blogDeployPipelineStack = new BlogDeployPipelineStack(this, "BlogDeployPipelineStack", null);
        var blogApiStack = new BlogApiStack(this, "BlogApiStack", null);
        var blogStage = new PipelineStack.BlogStage(this, "BlogStage", null);

    }

    public static void main(final String[] args) {

        BlogApp blogApp = new BlogApp();
        blogApp.synth();

    }

}
