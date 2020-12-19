package io.srcmaxim.blog;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.codebuild.*;
import software.amazon.awscdk.services.codepipeline.Artifact;
import software.amazon.awscdk.services.codepipeline.Pipeline;
import software.amazon.awscdk.services.codepipeline.StageOptions;
import software.amazon.awscdk.services.codepipeline.actions.CodeBuildAction;
import software.amazon.awscdk.services.codepipeline.actions.GitHubSourceAction;
import software.amazon.awscdk.services.codepipeline.actions.GitHubTrigger;
import software.amazon.awscdk.services.ecr.Repository;

import java.util.List;
import java.util.Map;

public class BlogPipelineStack extends Stack {

    public BlogPipelineStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        var lambdaSourceOutput = Artifact.artifact("LAMBDA_SOURCE");

        var lambdaRepository = Repository.Builder.create(this, "BlogLambdaRepository")
                .repositoryName("blog-lambda")
                .build();

        var pipeline = Pipeline.Builder.create(this, "BlogPipeline")
                .build();

        pipeline.addStage(StageOptions.builder()
                .stageName("Source")
                .actions(List.of(
                        GitHubSourceAction.Builder.create()
                                .actionName("GitHubLambdaSource")
                                .output(lambdaSourceOutput)
                                .oauthToken(SecretValue.secretsManager("GITHUB_TOKEN"))
                                .owner("srcmaxim")
                                .repo("aws-blog")
                                .branch("master")
                                .trigger(GitHubTrigger.POLL)
                                .build()
                )).build());

        var lambdaBuildProject = PipelineProject.Builder.create(this, "LambdaBuildProject")
                .environment(BuildEnvironment.builder()
                        .buildImage(LinuxBuildImage.STANDARD_4_0)
                        .computeType(ComputeType.MEDIUM)
                        .privileged(true)
                        .environmentVariables(Map.of(
                                "AWS_ACCOUNT_ID", BuildEnvironmentVariable.builder()
                                        .type(BuildEnvironmentVariableType.PLAINTEXT)
                                        .value(this.getAccount())
                                        .build(),
                                "AWS_DEFAULT_REGION", BuildEnvironmentVariable.builder()
                                        .type(BuildEnvironmentVariableType.PLAINTEXT)
                                        .value(this.getRegion())
                                        .build()
                        ))
                        .build())
                .buildSpec(BuildSpec.fromSourceFilename("src/blog-lambda/buildspec.yml"))
                .cache(Cache.local(LocalCacheMode.DOCKER_LAYER))
                .build();

        lambdaRepository.grantPullPush(lambdaBuildProject);

        pipeline.addStage(StageOptions.builder()
                .stageName("LambdaBuild")
                .actions(List.of(
                        CodeBuildAction.Builder.create()
                                .actionName("LambdaBuild")
                                .project(lambdaBuildProject)
                                .input(lambdaSourceOutput)
                                .build()
                )).build());
    }

}
