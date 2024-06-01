package DSPPCode.spark.perceptron.impl;

import DSPPCode.spark.perceptron.question.DataPoint;
import DSPPCode.spark.perceptron.question.IterationStep;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;

public class IterationStepImpl extends IterationStep {

  @Override
  public Broadcast<double[]> createBroadcastVariable(JavaSparkContext sc, double[] localVariable) {
    Broadcast<double[]> broadcastVariable = sc.broadcast(localVariable);
    return broadcastVariable;
  }

  @Override
  public boolean termination(double[] old, double[] newWeightsAndBias) {
    int len = old.length;
    double result = 0.0;
    for (int i = 0; i < len; i++) {
      result += Math.pow(old[i] - newWeightsAndBias[i], 2);
    }
    return result < THRESHOLD;
  }

  @Override
  public double[] runStep(JavaRDD<DataPoint> points, Broadcast<double[]> broadcastWeightsAndBias) {
    double[] weightsAndBias = broadcastWeightsAndBias.value();

    JavaRDD<double[]> gradients = points.map(
        new ComputeGradient(weightsAndBias) {
          @Override
          public double[] call(DataPoint dataPoint) throws Exception {
            double[] features = dataPoint.x;
            double label = dataPoint.y;
            int dimension = features.length;

            double sum = 0.0;
            for (int i = 0; i < dimension; i++) {
              sum += weightsAndBias[i] * features[i];
            }
            sum += weightsAndBias[dimension]; // 加上偏置

            double[] gradient = new double[dimension + 1];
            if (label < 0 && sum >= 0 || label >= 0 && sum < 0) {
              for (int i = 0; i < dimension; i++) {
                gradient[i] = -label * features[i]; // 计算权重的梯度
              }
              gradient[dimension] = -label; // 计算偏置的梯度
            }

            return gradient;
          }
        }
    );

    double[] parameters = gradients.reduce(
        new VectorSum() {
          @Override
          public double[] call(double[] a, double[] b) throws Exception {
            int len = a.length;
            double[] result = new double[len];
            for (int i = 0; i < len; i++) {
              result[i] = a[i] + b[i];
            }
            return result;
          }
        }
    );

    for (int i = 0; i < weightsAndBias.length; i++) {
      weightsAndBias[i] -= STEP * parameters[i];
    }

    return weightsAndBias;
  }
}
