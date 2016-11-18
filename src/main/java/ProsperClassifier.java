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

public final class ProsperClassifier {
	private static ProsperClassifier prosperClassifier = null;
	private static Classifier model;
	
	private ProsperClassifier() {
		model = new NaiveBayes();
	}
	
	public static synchronized ProsperClassifier getInstance() {
		if (prosperClassifier == null)
			prosperClassifier = new ProsperClassifier();
		
		return prosperClassifier;
	}
	
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
	
	public void buildModel(String datafile) throws Exception {
		Instances dataset = loadData(datafile);
		
		model.buildClassifier(dataset);
			
		SerializationHelper.write("prosper.model", model);
		System.out.println("Saved trained model");
	}
	
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
