package io.srcmaxim.blog;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.SecretValue;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.codebuild.*;
import software.amazon.awscdk.services.codepipeline.Artifact;
import software.amazon.awscdk.services.codepipeline.Pipeline;
import software.amazon.awscdk.services.codepipeline.StageOptions;
import software.amazon.awscdk.services.codepipeline.actions.CodeBuildAction;
import software.amazon.awscdk.services.codepipeline.actions.EcrSourceAction;
import software.amazon.awscdk.services.codepipeline.actions.GitHubSourceAction;
import software.amazon.awscdk.services.codepipeline.actions.GitHubTrigger;
import software.amazon.awscdk.services.ecr.Repository;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.ssm.StringParameter;

import java.util.List;

public class BlogDeployPipelineStack extends Stack {

    public BlogDeployPipelineStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        var cdkSourceOutput = Artifact.artifact("CDK_SOURCE");
        var lambdaSourceOutput = Artifact.artifact("LAMBDA_SOURCE");

        var lambdaRepository = Repository.fromRepositoryName(this, "BlogLambdaRepositorySource", "blog-lambda");

        var pipeline = Pipeline.Builder.create(this, "BlogDeployPipeline")
                .pipelineName("BlogDeployPipeline")
                .build();

        var lambdaImageVersion = StringParameter.valueForStringParameter(this, "/dev/blog-lambda/ecr-image/version");

        pipeline.addStage(StageOptions.builder()
                .stageName("Source")
                .actions(List.of(
                        EcrSourceAction.Builder.create()
                                .actionName("RepositoryLambdaSource")
                                .output(lambdaSourceOutput)
                                .repository(lambdaRepository)
                                .imageTag(lambdaImageVersion)
                                .build(),
                        GitHubSourceAction.Builder.create()
                                .actionName("GitHubCdkSource")
                                .output(cdkSourceOutput)
                                .oauthToken(SecretValue.secretsManager("GITHUB_TOKEN"))
                                .owner("srcmaxim")
                                .repo("aws-blog")
                                .branch("master")
                                .trigger(GitHubTrigger.POLL)
                                .build()
                )).build());

        var cdkDeployProject = PipelineProject.Builder.create(this, "CdkDeployProject")
                .environment(BuildEnvironment.builder()
                        .buildImage(LinuxBuildImage.STANDARD_4_0)
                        .computeType(ComputeType.SMALL)
                        .build())
                .buildSpec(BuildSpec.fromSourceFilename("src/blog-pipeline/buildspec.yml"))
                .build();

        var policyAllowAll = PolicyStatement.Builder.create()
                .actions(List.of(
                        "s3:*",
                        "dynamodb:*",
                        "cloudformation:*",
                        "codedeploy:*",
                        "iam:*",
                        "ecr:*"
                ))
                .resources(List.of("*"))
                .build();

        cdkDeployProject.addToRolePolicy(policyAllowAll);

        pipeline.addStage(StageOptions.builder()
                .stageName("Deploy")
                .actions(List.of(
                        CodeBuildAction.Builder.create()
                                .actionName("CdkDeploy")
                                .project(cdkDeployProject)
                                .input(cdkSourceOutput)
                                .build()
                )).build());
    }

}
