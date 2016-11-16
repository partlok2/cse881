import java.io.File;
import java.io.FileNotFoundException;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.Loader;

public class ModelTrainer {
	
	//TODO: need constructor
	private static Instances loadData(String filename) throws Exception {
		Instances dataset = null;
		
		try {
			ArffLoader arffLoader = new ArffLoader();
			arffLoader.setSource(new File(filename));
			arffLoader.setRetrieval(Loader.BATCH);
			
			dataset = arffLoader.getDataSet();
		} catch(FileNotFoundException e) {
			System.err.println("Could not find file: " + filename);
		}
		
		return dataset;
	}
	
	public static Classifier buildModel(String datafile, String classifierName) throws Exception{
		Instances dataset = loadData(datafile);
		Attribute trainAttribute = dataset.attribute(dataset.size() - 1);
		dataset.setClass(trainAttribute);
		
		Classifier classifier = null;
		
		if (classifierName == "NaiveBayes") {
			classifier = new NaiveBayes();
			classifier.buildClassifier(dataset);
			
			SerializationHelper.write("prosper.model", classifier);
			System.out.println("Saved trained model");
		}
		
		return classifier;
	}
}
