package com.fteotini.amf.tester.integrationTests.providers.JUnit5;

import com.fteotini.amf.tester.ExecutionSummary.ExecutionResult;
import com.fteotini.amf.tester.ExecutionSummary.TestEntity;
import com.fteotini.amf.tester.ExecutionSummary.TestEntityType;
import com.fteotini.amf.tester.TestRunner;
import com.fteotini.amf.tester.providers.JUnit5.JUnit5TestRunnerFactory;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("IntegrationTest")
class JUnit5TestRunnerTest {
    private static final String SUREFIRE_VERSION = System.getProperty("surefire.version");
    private static final String COMPILER_VERSION = System.getProperty("compiler.version");
    private static final JUnit5TestRunnerFactory testRunnerFactory = new JUnit5TestRunnerFactory();

    @Test
    void it_can_run_a_suite() throws MavenInvocationException, URISyntaxException {
        var sut = buildTestRunnerForSubProject("JUnit5TestRunner_Integration_project");
        var result = sut.runEntireSuite().getTestContainers();

        assertThat(result).hasSize(4);
        assertThat(result.stream().map(TestEntity::getEntityName)).containsExactlyInAnyOrder(
                "SkippedClassTest",
                "With2SuccessfulTest",
                "With1FailingAnd1SuccessfulTest",
                "With2SkippedAnd1SuccessfulTest"
        );
        assertThat(result).allSatisfy(e -> assertThat(e.getType()).isEqualTo(TestEntityType.Class));

        var skippedClassTest = getTestClassByEntityName(result, "SkippedClassTest");
        assertThat(skippedClassTest.getResult()).isEqualTo(ExecutionResult.Skipped);
        assertThat(skippedClassTest.getSkipReason()).isNotEmpty();
        assertThat(skippedClassTest.getException()).isEmpty();
        assertThat(skippedClassTest.hasChildren()).isFalse();

        var with2SuccessfulTest = getTestClassByEntityName(result, "With2SuccessfulTest");
        assertThat(with2SuccessfulTest.getResult()).isEqualTo(ExecutionResult.Success);
        assertThat(with2SuccessfulTest.getSkipReason()).isEmpty();
        assertThat(with2SuccessfulTest.getException()).isEmpty();
        assertThat(with2SuccessfulTest.hasChildren()).isTrue();

        var with1FailingAnd1SuccessfulTest = getTestClassByEntityName(result, "With1FailingAnd1SuccessfulTest");
        assertThat(with1FailingAnd1SuccessfulTest.getSkipReason()).isEmpty();
        assertThat(with1FailingAnd1SuccessfulTest.hasChildren()).isTrue();
        //TODO: maybe it's better to propagate the child error to the container
        assertThat(with1FailingAnd1SuccessfulTest.getResult()).isEqualTo(ExecutionResult.Success);
        assertThat(with1FailingAnd1SuccessfulTest.getException()).isEmpty();

        var with2SkippedAnd1SuccessfulTest = getTestClassByEntityName(result, "With2SkippedAnd1SuccessfulTest");
        assertThat(with2SkippedAnd1SuccessfulTest.getResult()).isEqualTo(ExecutionResult.Success);
        assertThat(with2SkippedAnd1SuccessfulTest.getSkipReason()).isEmpty();
        assertThat(with2SkippedAnd1SuccessfulTest.getException()).isEmpty();
        assertThat(with2SkippedAnd1SuccessfulTest.hasChildren()).isTrue();
    }

    @Test
    void Given_a_successful_run_then_result_for_With2SkippedAnd1SuccessfulTest_children_is_as_expected() throws MavenInvocationException, URISyntaxException {
        var sut = buildTestRunnerForSubProject("JUnit5TestRunner_Integration_project");

        var testClass = getTestClassByEntityName(sut.runEntireSuite().getTestContainers(), "With2SkippedAnd1SuccessfulTest");

        assertThat(testClass.getChildren())
                .hasSize(3)
                .allSatisfy(e -> {
                    assertThat(e.getType()).isEqualTo(TestEntityType.Method);
                    assertThat(e.hasChildren()).isFalse();
                    assertThat(e.getException()).isEmpty();
                });

        var firstSkippedTest = getTestMethodByEntityName(testClass, "it_runs_the_test");
        assertThat(firstSkippedTest.getResult()).isEqualTo(ExecutionResult.Skipped);
        assertThat(firstSkippedTest.getSkipReason()).contains("reason");

        var secondSkippedTest = getTestMethodByEntityName(testClass, "it_runs_the_test_2");
        assertThat(secondSkippedTest.getResult()).isEqualTo(ExecutionResult.Skipped);
        assertThat(secondSkippedTest.getSkipReason()).isNotEmpty();

        var successfulTest = getTestMethodByEntityName(testClass, "it_runs_the_test_3");
        assertThat(successfulTest.getResult()).isEqualTo(ExecutionResult.Success);
        assertThat(successfulTest.getSkipReason()).isEmpty();
    }

