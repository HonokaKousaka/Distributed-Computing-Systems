package DSPPCode.mapreduce.common_pagerank.impl;

import DSPPCode.mapreduce.common_pagerank.question.PageRankJoinReducer;
import DSPPCode.mapreduce.common_pagerank.question.PageRankReducer;
import DSPPCode.mapreduce.common_pagerank.question.PageRankRunner;
import DSPPCode.mapreduce.common_pagerank.question.utils.ReducePageRankWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import java.io.IOException;

public class PageRankReducerImpl extends PageRankReducer {

  @Override
  public void reduce(Text key, Iterable<ReducePageRankWritable> values,
      Reducer<Text, ReducePageRankWritable, Text, NullWritable>.Context context)
      throws IOException, InterruptedException {
    double D = 0.85;
    String[] pageinfo = null;
    int totalPage = context.getConfiguration().getInt(PageRankRunner.TOTAL_PAGE, 0);
    int iteration = context.getConfiguration().getInt(PageRankRunner.ITERATION, 0);
    // double delta = context.getConfiguration().getDouble(PageRankRunner.DELTA, 0);
    double sum = 0;
    for (ReducePageRankWritable value: values) {
      String tag = value.getTag();
      if (tag.equals(ReducePageRankWritable.PR_L)) {
        sum += Double.parseDouble(value.getData());
      } else if (tag.equals(ReducePageRankWritable.PAGE_INFO)) {
        pageinfo = value.getData().split( " ");
      }
    }
    double pageRank = (1 - D) / totalPage + D * sum;
    double lastRank = Double.parseDouble(pageinfo[1]);
    pageinfo[1] = String.valueOf(pageRank);

    StringBuilder result = new StringBuilder();
    if (iteration == (PageRankRunner.MAX_ITERATION - 1) || (Math.abs(lastRank - pageRank) <= PageRankRunner.DELTA)) {
      for (String data: pageinfo) {
        result.append(data).append(" ");
      }
      context.getCounter(PageRankRunner.GROUP_NAME, PageRankRunner.COUNTER_NAME).increment(1);
    } else {
      for (String data: pageinfo) {
        result.append(data).append(" ");
      }
    }
    System.out.println(result.toString());
    context.write(new Text(result.toString()), NullWritable.get());
  }
}
