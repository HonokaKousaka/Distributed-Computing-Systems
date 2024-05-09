package DSPPCode.mapreduce.difference.impl;

import DSPPCode.mapreduce.difference.question.DifferenceReducer;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DifferenceReducerImpl extends DifferenceReducer {

  @Override
  public void reduce(Text key, Iterable<Text> values,
      Reducer<Text, Text, Text, NullWritable>.Context context)
      throws IOException, InterruptedException {
    Set<String> R = new HashSet<>();
    List<String> S = new ArrayList<>();
    // values里面是键值对里的值
    for (Text value: values) {
      String[] info = value.toString().split("\t");
      if (info[0].equals("R")) {
        R.add(info[1] + "\t" + info[2]);
      }
      if (info[0].equals("S")) {
        S.add(info[1] + "\t" + info[2]);
      }

    }
    for (String r:R) {
      if (!S.contains(r)) {
        context.write(new Text(r), NullWritable.get());
      }
    }
  }
}
