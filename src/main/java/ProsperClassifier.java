import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.Loader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;

/**
 * ProsperClassifier
 * @author joel
 * Singleton class containing the classifier for Prosper loan data
 *
 */
public final class ProsperClassifier {
	// The singleton object
	private static ProsperClassifier prosperClassifier = null;
	// The actual classifier contained in the singleton
	private static Classifier model;
	
	private ProsperClassifier() {
		model = new NaiveBayes();
	}
	
	public static synchronized ProsperClassifier getInstance() {
		if (prosperClassifier == null)
			prosperClassifier = new ProsperClassifier();
		
		return prosperClassifier;
	}
	
	/**
	 * Loads in data from an arff data file. Uses a filter to convert nominal
	 * data to binary. Randomizes the order of the data instances.
	 * @param filename the path to an arff file
	 * @return an Instances object of the data in the file
	 * @throws Exception
	 */
	private Instances loadData(String filename) throws Exception {
		Instances dataset = null;
		
		try {
			ArffLoader arffLoader = new ArffLoader();
			arffLoader.setSource(new File(filename));
			arffLoader.setRetrieval(Loader.BATCH);
			
			dataset = arffLoader.getDataSet();
			
			Attribute trainAttribute = dataset.attribute(dataset.numAttributes() - 1);
			dataset.setClass(trainAttribute);
		} catch(FileNotFoundException e) {
			System.err.println("Could not find file: " + filename);
		}
		
		String[] option = new String[1];
		option[0] = "-A";
		NominalToBinary ntbFilter = new NominalToBinary();
		ntbFilter.setOptions(option);
		ntbFilter.setInputFormat(dataset);
		
		Instances filteredData = Filter.useFilter(dataset, ntbFilter);
		filteredData.randomize(new Random(1));
		return filteredData;
	}
	
	/**
	 * Builds a classifier based on the data in the input arff file.
	 * @param datafile the path to an arff file
	 * @throws Exception
	 */
	public void buildModel(String datafile) throws Exception {
		Instances dataset = loadData(datafile);
		
		model.buildClassifier(dataset);
			
		SerializationHelper.write("prosper.model", model);
		System.out.println("Saved trained model");
	}
	
	/**
	 * Loads a classifier by reading a previously created classifier from
	 * memory or creating a new one based on data in a file.
	 * @param datafile the path to an arff file
	 * @throws Exception
	 */
	public void loadModel(String datafile) throws Exception {
		try {
			model = (NaiveBayes) SerializationHelper.read("prosper.model");
		} catch (Exception e) {
			buildModel(datafile);
		}
	}
	
	/**
	 * Evaluates the performance of the classifier using data from a file.
	 * The input data is split into a training and a test group. The results 
	 * are printed to the screen.
	 * @param trainFile the path to an arff file
	 * @throws Exception
	 */
	public void evaluate(String trainFile) throws Exception {
		Instances trainData = loadData(trainFile);
		
		int numTestInstances = trainData.numInstances() / 5;
		Instances testData = new Instances(trainData, numTestInstances);
		
		for (int i = 0; i < numTestInstances; i++) {
			testData.add(trainData.remove(i));
		}
		
		Evaluation eval = new Evaluation(trainData);
		eval.evaluateModel(model, testData);
		System.out.println(eval.toSummaryString("\nResults\n======\n", false));
	}
	
	/**
	 * Predicts class labels for new data in an Instances object. 
	 * @param dataInstances Instances object with data to predict
	 * @return a HashMap<Integer, Double> containing the index of the predicted
	 * data and the predicted class label
	 * @throws Exception
	 */
	public HashMap<Integer, Double> predictInstances(Instances dataInstances) throws Exception {		
		dataInstances.setClassIndex(dataInstances.numAttributes() - 1);
		HashMap<Integer, Double> labeledInst = new HashMap<Integer, Double>();
		
		for (int i = 0; i < dataInstances.numInstances(); i++) {
			double classLabel = model.classifyInstance(dataInstances.instance(i));
			labeledInst.put(i, classLabel);
		}
		
		return labeledInst;
	}
}
