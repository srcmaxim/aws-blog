package io.srcmaxim.blog;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stage;
import software.amazon.awscdk.core.StageProps;

public class BlogStage extends Stage {

    BlogStage(Construct scope, String id, StageProps props) {
        super(scope, id, props);

        var blogPipelineStack = new BlogPipelineStack(scope, "BlogPipelineStack", null);

    }

    public static void main(final String[] args) {

        var app = new App();
        var blogStage = new BlogStage(app, "BlogStage", null);
        app.synth();

    }

}
