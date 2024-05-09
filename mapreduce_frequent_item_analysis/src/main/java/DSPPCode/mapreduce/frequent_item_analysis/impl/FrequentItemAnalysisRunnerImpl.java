package DSPPCode.mapreduce.frequent_item_analysis.impl;

import DSPPCode.mapreduce.frequent_item_analysis.question.FrequentItemAnalysisReducer;
import DSPPCode.mapreduce.frequent_item_analysis.question.FrequentItemAnalysisRunner;
import org.apache.commons.httpclient.URI;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.spark_project.dmg.pmml.True;
import java.io.IOException;
import java.net.URISyntaxException;

public class FrequentItemAnalysisRunnerImpl extends FrequentItemAnalysisRunner {

  @Override
  public void configureMapReduceTask(Job job) throws IOException, URISyntaxException {
    job.setCombinerClass(FrequentItemAnalysisCombinerImpl.class);
  }
}
