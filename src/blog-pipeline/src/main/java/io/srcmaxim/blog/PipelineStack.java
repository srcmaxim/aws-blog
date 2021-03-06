package io.srcmaxim.blog;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.pipelines.CdkPipeline;
import software.amazon.awscdk.pipelines.CdkPipelineProps;
import software.amazon.awscdk.pipelines.SimpleSynthAction;
import software.amazon.awscdk.services.codebuild.BuildEnvironment;
import software.amazon.awscdk.services.codebuild.ComputeType;
import software.amazon.awscdk.services.codebuild.LinuxBuildImage;
import software.amazon.awscdk.services.codepipeline.Artifact;
import software.amazon.awscdk.services.codepipeline.actions.GitHubSourceAction;
import software.amazon.awscdk.services.codepipeline.actions.GitHubTrigger;

public class PipelineStack extends Stack {

    public PipelineStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        var sourceArtifact = new Artifact();
        var cloudAssemblyArtifact = new Artifact();

        var pipeline = new CdkPipeline(this, "BlogDeployPipeline", CdkPipelineProps.builder()
                .cloudAssemblyArtifact(cloudAssemblyArtifact)
                .sourceAction(
                        GitHubSourceAction.Builder.create()
                                .actionName("GitHubLambdaSource")
                                .output(sourceArtifact)
                                .oauthToken(SecretValue.secretsManager("GITHUB_TOKEN"))
                                .owner("srcmaxim")
                                .repo("aws-blog")
                                .branch("master")
                                .trigger(GitHubTrigger.POLL)
                                .build())
                .synthAction(SimpleSynthAction.Builder.create()
                        .environment(BuildEnvironment.builder()
                                .buildImage(LinuxBuildImage.STANDARD_4_0)
                                .computeType(ComputeType.SMALL)
                                .build())
                        .sourceArtifact(sourceArtifact)
                        .cloudAssemblyArtifact(cloudAssemblyArtifact)
                        .subdirectory("src/blog-pipeline")
                        .synthCommand("npx cdk synth")
                        .build())
                .build());

        pipeline.addApplicationStage(new BlogStage(this, "BlogStage", null));
    }

    static class BlogStage extends Stage {

        BlogStage(Construct scope, String id, StageProps props) {
            super(scope, id, props);

            var blogPipelineStack = new BlogPipelineStack(this, "BlogPipelineStack", null);
            var blogApiStack = new BlogApiStack(this, "BlogApiStack", null);
            blogApiStack.addDependency(blogPipelineStack);

        }

    }

}