    @Test
    void Given_a_successful_run_then_result_for_With1FailingAnd1SuccessfulTest_children_is_as_expected() throws MavenInvocationException, URISyntaxException {
        var sut = buildTestRunnerForSubProject("JUnit5TestRunner_Integration_project");

        var testClass = getTestClassByEntityName(sut.runEntireSuite().getTestContainers(), "With1FailingAnd1SuccessfulTest");

        assertThat(testClass.getChildren())
                .hasSize(2)
                .allSatisfy(e -> {
                    assertThat(e.getType()).isEqualTo(TestEntityType.Method);
                    assertThat(e.hasChildren()).isFalse();
                    assertThat(e.getSkipReason()).isEmpty();
                });

        var successTest = getTestMethodByEntityName(testClass, "it_runs_the_test");
        assertThat(successTest.getResult()).isEqualTo(ExecutionResult.Success);
        assertThat(successTest.getException()).isEmpty();

        var failingTest = getTestMethodByEntityName(testClass, "it_fails");
        assertThat(failingTest.getResult()).isEqualTo(ExecutionResult.Failure);
        assertThat(failingTest.getException())
                .isNotEmpty()
                .containsInstanceOf(RuntimeException.class);
        assertThat(failingTest.getException().get().getMessage()).isEqualTo("purposely failing");
    }

    @Test
    void Given_a_successful_run_then_result_for_With2SuccessfulTest_children_is_as_expected() throws MavenInvocationException, URISyntaxException {
        var sut = buildTestRunnerForSubProject("JUnit5TestRunner_Integration_project");

        var testClass = getTestClassByEntityName(sut.runEntireSuite().getTestContainers(), "With2SuccessfulTest");

        assertThat(testClass.getChildren()).hasSize(2);

        assertThat(testClass.getChildren())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(
                        TestEntity.Success("success_1()", TestEntityType.Method, Collections.emptySet()),
                        TestEntity.Success("success_2()", TestEntityType.Method, Collections.emptySet())
                );
    }

    private static List<String> BuildGoalsList(String... goals) {
        var list = new ArrayList<String>();

        list.add("-Dsurefire.version=" + SUREFIRE_VERSION);
        list.add("-Dcompiler.version=" + COMPILER_VERSION);
        list.add("-q");

        list.addAll(Arrays.asList(goals));

        return list;
    }

    private static TestEntity getTestClassByEntityName(Set<TestEntity> result, String entityName) {
        return result.stream().filter(e -> e.getEntityName().equals(entityName)).findFirst().get();
    }

    private TestEntity getTestMethodByEntityName(TestEntity testClass, String testName) {
        return testClass.getChildren().stream().filter(e -> e.getEntityName().startsWith(testName)).findFirst().get();
    }

    private TestRunner buildTestRunnerForSubProject(String projectName) throws MavenInvocationException, URISyntaxException {
        BuildTestSubProject(projectName);

        var cp = getProjectClassPath(projectName);
        return testRunnerFactory.createTestRunner(Set.of(cp));
    }

    private Path getProjectClassPath(String projectName) throws URISyntaxException {
        return Path.of(getClass().getClassLoader().getResource(projectName + "/target/test-classes").toURI());
    }

    private void BuildTestSubProject(String projectName) throws MavenInvocationException {
        var pomUrl = getClass().getClassLoader().getResource(projectName + "/pom.xml");

        var invocationRequest = new DefaultInvocationRequest();
        invocationRequest.setPomFile(new File(pomUrl.getFile()));
        invocationRequest.setBatchMode(true);
        invocationRequest.setGoals(BuildGoalsList("clean", "test-compile"));

        var invoker = new DefaultInvoker();
        var result = invoker.execute(invocationRequest);

        if (result.getExitCode() != 0) {
            throw new RuntimeException("maven error");
        }
    }
}
