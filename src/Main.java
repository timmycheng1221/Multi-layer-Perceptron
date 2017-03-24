import java.awt.Color;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Main extends JFrame implements ActionListener {
    /**
     * 
     */
    private static final long serialVersionUID = -7960778555020997268L;

    private Plot plotPanel1, plotPanel2;

    private JPanel inputPanel, outputPanel;
    private JLabel learnText, accuracyText, iterationText, inputText, weightText, outputText, hiddenLayerText,
            hiddenLayerNeuronText, outputLayerNeuronText, trainAccuracyText, testAccuracyText;
    private JTextField learn, accuracy, iteration, filename, trainAccuracy, testAccuracy;
    private JComboBox<?> hiddenLayer;
    @SuppressWarnings("rawtypes")
    private JComboBox[] hiddenLayerNeuron = new JComboBox[10];
    private JTextArea inputData, weightData, outputData;
    private JButton fileButton, trainButton, testButton, transformationButton;

    private ArrayList<List<Double>> x = new ArrayList<List<Double>>();
    private ArrayList<Integer> neuron = new ArrayList<Integer>();
    private ArrayList<Double> groupNum = new ArrayList<Double>(); 
    private String[] hiddenLayerOption = new String[] { "1", "2", "3", "4" };
    private String[] neuronOption = new String[] { "2", "3", "4", "5", "6", "7", "8", "9" };
    private DecimalFormat decimalFormat = new DecimalFormat("##.000000");
    private FileDialog fileDialog;
    private FileReader fileReader;
    private BufferedReader bufferedReader;
    
    private double groupTotal = 0, groupMax, groupMin;
    private boolean transformationData = false;
    private MultiPerception mlp = new MultiPerception();

    public Main() {
        setLayout(null);
        setSize(700, 870);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createInputPanel();
        createOutputPanel();
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Main mlp = new Main();
        mlp.setVisible(true);
    }

    public void createInputPanel() {
        inputPanel = new JPanel();
        inputPanel.setBounds(410, 0, 260, 700);
        inputPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        fileButton = new JButton("Open File");
        filename = new JTextField(12);
        filename.setEditable(false);
        learnText = new JLabel("Learning Rate");
        learn = new JTextField(12);
        learn.setText("0.2");
        accuracyText = new JLabel("Accuracy(%)");
        accuracy = new JTextField(14);
        accuracy.setText("80");
        iterationText = new JLabel("Iteration");
        iteration = new JTextField(17);
        iteration.setText("100");
        hiddenLayerText = new JLabel("Number of Hidden Layer(1 ~ 4)");
        hiddenLayer = new JComboBox(hiddenLayerOption);
        hiddenLayerNeuronText = new JLabel("Number of Hidden Layer Neuron(2 ~ 9)");
        outputLayerNeuronText = new JLabel("Number of Output Layer Neuron: 1");

        inputText = new JLabel("Input");
        inputData = new JTextArea(8, 20);
        inputData.setEditable(false);
        weightText = new JLabel("Weight");
        weightData = new JTextArea(8, 20);
        weightData.setEditable(false);
        outputText = new JLabel("RMSE");
        outputData = new JTextArea(8, 20);
        outputData.setEditable(false);
        trainButton = new JButton("Train");
        testButton = new JButton("Test");
        transformationButton = new JButton("Trans");

        inputPanel.add(fileButton);
        inputPanel.add(filename);
        inputPanel.add(learnText);
        inputPanel.add(learn);
        inputPanel.add(accuracyText);
        inputPanel.add(accuracy);
        inputPanel.add(iterationText);
        inputPanel.add(iteration);
        inputPanel.add(hiddenLayerText);
        inputPanel.add(hiddenLayer);
        inputPanel.add(hiddenLayerNeuronText);
        for (int i = 0; i < 4; i++) {
            hiddenLayerNeuron[i] = new JComboBox(neuronOption);
            inputPanel.add(hiddenLayerNeuron[i]);
        }
        inputPanel.add(outputLayerNeuronText);
        inputPanel.add(inputText);
        inputPanel.add(new JScrollPane(inputData));
        inputPanel.add(weightText);
        inputPanel.add(new JScrollPane(weightData));
        inputPanel.add(outputText);
        inputPanel.add(new JScrollPane(outputData));
        inputPanel.add(trainButton);
        inputPanel.add(testButton);
        inputPanel.add(transformationButton);
        this.add(inputPanel);

        plotPanel1 = new Plot(0, 0);
        plotPanel2 = new Plot(0, 410);
        this.add(plotPanel1);
        this.add(plotPanel2);

        fileButton.setActionCommand("openFile");
        fileButton.addActionListener(this);
        trainButton.setActionCommand("train");
        trainButton.addActionListener(this);
        testButton.setActionCommand("test");
        testButton.addActionListener(this);
        transformationButton.setActionCommand("transformation");
        transformationButton.addActionListener(this);
    }

    public void createOutputPanel() {
        outputPanel = new JPanel();
        outputPanel.setBounds(410, 705, 260, 100);
        outputPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        trainAccuracyText = new JLabel("Training Accuracy");
        trainAccuracy = new JTextField(15);
        trainAccuracy.setEditable(false);
        testAccuracyText = new JLabel("Testing Accuracy");
        testAccuracy = new JTextField(15);
        testAccuracy.setEditable(false);
        outputPanel.add(trainAccuracyText);
        outputPanel.add(trainAccuracy);
        outputPanel.add(testAccuracyText);
        outputPanel.add(testAccuracy);
        this.add(outputPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        String command = e.getActionCommand();
        // open a txt file
        if (command == "openFile") {
            // clear the old data
            if (!x.isEmpty()) {
                x.clear();
            }

            fileDialog = new FileDialog(this, "開啟檔案", FileDialog.LOAD);
            fileDialog.setVisible(true);
            filename.setText(fileDialog.getFile());
            try {
                fileReader = new FileReader(fileDialog.getDirectory() + fileDialog.getFile());
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            bufferedReader = new BufferedReader(fileReader);
            String line;
            String[] tempstring;
            int index, dataIndex = 0;
            x.clear();

            inputData.setText("");
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    // split the data. ex.1 0 1
                    tempstring = line.split("\\s+");
                    // some lines' first character is ' ' or a number
                    if (tempstring[0].isEmpty()) {
                        index = 1;
                    } else {
                        index = 0;
                    }
                    // store data
                    x.add(new ArrayList<Double>());
                    x.get(dataIndex).add((double) -1);

                    inputData.setText(inputData.getText() + tempstring[index]);
                    x.get(dataIndex).add(Double.valueOf(tempstring[index]));
                    for (int i = index + 1; i < tempstring.length; i++) {
                        // show the data in the "input" TextArea
                        inputData.setText(inputData.getText() + "," + tempstring[i]);
                        x.get(dataIndex).add(Double.valueOf(tempstring[i]));
                    }
                    inputData.setText(inputData.getText() + "\n");
                    dataIndex++;
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            iteration.setText("" + dataIndex * 2 / 3);
            initial();
        }
        if (command == "train") {
            if (filename.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please open the txt file first");
            } else if (learn.getText().isEmpty() || accuracy.getText().isEmpty() || iteration.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please type the all textfield on the upper right");
            } else {
                plotPanel1.blankPlot();
                plotPanel2.blankPlot();
                neuronNote();
                mlp.initial(groupTotal, x, neuron);
                int count = 0, index;
                double feedForwardOutput, expectOutput, realTrainAccuracy = 0;
                while (count < Integer.valueOf(iteration.getText()) && realTrainAccuracy < Double.valueOf(accuracy.getText())) {
                    if (x.size() > 10) {
                        index = count % (x.size() * 2 / 3);
                    } else {
                        index = count % x.size();
                    }
                    weightData.setText(mlp.getWeight());
                    feedForwardOutput = mlp.feedForward(index);
                    mlp.backPropagation(feedForwardOutput, index);
                    mlp.weightAdjust(Double.valueOf(learn.getText()), index);
                    trainAccuracy.setText("" + mlp.trainAccuracyCalc());
                    outputData.setText(outputData.getText() + Double.parseDouble(decimalFormat.format(mlp.getAverageE())) + "\n");
                    count++;
                }
                testAccuracy.setText("");
                if (count == Integer.valueOf(iteration.getText())) {
                    weightData.setText(mlp.getBestWeight());
                    trainAccuracy.setText("" + mlp.bestTrainAccuracyCalc());
                }
                transformationData = true;
                if (x.get(0).size() == 6) {
                    plotPanel1.cleanData();
                    plotPanel2.cleanData();
                    double temp;
                    for (int i = 0; i < mlp.getTrain_x().size(); i++) {
                        temp = mlp.bestFeedForward(i);
                        for (int j = 0; j <= mlp.getGroupTotal(); j++) {
                            if (mlp.getTrain_x().get(i).get(3) == (j / (groupMax - groupMin)) || mlp.getTrain_x().get(i).get(3) == (j / mlp.getGroupTotal())) {
                                plotPanel1.readData(mlp.getTrain_x().get(i).get(1), mlp.getTrain_x().get(i).get(2), j);
                            }
                            if (temp >= (j / (mlp.getGroupTotal() + 1)) && temp < ((j + 1) / (mlp.getGroupTotal() + 1))) {
                                plotPanel2.readData(mlp.getTrain_x().get(i).get(1), mlp.getTrain_x().get(i).get(2), j);
                            }
                        }
                    }
                    plotPanel1.trainPlot();
                    plotPanel2.trainPlot();
                }
            }
        }
        if (command == "test") {
            if ((plotPanel1.getTrain() && plotPanel2.getTrain()) || transformationData) {
                trainAccuracy.setText("");
                testAccuracy.setText("" + mlp.testAccuracyCalc());
                if (x.get(0).size() == 6) {
                    plotPanel1.cleanData();
                    plotPanel2.cleanData();
                    double temp;
                    for (int i = 0; i < mlp.getTest_x().size(); i++) {
                        temp = mlp.testFeedForward(i);
                        for (int j = 0; j <= mlp.getGroupTotal(); j++) {
                            if (mlp.getTest_x().get(i).get(3) == (j / (groupMax - groupMin)) || mlp.getTest_x().get(i).get(3) == (j / mlp.getGroupTotal())) {
                                plotPanel1.readData(mlp.getTest_x().get(i).get(1), mlp.getTest_x().get(i).get(2), j);
                            }
                            if (temp >= (j / (mlp.getGroupTotal() + 1))
                                    && temp < ((j + 1) / (mlp.getGroupTotal() + 1))) {
                                plotPanel2.readData(mlp.getTest_x().get(i).get(1), mlp.getTest_x().get(i).get(2), j);
                            }
                        }
                    }
                    plotPanel1.testPlot();
                    plotPanel2.testPlot();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please train the input data first");
            }
        }
        if (command == "transformation") {
            if ((plotPanel1.getTrain() && plotPanel2.getTrain()) || transformationData) {
                if(neuron.get(neuron.size() - 2) == 2) {
                    transformationData = false;
                    plotPanel1.cleanData();
                    plotPanel2.cleanData();
                    double temp[] = new double[3];
                    for (int i = 0; i < mlp.getTrain_x().size(); i++) {
                        temp = mlp.bestFeedForwardTrans(true, i);
                        for (int j = 0; j <= mlp.getGroupTotal(); j++) {
                            if (temp[2] >= (j / (mlp.getGroupTotal() + 1)) && temp[2] < ((j + 1) / (mlp.getGroupTotal() + 1))) {
                                plotPanel1.readData(temp[0], temp[1], j);
                            }
                        }
                    }
                    for (int i = 0; i < mlp.getTest_x().size(); i++) {
                        temp = mlp.bestFeedForwardTrans(false, i);
                        for (int j = 0; j <= mlp.getGroupTotal(); j++) {
                            if (temp[2] >= (j / (mlp.getGroupTotal() + 1)) && temp[2] < ((j + 1) / (mlp.getGroupTotal() + 1))) {
                                plotPanel2.readData(temp[0], temp[1], j);
                            }
                        }
                    }
                    plotPanel1.transformationPlot();
                    plotPanel2.transformationPlot();
                } 
            }
        }
    }

    public void initial() {
        //資料集依照期望輸出做排序
        Collections.sort(x, new Comparator<List<Double>>() {
            @Override
            public int compare(List<Double> x1, List<Double> x2) {
                // TODO Auto-generated method stub
                return x1.get(x1.size() - 1).compareTo(x2.get(x2.size() - 1));
            }
        });
        double tempX = -1;
        //groupTotal為分類數
        groupTotal = 0;
        //rangeMin為存期望輸出範圍最小值的index
        //rangeMax為存期望輸出範圍最大值的index
        //groupIndex為存期望輸出的index
        int rangeMin = x.get(0).size();
        int rangeMax = x.get(0).size() + 1;
        int groupIndex = x.get(0).size() - 1;
        //groupMin為期望輸出的最小值，groupMax為期望輸出的最大值
        groupMin = x.get(0).get(groupIndex);
        groupMax = x.get(x.size() - 1).get(groupIndex);
        for (int i = 0; i < x.size(); i++) {
            if (i > 0 && x.get(i).get(groupIndex) != tempX) {
                groupNum.add(tempX);
                groupTotal++;
            }
            x.get(i).add(groupTotal);
            x.get(i).add(groupTotal + 1);
            tempX = x.get(i).get(groupIndex);
            x.get(i).set(groupIndex, normalization(x.get(i).get(groupIndex), groupMin, groupMax));
        }
        groupNum.add(tempX);
        for(int i = 0 ; i < groupNum.size() ; i++) {
            groupNum.set(i, groupNum.get(i) / (groupTotal + 1));
        }
        for (int i = 0; i < x.size(); i++) {
            x.get(i).set(rangeMin, (x.get(i).get(rangeMin) / (groupTotal + 1)));
            x.get(i).set(rangeMax, (x.get(i).get(rangeMax) / (groupTotal + 1)));
        }
    }
    //紀錄各層的神經元數
    public void neuronNote() {
        neuron.clear();
        for (int i = 0; i < hiddenLayer.getSelectedIndex() + 1; i++) {
            neuron.add(hiddenLayerNeuron[i].getSelectedIndex() + 2);
        }
        neuron.add(1);
    }
    //正規化
    public double normalization(double group, double groupMin, double groupMax) {
        return (group - groupMin) / (groupMax - groupMin);
    }
}
