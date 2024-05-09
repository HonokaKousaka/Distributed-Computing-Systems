package DSPPCode.mapreduce.transitive_closure.impl;

import DSPPCode.mapreduce.transitive_closure.question.TransitiveClosureMapper;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import java.io.IOException;

public class TransitiveClosureMapperImpl extends TransitiveClosureMapper {

  @Override
  public void map(Object key, Text value, Mapper<Object, Text, Text, Text>.Context context)
      throws IOException, InterruptedException {
    String[] words = value.toString().split("\\s+");
    String child = words[0];
    String parent = words[1];
    context.write(new Text(child), new Text(child + " " + parent));
    context.write(new Text(parent), new Text(child + " " + parent));

  }
}
