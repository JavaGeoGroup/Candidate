package ge.project.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class PagingResponseDto {
  private int page;
  private int limit;
  private int totalObjects;
  private List<? extends Object> objects;
}
