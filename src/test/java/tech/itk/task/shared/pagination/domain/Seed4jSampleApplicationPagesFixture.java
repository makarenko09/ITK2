package tech.itk.task.shared.pagination.domain;

import java.util.List;

public final class Seed4jSampleApplicationPagesFixture {

  private Seed4jSampleApplicationPagesFixture() {}

  public static Seed4jSampleApplicationPage<String> page() {
    return pageBuilder().build();
  }

  public static Seed4jSampleApplicationPage.Seed4jSampleApplicationPageBuilder<String> pageBuilder() {
    return Seed4jSampleApplicationPage.builder(List.of("test")).currentPage(2).pageSize(10).totalElementsCount(21);
  }
}
