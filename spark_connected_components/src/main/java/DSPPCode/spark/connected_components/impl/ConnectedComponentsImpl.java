package DSPPCode.spark.connected_components.impl;

import DSPPCode.spark.connected_components.question.ConnectedComponents;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConnectedComponentsImpl extends ConnectedComponents {

  @Override
  public JavaPairRDD<String, Integer> getcc(JavaRDD<String> text) {
    // 将输入文本转换为顶点关系RDD
    JavaPairRDD<String, Integer> V_minV_pair = text.flatMapToPair(line -> {
      String[] vertices=line.split("\\s+");
      List<Integer> connectedVertex=new ArrayList<>();
      Integer min=Integer.parseInt(vertices[0]);
      for(int i=0;i<vertices.length;i++){
        Integer tempvalue=Integer.parseInt(vertices[i]);
        connectedVertex.add(tempvalue);
        if(tempvalue<min)min=tempvalue;
      }
      List<Tuple2<String,Integer>> v_minv_pair=new ArrayList<>();
      Integer finalMin = min;
      connectedVertex.forEach(n -> v_minv_pair.add(new Tuple2<>(String.valueOf(n), finalMin)));
      return v_minv_pair.iterator();
    });
    V_minV_pair.foreach(var->System.out.println(var));
    JavaPairRDD<String, Integer> min = V_minV_pair.reduceByKey(Math::min);
    System.out.println("min");
    min.foreach(var->System.out.println(var) );
    JavaPairRDD<String, Integer> minNew=min;
    JavaPairRDD<String,Integer> min_reversed;
    JavaPairRDD<String,Tuple2<Integer,Integer>> joined;
    JavaPairRDD<String, Integer> Joined;
    do {
      // 更新连通分量
      min=minNew;
      min_reversed=min.mapToPair(
          item->new Tuple2<>(String.valueOf(item._2),Integer.parseInt(item._1))
      );
      joined=min.join(min_reversed);
      Joined = joined.mapToPair(
          item -> new Tuple2<>(String.valueOf(item._2()._1()), item._2()._2())
      );
      minNew = min.union(Joined).reduceByKey(Math::min);
      System.out.println("minNew after reduceByKey");
      minNew.foreach(var->System.out.println(var) );
    } while (isChange(min, minNew)); // 检测是否有变化
    System.out.println("minNew ");
    minNew.foreach(var->System.out.println(var) );
    System.out.println("min");
    min.foreach(var->System.out.println(var) );
    JavaPairRDD<String, Integer> sortedMin = min.mapToPair(pair -> new Tuple2<>(String.valueOf(pair._1), pair._2));
    sortedMin.foreach(pair->System.out.println("Intermediate minNew: " + pair));

    return sortedMin;
  }


}

