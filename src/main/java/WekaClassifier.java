import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.core.FastVector;
import weka.core.Instances;

public class WekaClassifier {

	public static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;
		
		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e){
			System.err.println("File not found: " + filename);
		}
		
		return inputReader;
	}
	
	public static Evaluation classify(Classifier model, Instances trainingSet, 
			Instances testSet) throws Exception {
		Evaluation evaluation = new Evaluation(trainingSet);
		
		model.buildClassifier(trainingSet);
		evaluation.evaluateModel(model, testSet);
		
		return evaluation;
	}
	
	public static double calculateAccuracy(FastVector<Prediction> predictions) {
		double correct = 0;
		
		for (int i = 0; i < predictions.size(); i++) {
			NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
			
			if (np.predicted() == np.actual())
				correct++;
		}
		
		return 100 * correct / predictions.size();
	}
	
	public static Instances[][] crossValidationSplit(Instances data, int numberOfFolds) {
		Instances[][] split = new Instances[2][numberOfFolds];
		
		for (int i = 0; i < numberOfFolds; i++) {
			split[0][i] = data.trainCV(numberOfFolds, i);
			split[1][i] = data.testCV(numberOfFolds, i);
		}
		
		return split;
	}
	
	public static void runModel(String filename) throws Exception {
		BufferedReader datafile = readDataFile(filename);
		
		Instances data = new Instances(datafile);
		// TODO: change this, move label to end of arff file
		data.setClassIndex(data.numAttributes() - 1);
		
		Instances[][] split = crossValidationSplit(data, 10);
		
		Instances[] trainingSplits = split[0];
		Instances[] testSplits = split[1];
		
		Classifier[] model = {
				new NaiveBayes()
		};
		
		for (int i = 0; i < model.length; i++) {
			FastVector<Prediction> predictions = new FastVector<Prediction>();
			
			for (int j = 0; j < trainingSplits.length; j++) {
				Evaluation validation = classify(model[i], trainingSplits[j], testSplits[j]);
				predictions.appendElements(validation.predictions());
			}
			
			double accuracy = calculateAccuracy(predictions);
			
			System.out.println("Accuracy of " + model[i].getClass().getSimpleName() +
					": " + String.format("%.2f%%", accuracy) + "\n");
		}
		
		
	}
}
