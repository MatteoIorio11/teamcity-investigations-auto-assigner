

package jetbrains.buildServer.investigationsAutoAssigner.utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import jetbrains.buildServer.BuildProblemTypes;
import jetbrains.buildServer.investigationsAutoAssigner.common.Constants;
import jetbrains.buildServer.investigationsAutoAssigner.utils.errors.BuildProblemProcessor;
import jetbrains.buildServer.investigationsAutoAssigner.utils.errors.BuildProblemProcessorFactory;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.STest;
import jetbrains.buildServer.serverSide.STestRun;
import jetbrains.buildServer.serverSide.TeamCityProperties;
import jetbrains.buildServer.serverSide.buildLog.LogMessage;
import jetbrains.buildServer.serverSide.problems.BuildLogCompileErrorCollector;
import jetbrains.buildServer.serverSide.problems.BuildProblem;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.util.ItemProcessor;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static jetbrains.buildServer.serverSide.impl.problems.types.CompilationErrorTypeDetailsProvider.COMPILE_BLOCK_INDEX;

public class ProblemTextExtractor {
  public String getBuildProblemText(@NotNull final BuildProblem problem, @NotNull final SBuild build) {
    return BuildProblemProcessorFactory.getProcessor(problem.getBuildProblemData().getType())
      .map(processor -> processor.process(problem, build, getCompileBlockIndex(problem)))
      .map(text -> text + " " + problem.getBuildProblemDescription())
      .orElse("");
  }

  @Nullable
  private static Integer getCompileBlockIndex(@NotNull final BuildProblem problem) {
    final String compilationBlockIndex = problem.getBuildProblemData().getAdditionalData();
    if (compilationBlockIndex == null) return null;

    try {
      return Integer.parseInt(
        StringUtil.stringToProperties(compilationBlockIndex, StringUtil.STD_ESCAPER2).get(COMPILE_BLOCK_INDEX));
    } catch (Exception e) {
      return null;
    }
  }

  public String getBuildProblemText(STestRun sTestRun) {
    final STest test = sTestRun.getTest();
    final TestName testName = test.getName();
    return testName.getAsString() + " " + sTestRun.getFullText();
  }
}
