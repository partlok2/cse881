import java.io.File;
import java.util.Enumeration;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVSaver;
import weka.core.converters.Loader;

public class Prediction {
	public static void predict() throws Exception {
		ArffLoader testLoader = new ArffLoader();
		testLoader.setSource(new File("test.arff"));
		testLoader.setRetrieval(Loader.BATCH);
		Instances dataset = testLoader.getDataSet();
		
		Attribute testAttribute = dataset.attribute(dataset.size() - 1);
		dataset.setClass(testAttribute);
		
		Classifier classifier = (Classifier) SerializationHelper.read("prosper.model");
		
		ArffLoader testLoader2 = new ArffLoader();
		testLoader2.setSource(new File("test.arff"));
		Instances dataset2 = testLoader2.getDataSet();
		Attribute testAttribute2 = dataset2.attribute(dataset2.size() - 1);
		dataset2.setClass(testAttribute2);
		
		Enumeration<Instance> testInstances = dataset.enumerateInstances();
		Enumeration<Instance> testInstances2 = dataset2.enumerateInstances();
		
		while (testInstances.hasMoreElements()) {
			Instance instance = (Instance) testInstances.nextElement();
			Instance instance2 = (Instance) testInstances2.nextElement();
			double classification = classifier.classifyInstance(instance);
			instance2.setClassValue(classification);
		}
		
		CSVSaver predictedCsvSaver = new CSVSaver();
		predictedCsvSaver.setFile(new File("predict.csv"));
		predictedCsvSaver.setInstances(dataset2);
		predictedCsvSaver.writeBatch();
		
		System.out.println("Prediction saved");
	}
}
