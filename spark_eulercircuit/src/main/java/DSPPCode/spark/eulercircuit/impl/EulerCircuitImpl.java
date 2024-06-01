package DSPPCode.spark.eulercircuit.impl;

import DSPPCode.spark.eulercircuit.question.EulerCircuit;
import scala.Tuple2;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import java.util.ArrayList;
import java.util.List;

public class EulerCircuitImpl extends EulerCircuit {

  @Override
  public boolean isEulerCircuit(JavaRDD<String> lines, JavaSparkContext jsc) {
    JavaRDD<Tuple2<Integer, Integer>> edges = lines.map(line -> {
      String[] tokens = line.split("\\s+");
      int source = Integer.parseInt(tokens[0]);
      int destination = Integer.parseInt(tokens[1]);
      return new Tuple2<>(source, destination);
    });

    List<Integer> colleges = edges.flatMap(edge -> {
      List<Integer> list = new ArrayList<>();
      list.add(edge._1);
      list.add(edge._2);
      return list.iterator();
    }).distinct().collect();

    for (Integer college : colleges) {
      long outDegree = edges.filter(edge -> edge._1().equals(college)).count();
      long inDegree = edges.filter(edge -> edge._2().equals(college)).count();
      if ((outDegree + inDegree) % 2 != 0) {
        return false;
      }
    }

    return true;
  }
}
