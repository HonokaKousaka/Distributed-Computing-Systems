package DSPPCode.mapreduce.common_pagerank.impl;

import DSPPCode.mapreduce.common_pagerank.question.PageRankJoinReducer;
import DSPPCode.mapreduce.common_pagerank.question.utils.ReduceJoinWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageRankJoinReducerImpl extends PageRankJoinReducer {

  @Override
  public void reduce(Text key, Iterable<ReduceJoinWritable> values,
      Reducer<Text, ReduceJoinWritable, Text, NullWritable>.Context context)
      throws IOException, InterruptedException {
    String pageinfo = null, pagerank = null, pageString = null;
    for (ReduceJoinWritable value: values) {
      String tag = value.getTag();
      if (tag.equals(ReduceJoinWritable.PAGEINFO)) {
        pageinfo = value.getData();
        String[] pageSplit = pageinfo.split("\\s+");
        String[] finalPage = new String[pageSplit.length - 1];
        for (int i = 1; i < pageSplit.length; i++) {
          finalPage[i - 1] = pageSplit[i];
        }
        pageString = String.join(" ", finalPage);
      }
      if (tag.equals(ReduceJoinWritable.PAGERNAK)) {
        pagerank = value.getData();
      }
    }
    String result = pagerank + " " + pageString;
    System.out.println(result);
    context.write(new Text(result), NullWritable.get());
  }
}
