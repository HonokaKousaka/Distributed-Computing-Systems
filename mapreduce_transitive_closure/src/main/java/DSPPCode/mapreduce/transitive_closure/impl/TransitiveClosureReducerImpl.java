package DSPPCode.mapreduce.transitive_closure.impl;

import DSPPCode.mapreduce.transitive_closure.question.TransitiveClosureReducer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransitiveClosureReducerImpl extends TransitiveClosureReducer {

  @Override
  public void reduce(Text key, Iterable<Text> values,
      Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
    String key_string = String.valueOf(key);
    List<String> children = new ArrayList<>();
    List<String> parents = new ArrayList<>();
    for (Text value: values) {
      String[] words = value.toString().split("\\s+");
      if (words[1].equals(key_string)) {
        children.add(words[0]);
      }
      if (words[0].equals(key_string)) {
        parents.add(words[1]);
      }
    }

    for (String child: children) {
      for (String parent: parents) {
        context.write(new Text(child), new Text(parent));
      }
    }

  }
}
