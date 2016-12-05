import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.awt.List;
import java.net.URI;
import java.net.URISyntaxException;

import static spark.Spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;

import com.heroku.sdk.jdbc.DatabaseUrl;

import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import java.net.URI;
import java.net.URISyntaxException;

import static spark.Spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import weka.classifiers.Classifier;
import weka.core.Instances;
import spark.ModelAndView;
import static spark.Spark.get;

import com.heroku.sdk.jdbc.DatabaseUrl;

public class ProsperLoans{
	public ArrayList<Loan> aLoans = new ArrayList<Loan>();
	public ArrayList<Loan> aaLoans = new ArrayList<Loan>();
	public ArrayList<Loan> bLoans = new ArrayList<Loan>();
	public ArrayList<Loan> cLoans = new ArrayList<Loan>();
	public ArrayList<Loan> dLoans = new ArrayList<Loan>();
	public ArrayList<Loan> eLoans = new ArrayList<Loan>();
	public ArrayList<Loan> hrLoans = new ArrayList<Loan>();
	
	
	public String getHTML(HashMap<Integer, Double> classificationData, Instances prosperData){
		populateLoanData(classificationData, prosperData);
		
		String outputHTML = "<!DOCTYPE html><html><head><style>table {    font-family: arial, sans-serif;    border-collapse: collapse;    width: 100%;}td, th {    border: 1px solid #dddddd;    text-align: left;    padding: 8px;}tr:nth-child(even) {    background-color: #dddddd;}</style></head><body>";
		
		//Header
		outputHTML = outputHTML + "<h1 align=\"center\">Prosper Loan Default Classification: CSE 881</h1><hr>";
		long yourmilliseconds = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");    
		Date resultdate = new Date(yourmilliseconds);
		System.out.println(sdf.format(resultdate));
		outputHTML = outputHTML + "<h2 align=\"center\">Updated: " + sdf.format(resultdate) + "</h2>";
		outputHTML = outputHTML + "<h5 align=\"center\">All loans listed have been flagged as \"Not Likely to Default\" by the model</h5><hr>";
				
		//A Classification
		outputHTML = outputHTML + "<h2>Classification: A</h2>";
		outputHTML = outputHTML + "<table>";
		outputHTML = outputHTML + getHeaders();
		outputHTML = outputHTML + getData(aLoans);
		outputHTML = outputHTML + "</table>";
		
		//AA Classification
		outputHTML = outputHTML + "<h2>Classification: AA</h2>";
		outputHTML = outputHTML + "<table>";
		outputHTML = outputHTML + getHeaders();
		outputHTML = outputHTML + getData(aaLoans);
		outputHTML = outputHTML + "</table>";
		
		//B Classification
		outputHTML = outputHTML + "<h2>Classification: B</h2>";
		outputHTML = outputHTML + "<table>";
		outputHTML = outputHTML + getHeaders();
		outputHTML = outputHTML + getData(bLoans);
		outputHTML = outputHTML + "</table>";
		
		//C Classification
		outputHTML = outputHTML + "<h2>Classification: C</h2>";
		outputHTML = outputHTML + "<table>";
		outputHTML = outputHTML + getHeaders();
		outputHTML = outputHTML + getData(cLoans);
		outputHTML = outputHTML + "</table>";
		
		//D Classification
		outputHTML = outputHTML + "<h2>Classification: D</h2>";
		outputHTML = outputHTML + "<table>";
		outputHTML = outputHTML + getHeaders();
		outputHTML = outputHTML + getData(dLoans);
		outputHTML = outputHTML + "</table>";
		
		//E Classification
		outputHTML = outputHTML + "<h2>Classification: E</h2>";
		outputHTML = outputHTML + "<table>";
		outputHTML = outputHTML + getHeaders();
		outputHTML = outputHTML + getData(eLoans);
		outputHTML = outputHTML + "</table>";
				
		//HR Classification
		outputHTML = outputHTML + "<h2>Classification: HR</h2>";
		outputHTML = outputHTML + "<table>";
		outputHTML = outputHTML + getHeaders();
		outputHTML = outputHTML + getData(hrLoans);
		outputHTML = outputHTML + "</table>";
				
		outputHTML = outputHTML + "</body></html>";
				
		return outputHTML;
	}
	
	private String getHeaders(){
		String headers = "<tr>";
		String[] fieldNames = {"Loan Number", "Loan Amount", "Fico score", "Monthly Income", "Monthly Debt"};
		
		for(int i = 0; i < fieldNames.length; i++){
			headers = headers + "<th>";
			headers = headers + fieldNames[i];
			headers = headers + "</th>";
		}
		
		return headers + "</tr>";
	}
	
	private String getData(ArrayList<Loan> list){
		String data = "";
		
		for(int i = 0; i < list.size(); i++){
			data = data + "<tr>";
			data = data + "<th>";
			data = data + list.get(i).id;
			data = data + "</th>";
			data = data + "<th>";
			data = data + "$" + list.get(i).loanAmount;
			data = data + "</th>";
			data = data + "<th>";
			data = data + list.get(i).fico;
			data = data + "</th>";
			data = data + "<th>";
			data = data + "$" + list.get(i).monthlyIncome;
			data = data + "</th>";
			data = data + "<th>";
			data = data + "$" + list.get(i).monthlyDebt;
			data = data + "</th>";
			data = data + "</tr>";
		}
		
		return data ;
	}
	
	private void populateLoanData(HashMap<Integer, Double> classificationData, Instances prosperData){
		
		for (Map.Entry<Integer, Double> entry : classificationData.entrySet()) {
			
			
			//prosperData.get(entry.getKey()).value(1);
			Loan testLoan = new Loan((int)prosperData.get(entry.getKey()).value(37), 
					(int)prosperData.get(entry.getKey()).value(1), 
					(int)prosperData.get(entry.getKey()).value(7), 
					(int)prosperData.get(entry.getKey()).value(9), 
					(int)prosperData.get(entry.getKey()).value(14));

			if(entry.getValue() == 1.0){
				switch((int)prosperData.get(entry.getKey()).value(35)){
					case 0:
						aLoans.add(testLoan);
						break;
					case 1:
						aaLoans.add(testLoan);
						break;
					case 2:
						bLoans.add(testLoan);
						break;
					case 3:
						cLoans.add(testLoan);
						break;
					case 4:
						dLoans.add(testLoan);
						break;
					case 5:
						eLoans.add(testLoan);
						break;
					case 6:
						hrLoans.add(testLoan);
						break;
					
				}
			}
		}
		
		
		//populate aLoans,bLoans etc... here.
		//aLoans.add(testLoan);
		//bLoans.add(testLoan);
		//cLoans.add(testLoan);
		//dLoans.add(testLoan);
	}
}