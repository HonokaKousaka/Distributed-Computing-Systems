package DSPPCode.mapreduce.frequent_item_analysis.impl;

import DSPPCode.mapreduce.frequent_item_analysis.question.FrequentItemAnalysisReducer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import java.io.IOException;

public class FrequentItemAnalysisReducerImpl extends FrequentItemAnalysisReducer {

  @Override
  public void reduce(Text key, Iterable<IntWritable> values,
      Reducer<Text, IntWritable, Text, NullWritable>.Context context)
      throws IOException, InterruptedException {
    int count = context.getConfiguration().getInt("count.of.transactions", 0);
    double threshold = context.getConfiguration().getDouble("support", 0);
    int sum = 0;
    for (IntWritable value: values) {
      sum += value.get();
    }
    if (sum >= threshold * count) {
      context.write(key, NullWritable.get());
    }
  }
}
