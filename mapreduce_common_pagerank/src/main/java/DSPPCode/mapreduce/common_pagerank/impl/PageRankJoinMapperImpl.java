package DSPPCode.mapreduce.common_pagerank.impl;

import DSPPCode.mapreduce.common_pagerank.question.PageRankJoinMapper;
import DSPPCode.mapreduce.common_pagerank.question.utils.ReduceJoinWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import java.io.IOException;

public class PageRankJoinMapperImpl extends PageRankJoinMapper {

  @Override
  public void map(LongWritable key, Text value,
      Mapper<LongWritable, Text, Text, ReduceJoinWritable>.Context context)
      throws IOException, InterruptedException {
    FileSplit split = (FileSplit) context.getInputSplit();
    String path = split.getPath().toString();
    ReduceJoinWritable writable = new ReduceJoinWritable();
    writable.setData(value.toString());
    String[] datas = value.toString().split("\\s+");
    if (path.contains("pages")) {
      writable.setTag(ReduceJoinWritable.PAGEINFO);
      context.write(new Text(datas[0]), writable);
    }
    if (path.contains("ranks")) {
      writable.setTag(ReduceJoinWritable.PAGERNAK);
      context.write(new Text(datas[0]), writable);
    }

  }
}
