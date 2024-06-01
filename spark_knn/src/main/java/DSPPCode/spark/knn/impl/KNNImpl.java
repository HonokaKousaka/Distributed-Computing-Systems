package DSPPCode.spark.knn.impl;

import DSPPCode.spark.knn.question.Data;
import DSPPCode.spark.knn.question.KNN;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class KNNImpl extends KNN {


  public KNNImpl(int k) {
    super(k);
  }

  @Override
  public JavaPairRDD<Data, Data> kNNJoin(JavaRDD<Data> trainData, JavaRDD<Data> queryData) {
    return trainData.cartesian(queryData);
  }

  @Override
  public JavaPairRDD<Integer, Tuple2<Integer, Double>> calculateDistance(JavaPairRDD<Data, Data> data) {
    return data.mapToPair(new PairFunction<Tuple2<Data, Data>, Integer, Tuple2<Integer, Double>>() {
      @Override
      public Tuple2<Integer, Tuple2<Integer, Double>> call(Tuple2<Data, Data> t) {
        Data train = t._1;
        Data query = t._2;
        double[] trainFeatures = train.x;
        double[] queryFeatures = query.x;
        double sum = 0.0;

        for (int i = 0; i < trainFeatures.length; i++) {
          double diff = trainFeatures[i] - queryFeatures[i];
          sum += diff * diff;
        }

        double distance = Math.sqrt(sum);
        return new Tuple2<>(query.id, new Tuple2<>(train.y, distance));
      }
    });
  }

  @Override
  public JavaPairRDD<Integer, Integer> classify(JavaPairRDD<Integer, Tuple2<Integer, Double>> distances) {
    // Use combineByKey to combine distances
    JavaPairRDD<Integer, List<Tuple2<Integer, Double>>> combinedDistances = distances.combineByKey(
        // CreateCombiner: initialize a list with the first value
        (Tuple2<Integer, Double> value) -> {
          List<Tuple2<Integer, Double>> list = new ArrayList<>();
          list.add(value);
          return list;
        },
        // MergeValue: add each new value to the list
        (List<Tuple2<Integer, Double>> list, Tuple2<Integer, Double> value) -> {
          list.add(value);
          return list;
        },
        // MergeCombiners: merge two lists
        (List<Tuple2<Integer, Double>> list1, List<Tuple2<Integer, Double>> list2) -> {
          list1.addAll(list2);
          return list1;
        }
    );

    JavaPairRDD<Integer, Integer> classifiedData = combinedDistances.mapToPair(group -> {
      Integer queryId = group._1();
      List<Tuple2<Integer, Double>> neighbors = group._2();

      // Sort neighbors by distance
      neighbors.sort(Comparator.comparingDouble(Tuple2::_2));

      // Use a HashMap to count labels
      Map<Integer, Integer> labelCount = new HashMap<>();
      int count = 0;
      for (Tuple2<Integer, Double> neighbor : neighbors) {
        if (count < k) {
          int label = neighbor._1();
          labelCount.put(label, labelCount.getOrDefault(label, 0) + 1);
          count++;
        } else {
          break;
        }
      }

      // Determine the most frequent label
      int mostFrequentLabel = -1;
      int maxCount = -1;
      for (Map.Entry<Integer, Integer> entry : labelCount.entrySet()) {
        if (entry.getValue() > maxCount || (entry.getValue() == maxCount && entry.getKey() < mostFrequentLabel)) {
          mostFrequentLabel = entry.getKey();
          maxCount = entry.getValue();
        }
      }

      return new Tuple2<>(queryId, mostFrequentLabel);
    });

    return classifiedData;
  }

}
