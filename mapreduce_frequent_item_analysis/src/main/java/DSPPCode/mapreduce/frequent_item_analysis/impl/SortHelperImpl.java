package DSPPCode.mapreduce.frequent_item_analysis.impl;

import DSPPCode.mapreduce.frequent_item_analysis.question.SortHelper;
import java.util.Collections;
import java.util.List;

public class SortHelperImpl extends SortHelper {

  @Override
  public List<String> sortSeq(List<String> input) {
    Collections.sort(input);
    return input;
  }
}
