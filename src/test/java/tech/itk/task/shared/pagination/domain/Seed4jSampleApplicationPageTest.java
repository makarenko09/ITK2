package tech.itk.task.shared.pagination.domain;

import static org.assertj.core.api.Assertions.*;
import static tech.itk.task.shared.pagination.domain.Seed4jSampleApplicationPagesFixture.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import tech.itk.task.UnitTest;
import tech.itk.task.shared.error.domain.MissingMandatoryValueException;

@UnitTest
class Seed4jSampleApplicationPageTest {

  @Test
  void shouldGetEmptySinglePageWithoutContent() {
    Seed4jSampleApplicationPage<String> page = Seed4jSampleApplicationPage.singlePage(null);

    assertEmptyPage(page);
  }

  @Test
  void shouldGetEmptySinglePageFromBuilderWithoutContent() {
    Seed4jSampleApplicationPage<?> page = Seed4jSampleApplicationPage.builder(null).build();

    assertEmptyPage(page);
  }

  private void assertEmptyPage(Seed4jSampleApplicationPage<?> page) {
    assertThat(page.content()).isEmpty();
    assertThat(page.currentPage()).isZero();
    assertThat(page.pageSize()).isZero();
    assertThat(page.totalElementsCount()).isZero();
  }

  @Test
  void shouldGetSinglePage() {
    Seed4jSampleApplicationPage<String> page = Seed4jSampleApplicationPage.singlePage(List.of("test", "dummy"));

    assertSinglePage(page);
  }

  @Test
  void shouldGetSinglePageFromBuilderWithContentOnly() {
    Seed4jSampleApplicationPage<String> page = Seed4jSampleApplicationPage.builder(List.of("test", "dummy")).build();

    assertSinglePage(page);
  }

  private void assertSinglePage(Seed4jSampleApplicationPage<String> page) {
    assertThat(page.content()).containsExactly("test", "dummy");
    assertThat(page.currentPage()).isZero();
    assertThat(page.pageSize()).isEqualTo(2);
    assertThat(page.totalElementsCount()).isEqualTo(2);
    assertThat(page.pageCount()).isEqualTo(1);
  }

  @Test
  void shouldGetFullPage() {
    Seed4jSampleApplicationPage<String> page = pageBuilder().build();

    assertThat(page.content()).containsExactly("test");
    assertThat(page.currentPage()).isEqualTo(2);
    assertThat(page.pageSize()).isEqualTo(10);
    assertThat(page.totalElementsCount()).isEqualTo(21);
    assertThat(page.pageCount()).isEqualTo(3);
  }

  @Test
  void shouldNotMapWithoutMapper() {
    assertThatThrownBy(() -> pageBuilder().build().map(null))
      .isExactlyInstanceOf(MissingMandatoryValueException.class)
      .hasMessageContaining("mapper");
  }

  @Test
  void shouldMapPage() {
    Seed4jSampleApplicationPage<String> page = pageBuilder().build().map(entry -> "hey");

    assertThat(page.content()).containsExactly("hey");
    assertThat(page.currentPage()).isEqualTo(2);
    assertThat(page.pageSize()).isEqualTo(10);
    assertThat(page.totalElementsCount()).isEqualTo(21);
    assertThat(page.pageCount()).isEqualTo(3);
  }

  @Test
  void shouldNotBeLastForFirstPage() {
    assertThat(pageBuilder().currentPage(0).build().isNotLast()).isTrue();
  }

  @Test
  void shouldBeLastWithOnePage() {
    assertThat(Seed4jSampleApplicationPage.singlePage(List.of("d")).isNotLast()).isFalse();
  }

  @Test
  void shouldBeLastPageWithoutContent() {
    Seed4jSampleApplicationPage<Object> page = Seed4jSampleApplicationPage.builder(List.of())
      .currentPage(0)
      .pageSize(1)
      .totalElementsCount(0)
      .build();
    assertThat(page.isNotLast()).isFalse();
  }

  @Test
  void shouldBeLastForLastPage() {
    assertThat(pageBuilder().currentPage(2).build().isNotLast()).isFalse();
  }

  @Test
  void shouldGetPageFromElements() {
    Seed4jSampleApplicationPage<String> page = Seed4jSampleApplicationPage.of(
      List.of("hello", "java", "world"),
      new Seed4jSampleApplicationPageable(1, 1)
    );

    assertThat(page.currentPage()).isEqualTo(1);
    assertThat(page.hasNext()).isTrue();
    assertThat(page.hasPrevious()).isTrue();
    assertThat(page.pageCount()).isEqualTo(3);
    assertThat(page.pageSize()).isEqualTo(1);
    assertThat(page.content()).containsExactly("java");
  }

  @Test
  void shouldGetEmptyPageFromOutOfBoundElements() {
    Seed4jSampleApplicationPage<String> page = Seed4jSampleApplicationPage.of(
      List.of("hello", "java", "world"),
      new Seed4jSampleApplicationPageable(4, 1)
    );

    assertThat(page.currentPage()).isEqualTo(4);
    assertThat(page.hasNext()).isFalse();
    assertThat(page.hasPrevious()).isTrue();
    assertThat(page.pageCount()).isEqualTo(3);
    assertThat(page.pageSize()).isEqualTo(1);
    assertThat(page.content()).isEmpty();
  }

  @Test
  void shouldGetPageWithLessThanExpectedElements() {
    Seed4jSampleApplicationPage<String> page = Seed4jSampleApplicationPage.of(
      List.of("hello", "java", "world"),
      new Seed4jSampleApplicationPageable(0, 4)
    );

    assertThat(page.currentPage()).isZero();
    assertThat(page.hasNext()).isFalse();
    assertThat(page.hasPrevious()).isFalse();
    assertThat(page.pageCount()).isEqualTo(1);
    assertThat(page.pageSize()).isEqualTo(4);
    assertThat(page.content()).hasSize(3);
  }
}
