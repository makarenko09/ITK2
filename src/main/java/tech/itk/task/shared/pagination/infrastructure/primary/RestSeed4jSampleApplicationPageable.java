package tech.itk.task.shared.pagination.infrastructure.primary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import tech.itk.task.shared.generation.domain.ExcludeFromGeneratedCodeCoverage;
import tech.itk.task.shared.pagination.domain.Seed4jSampleApplicationPageable;

@Schema(name = "Seed4jSampleApplicationPageable", description = "Pagination information")
public class RestSeed4jSampleApplicationPageable {

  private int page;
  private int pageSize = 10;

  @ExcludeFromGeneratedCodeCoverage
  public RestSeed4jSampleApplicationPageable() {}

  public RestSeed4jSampleApplicationPageable(int page, int pageSize) {
    this.page = page;
    this.pageSize = pageSize;
  }

  @Min(value = 0)
  @Schema(description = "Page to display (starts at 0)", example = "0")
  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  @Min(value = 1)
  @Max(value = 100)
  @Schema(description = "Number of elements on each page", example = "10")
  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public Seed4jSampleApplicationPageable toPageable() {
    return new Seed4jSampleApplicationPageable(page, pageSize);
  }
}
