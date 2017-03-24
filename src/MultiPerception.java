import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiPerception {

    private ArrayList<List<Double>> train_x;
    private ArrayList<List<Double>> test_x;
    private ArrayList<Integer> neuron = new ArrayList<Integer>();
    private ArrayList<List<Double>> weightNeuron = new ArrayList<List<Double>>();
    private ArrayList<List<Double>> bestWeightNeuron = new ArrayList<List<Double>>();
    private ArrayList<List<Double>> outputLayer = new ArrayList<List<Double>>();
    private ArrayList<Double> outputNeuron = new ArrayList<Double>();

    private int neuronTotal, groupIndex = 0, rangeMin, rangeMax;
    private double[] delta = new double[40];
    private double groupTotal, trainAccuracy = 0, averageE;
    private DecimalFormat decimalFormat = new DecimalFormat("##.000");

    public void initial(double groupTotal, ArrayList<List<Double>> x, ArrayList<Integer> neuron) {
        this.neuron = neuron;
        this.groupTotal = groupTotal;

        trainAccuracy = 0;
        groupIndex = x.get(0).size() - 3;
        rangeMin = x.get(0).size() - 2;
        rangeMax = x.get(0).size() - 1;
        // random the weight
        neuronCount();
        weightRandom();
        // random the data and pick the 2/3 data to train
        Collections.shuffle(x);
        if (x.size() > 10) {
            train_x = new ArrayList<List<Double>>(x.subList(0, x.size() * 2 / 3));
            test_x = new ArrayList<List<Double>>(x.subList(x.size() * 2 / 3, x.size()));
        } else {
            train_x = new ArrayList<List<Double>>(x);
            test_x = new ArrayList<List<Double>>(x);
        }
    }

    public double feedForward(int index) {
        int weightIndex = 0;
        double outputTemp;
        outputLayer.clear();
        outputNeuron.clear();
        // first hidden layer
        outputLayer.add(new ArrayList<Double>());
        outputLayer.get(0).add((double) -1);
        for (int i = 0; i < neuron.get(0); i++) {
            outputTemp = outputCalc(train_x.get(index), weightNeuron.get(weightIndex));
            outputLayer.get(0).add(outputTemp);
            outputNeuron.add(outputTemp);
            weightIndex++;
        }
        // second hidden layer ~ last hidden layer
        for (int j = 1; j < neuron.size() - 1; j++) {
            outputLayer.add(new ArrayList<Double>());
            outputLayer.get(j).add((double) -1);
            for (int k = 0; k < neuron.get(j); k++) {
                outputTemp = outputCalc(outputLayer.get(j - 1), weightNeuron.get(weightIndex));
                outputLayer.get(j).add(outputTemp);
                outputNeuron.add(outputTemp);
                weightIndex++;
            }
        }
        // output layer
        outputTemp = outputCalc(outputLayer.get(neuron.size() - 2), weightNeuron.get(weightIndex));
        outputLayer.add(new ArrayList<Double>());
        outputLayer.get(neuron.size() - 1).add(outputTemp);
        outputNeuron.add(outputTemp);
        return outputTemp;
    }

    public void backPropagation(double outputFeedForward, int index) {
        int neuronIndex = neuronTotal - 1;
        if (outputFeedForward >= train_x.get(index).get(rangeMin)
                && outputFeedForward <= train_x.get(index).get(rangeMax)) {

        } else {
            delta[neuronIndex] = deltaCalc1(train_x.get(index).get(groupIndex), outputNeuron.get(neuronIndex));
            neuronIndex--;
            for (int i = neuron.size() - 2; i >= 0; i--) {
                for (int j = neuron.get(i) - 1; j >= 0; j--) {
                    delta[neuronIndex] = deltaCalc2(outputNeuron.get(neuronIndex), delta, neuron.get(i),
                            neuron.get(i + 1), j + 1, weightNeuron);
                    neuronIndex--;
                }
            }
        }
    }

    public double trainAccuracyCalc() {
        double total = 0;
        averageE = 0;
        for (int i = 0; i < train_x.size(); i++) {
            double temp = feedForward(i);
            if (groupCorrect(temp, i)) {
                total++;
            }
            averageE += Math.pow((train_x.get(i).get(groupIndex) - temp), 2);
        }
        averageE = Math.sqrt(averageE / train_x.size());
        if (total > trainAccuracy) {
            bestWeightNeuron.clear();
            for (int i = 0; i < weightNeuron.size(); i++) {
                bestWeightNeuron.add(new ArrayList<Double>());
                for (int j = 0; j < weightNeuron.get(i).size(); j++) {
                    bestWeightNeuron.get(i).add(weightNeuron.get(i).get(j));
                }
            }
            trainAccuracy = total;
        }
        return (total / train_x.size()) * 100;
    }

    public double testAccuracyCalc() {
        double total = 0;
        for (int i = 0; i < test_x.size(); i++) {
            double temp = bestFeedForward(i);
            if (groupCorrect(temp, i)) {
                total++;
            }
        }
        return (total / test_x.size()) * 100;
    }

    public double bestFeedForward(int index) {
        int weightIndex = 0;
        double outputTemp;
        outputLayer.clear();
        outputNeuron.clear();
        // first hidden layer
        outputLayer.add(new ArrayList<Double>());
        outputLayer.get(0).add((double) -1);
        for (int i = 0; i < neuron.get(0); i++) {
            outputTemp = outputCalc(train_x.get(index), bestWeightNeuron.get(weightIndex));
            outputLayer.get(0).add(outputTemp);
            outputNeuron.add(outputTemp);
            weightIndex++;
        }
        // second hidden layer ~ last hidden layer
        for (int j = 1; j < neuron.size() - 1; j++) {
            outputLayer.add(new ArrayList<Double>());
            outputLayer.get(j).add((double) -1);
            for (int k = 0; k < neuron.get(j); k++) {
                outputTemp = outputCalc(outputLayer.get(j - 1), bestWeightNeuron.get(weightIndex));
                outputLayer.get(j).add(outputTemp);
                outputNeuron.add(outputTemp);
                weightIndex++;
            }
        }
        // output layer
        outputTemp = outputCalc(outputLayer.get(neuron.size() - 2), bestWeightNeuron.get(weightIndex));
        outputLayer.add(new ArrayList<Double>());
        outputLayer.get(neuron.size() - 1).add(outputTemp);
        outputNeuron.add(outputTemp);
        return outputTemp;
    }
    
    public double[] bestFeedForwardTrans(Boolean flag, int index) {
        int weightIndex = 0;
        double outputTemp;
        double result[] = new double[3];
        outputLayer.clear();
        outputNeuron.clear();
        // first hidden layer
        outputLayer.add(new ArrayList<Double>());
        outputLayer.get(0).add((double) -1);
        if(flag) {
            for (int i = 0; i < neuron.get(0); i++) {
                outputTemp = outputCalc(train_x.get(index), bestWeightNeuron.get(weightIndex));
                outputLayer.get(0).add(outputTemp);
                outputNeuron.add(outputTemp);
                weightIndex++;
            }
        } else {
            for (int i = 0; i < neuron.get(0); i++) {
                outputTemp = outputCalc(test_x.get(index), bestWeightNeuron.get(weightIndex));
                outputLayer.get(0).add(outputTemp);
                outputNeuron.add(outputTemp);
                weightIndex++;
            }
        }
        
        // second hidden layer ~ last hidden layer
        for (int j = 1; j < neuron.size() - 1; j++) {
            outputLayer.add(new ArrayList<Double>());
            outputLayer.get(j).add((double) -1);
            for (int k = 0; k < neuron.get(j); k++) {
                outputTemp = outputCalc(outputLayer.get(j - 1), bestWeightNeuron.get(weightIndex));
                outputLayer.get(j).add(outputTemp);
                outputNeuron.add(outputTemp);
                weightIndex++;
            }
        }
        // output layer
        outputTemp = outputCalc(outputLayer.get(neuron.size() - 2), bestWeightNeuron.get(weightIndex));
        outputLayer.add(new ArrayList<Double>());
        outputLayer.get(neuron.size() - 1).add(outputTemp);
        outputNeuron.add(outputTemp);
        
        result[0] = outputNeuron.get(weightIndex - 2);
        result[1] = outputNeuron.get(weightIndex - 1);
        result[2] = outputNeuron.get(weightIndex);
        return result;
    }
    
    public double testFeedForward(int index) {
        int weightIndex = 0;
        double outputTemp;
        outputLayer.clear();
        outputNeuron.clear();
        // first hidden layer
        outputLayer.add(new ArrayList<Double>());
        outputLayer.get(0).add((double) -1);
        for (int i = 0; i < neuron.get(0); i++) {
            outputTemp = outputCalc(test_x.get(index), bestWeightNeuron.get(weightIndex));
            outputLayer.get(0).add(outputTemp);
            outputNeuron.add(outputTemp);
            weightIndex++;
        }
        // second hidden layer ~ last hidden layer
        for (int j = 1; j < neuron.size() - 1; j++) {
            outputLayer.add(new ArrayList<Double>());
            outputLayer.get(j).add((double) -1);
            for (int k = 0; k < neuron.get(j); k++) {
                outputTemp = outputCalc(outputLayer.get(j - 1), bestWeightNeuron.get(weightIndex));
                outputLayer.get(j).add(outputTemp);
                outputNeuron.add(outputTemp);
                weightIndex++;
            }
        }
        // output layer
        outputTemp = outputCalc(outputLayer.get(neuron.size() - 2), bestWeightNeuron.get(weightIndex));
        outputLayer.add(new ArrayList<Double>());
        outputLayer.get(neuron.size() - 1).add(outputTemp);
        outputNeuron.add(outputTemp);
        return outputTemp;
    }

    public double bestTrainAccuracyCalc() {
        return (trainAccuracy / train_x.size()) * 100;
    }
    
    public double getAverageE() {
        return averageE;
    }

    public ArrayList<List<Double>> getTrain_x() {
        return train_x;
    }

    public ArrayList<List<Double>> getTest_x() {
        return test_x;
    }

    public double getGroupTotal() {
        return groupTotal;
    }

    public boolean groupCorrect(double outputFeedForward, int index) {
        if (outputFeedForward >= train_x.get(index).get(rangeMin)
                && outputFeedForward < train_x.get(index).get(rangeMax)) {
            return true;
        } else {
            return false;
        }
    }

    public void weightAdjust(double learningRate, int index) {
        int neuronIndex = 0;
        for (int i = 0; i < neuron.get(0); i++) {
            weightCalc(train_x.get(index), learningRate, delta[i], weightNeuron.get(i));
            neuronIndex++;
        }
        for (int i = 1; i < neuron.size(); i++) {
            for (int j = 0; j < neuron.get(i); j++) {
                weightCalc(outputLayer.get(i - 1), learningRate, delta[neuronIndex], weightNeuron.get(neuronIndex));
                neuronIndex++;
            }
        }
    }

    public void weightRandom() {
        int weightIndex = 0;
        weightNeuron.clear();
        // weight of first hidden layer neurons
        for (int i = 0; i < neuron.get(0); i++) {
            weightNeuron.add(new ArrayList<Double>());
            for (int j = 0; j < groupIndex; j++) {
                weightNeuron.get(weightIndex).add(Math.random() * 2 - 1);
            }
            weightIndex++;
        }
        // weight of other hidden layer neurons and the output neuron
        for (int i = 1; i < neuron.size(); i++) {
            for (int j = 0; j < neuron.get(i); j++) {
                weightNeuron.add(new ArrayList<Double>());
                for (int k = 0; k < neuron.get(i - 1) + 1; k++) {
                    weightNeuron.get(weightIndex).add(Math.random() * 2 - 1);
                }
                weightIndex++;
            }
        }
        /*
         * weightNeuron.add(new ArrayList<Double>());
         * weightNeuron.get(0).add(-1.2); weightNeuron.get(0).add(1.0);
         * weightNeuron.get(0).add(1.0); weightNeuron.add(new
         * ArrayList<Double>()); weightNeuron.get(1).add(0.3);
         * weightNeuron.get(1).add(1.0); weightNeuron.get(1).add(1.0);
         * weightNeuron.add(new ArrayList<Double>());
         * weightNeuron.get(2).add(0.5); weightNeuron.get(2).add(0.4);
         * weightNeuron.get(2).add(0.8);
         */
    }

    public String getWeight() {
        int weightIndex = 0;
        String tempString = "";
        for (int i = 0; i < neuron.size() - 1; i++) {
            tempString += "Hidden Layer " + (i + 1) + ":";
            for (int j = 0; j < neuron.get(i); j++) {
                tempString += "\nNeuron " + (weightIndex + 1) + ": ";
                for (int k = 0; k < weightNeuron.get(weightIndex).size(); k++) {
                    tempString += Double.parseDouble(decimalFormat.format(weightNeuron.get(weightIndex).get(k)));
                    tempString += " ";
                }
                weightIndex++;
            }
            tempString += "\n";
        }
        tempString += "Output Layer :";
        tempString += "\nNeuron " + (weightIndex + 1) + ": ";
        for (int k = 0; k < weightNeuron.get(weightIndex).size(); k++) {
            tempString += Double.parseDouble(decimalFormat.format(weightNeuron.get(weightIndex).get(k)));
            tempString += " ";
        }
        return tempString;
    }

    public String getBestWeight() {
        int weightIndex = 0;
        String tempString = "";
        for (int i = 0; i < neuron.size() - 1; i++) {
            tempString += "Hidden Layer " + (i + 1) + ":";
            for (int j = 0; j < neuron.get(i); j++) {
                tempString += "\nNeuron " + (weightIndex + 1) + ": ";
                for (int k = 0; k < bestWeightNeuron.get(weightIndex).size(); k++) {
                    tempString += Double.parseDouble(decimalFormat.format(bestWeightNeuron.get(weightIndex).get(k)));
                    tempString += " ";
                }
                weightIndex++;
            }
            tempString += "\n";
        }
        tempString += "Output Layer :";
        tempString += "\nNeuron " + (weightIndex + 1) + ": ";
        for (int k = 0; k < bestWeightNeuron.get(weightIndex).size(); k++) {
            tempString += Double.parseDouble(decimalFormat.format(bestWeightNeuron.get(weightIndex).get(k)));
            tempString += " ";
        }
        return tempString;
    }

    public void neuronCount() {
        neuronTotal = 0;
        for (int i = 0; i < neuron.size(); i++) {
            neuronTotal += neuron.get(i);
        }
    }

    public double outputCalc(List<Double> vector, List<Double> weightVector) {
        double total = 0;
        for (int i = 0; i < weightVector.size(); i++) {
            total += vector.get(i) * weightVector.get(i);
        }
        return 1 / (1 + Math.exp(-1 * total));
    }

    public double deltaCalc1(double expectOutput, double output) {
        return (expectOutput - output) * output * (1 - output);
    }

    public double deltaCalc2(double output, double[] delta, int kLayer, int kLayerNeuron, int j,
            ArrayList<List<Double>> weightNeuron) {
        double total = 0;
        for (int i = 0; i < kLayerNeuron; i++) {
            total += delta[kLayer + i] * weightNeuron.get(kLayer + i).get(j);
        }
        return output * (1 - output) * total;
    }

    public void weightCalc(List<Double> vector, double learningRate, double delta, List<Double> weightVector) {
        double total;
        for (int i = 0; i < weightVector.size(); i++) {
            total = weightVector.get(i) + learningRate * delta * vector.get(i);
            weightVector.set(i, total);
        }
    }
}
