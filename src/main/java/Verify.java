import java.io.File;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.Loader;

public class Verify {
	public static void verify() throws Exception {
		Instances predictDataSet = null;
		
		ArffLoader trainLoader = new ArffLoader();
		trainLoader.setSource(new File("train.arff"));
		trainLoader.setRetrieval(Loader.BATCH);
		Instances dataset = trainLoader.getDataSet();
		
		Attribute trainAttribute = dataset.attribute(dataset.size() - 1);
		dataset.setClass(trainAttribute);
		
		Classifier classifier = (Classifier) SerializationHelper.read("prosper.model");
		Evaluation evaluation = new Evaluation(dataset);
		evaluation.evaluateModel(classifier, predictDataSet, new Object[] {});
		
		System.out.println(classifier);
		System.out.println(evaluation.toSummaryString());
	}
}
