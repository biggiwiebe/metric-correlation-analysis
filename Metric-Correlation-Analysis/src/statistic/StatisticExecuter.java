package statistic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math.stat.correlation.SpearmansCorrelation;

public class StatisticExecuter {

	private static StatisticExecuter executer;
	public static String result_dir = "C:\\Users\\Biggi\\Documents\\strategie2\\";

	public static void main(String[] args) {
		File dataFile = new File(result_dir + "results" + File.separator + "ResultsOk.csv");
		executer = new StatisticExecuter();
		executer.calculateStatistics(dataFile);
	}

	private Correlation correlation;
	private NormalDistribution normalityTest;

	public void calculateStatistics(File dataFile) {
		normalityTest = new NormalDistribution();
		executer = new StatisticExecuter();
		correlation = new Correlation();

		String[] metricNames = executer.getMetricNames(dataFile);

		File folder = new File(result_dir + "Boxplotauswahl");
		File boxplot_result = new File(result_dir + "StatisticResults" + File.separator + "Boxplot.jpeg");

		new BoxAndWhiskerMetric("Box-and-Whisker Project's Metrics", folder, boxplot_result);

		double[][] d = correlation.createMatrix(dataFile, metricNames);
		RealMatrix pearson_matrix = new PearsonsCorrelation().computeCorrelationMatrix(d);
		File pearsonMatrixFile = new File(
				result_dir + "StatisticResults" + File.separator + "PearsonCorrelationMatrix.csv");
		correlation.storeMatrix(pearson_matrix, metricNames, pearsonMatrixFile);

		RealMatrix spearman_matrix = new SpearmansCorrelation().computeCorrelationMatrix(d);
		correlation.printMatrix(spearman_matrix, metricNames);
		File spearmanMatrixFile = new File(
				result_dir + "StatisticResults" + File.separator + "SpearmanCorrelationMatrix.csv");
		correlation.storeMatrix(spearman_matrix, metricNames, spearmanMatrixFile);

		double[][] metricValues = normalityTest.getValues(dataFile, metricNames);
		File normalityTestResult = new File(
				result_dir + "StatisticResults" + File.separator + "shapiroWilkTestAll.csv");
		normalityTest.testNormalDistribution(metricValues, metricNames, normalityTestResult);

		// Normality norm = new Normality(LOCpC);
		// norm.fullAnalysis();
	}

	public String[] getMetricNames(File dataFile) {
		String[] metricNames = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(dataFile));
			String line = reader.readLine();
			metricNames = line.substring(0, line.length()).split(",");
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] metricNames2 = Arrays.copyOfRange(metricNames, 2, metricNames.length);
		return metricNames2;
	}
}
