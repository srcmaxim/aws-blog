package io.srcmaxim.blog;

import software.amazon.awscdk.core.App;

public class BlogApp extends App {

    BlogApp() {

        var pipelineStack = new PipelineStack(this, "PipelineStack", null);

    }

    public static void main(final String[] args) {

        BlogApp blogApp = new BlogApp();
        blogApp.synth();

    }

}
