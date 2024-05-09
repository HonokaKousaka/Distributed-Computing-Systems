package DSPPCode.mapreduce.difference.impl;

import DSPPCode.mapreduce.difference.question.DifferenceMapper;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import java.io.IOException;

public class DifferenceMapperImpl extends DifferenceMapper {
  // 以Alice作为Key，R或S Alice 2作为值
  @Override
  public void map(Object key, Text value, Mapper<Object, Text, Text, Text>.Context context)
      throws IOException, InterruptedException {
    // 获取输入键值对所属的 split
    FileSplit split = (FileSplit) context.getInputSplit();
    // 通过 split 获取键值对所属的文件路径
    String path = split.getPath().toString();
    String info = value.toString();
    String name = info.split("\t")[0];
    if (path.contains("R")) {
      context.write(new Text(name), new Text("R\t" + info));
    }
    else if (path.contains("S")) {
      context.write(new Text(name), new Text("S\t" + info));
    }
  }
}
