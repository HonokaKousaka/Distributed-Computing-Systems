package DSPPCode.mapreduce.frequent_item_analysis.impl;

import DSPPCode.mapreduce.frequent_item_analysis.question.FrequentItemAnalysisMapper;
import DSPPCode.mapreduce.frequent_item_analysis.impl.SortHelperImpl;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrequentItemAnalysisMapperImpl extends FrequentItemAnalysisMapper {

  @Override
  public void map(LongWritable key, Text value,
      Mapper<LongWritable, Text, Text, IntWritable>.Context context)
      throws IOException, InterruptedException {
    SortHelperImpl sortHelper = new SortHelperImpl();
    String str = value.toString();
    String[] products = str.split(",");
    int number = context.getConfiguration().getInt("number.of.pairs", 0);
    if (products.length < number) {}
    // else if (products.length == number) {
    //   List<String> subset = new ArrayList<>(Arrays.asList(products).subList(0, number));
    //   List<String> sortedList = sortHelper.sortSeq(subset);
    //   String[] item = sortedList.toArray(new String[0]);
    //   String itemset = String.join(",", item);
    //   context.write(new Text(itemset), new IntWritable(1));
    // }
    else {
      int totalSubsets = 1 << products.length;
      for (int i = 0; i < totalSubsets; i++) {
        if (Integer.bitCount(i) == number) {
          List<String> subset = new ArrayList<>();
          for (int j = 0; j < products.length; j++) {
            if ((i & (1 << j)) != 0) {
              subset.add(products[j]);
            }
          }
          List<String> sortedList = sortHelper.sortSeq(subset);
          String[] item = sortedList.toArray(new String[0]);
          String itemset = String.join(",", item);
          context.write(new Text(itemset), new IntWritable(1));
        }
      }
    }
  }
  }
