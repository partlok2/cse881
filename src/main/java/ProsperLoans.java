import java.sql.*;
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

public class ProsperLoans{
	public ArrayList<Loan> aLoans = new ArrayList<Loan>();
	public ArrayList<Loan> bLoans = new ArrayList<Loan>();
	public ArrayList<Loan> cLoans = new ArrayList<Loan>();
	public ArrayList<Loan> dLoans = new ArrayList<Loan>();
	
	
	public String getHTML(){
		populateLoanData();
		
		String outputHTML = "<!DOCTYPE html><html><head><style>table {    font-family: arial, sans-serif;    border-collapse: collapse;    width: 100%;}td, th {    border: 1px solid #dddddd;    text-align: left;    padding: 8px;}tr:nth-child(even) {    background-color: #dddddd;}</style></head><body>";
		
		//Header
		outputHTML = outputHTML + "<h1 align=\"center\">Prosper Loan Default Classification: CSE 881</h1><hr>";
				
		//A Classification
		outputHTML = outputHTML + "<h2>Classification: A</h2>";
		outputHTML = outputHTML + "<table>";
		outputHTML = outputHTML + getHeaders();
		outputHTML = outputHTML + getData(aLoans);
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
				
		outputHTML = outputHTML + "</body></html>";
				
		return outputHTML;
	}
	
	private String getHeaders(){
		String headers = "<tr>";
		String[] fieldNames = {"Loan Number", "Loan Amount", "Default Odds"};
		
		for(int i = 0; i < fieldNames.length; i++){
			headers = headers + "<th>";
			headers = headers + fieldNames[i];
			headers = headers + "</th>";
		}
		
		return headers + "</tr>";
	}
	
	private String getData(ArrayList<Loan> list){
		String data = "<tr>";
		
		for(int i = 0; i < list.size(); i++){
			data = data + "<th>";
			data = data + list.get(i).id;
			data = data + "</th>";
			data = data + "<th>";
			data = data + "$" + list.get(i).loanAmount;
			data = data + "</th>";
			data = data + "<th>";
			data = data + list.get(i).defaultOdds + "%";
			data = data + "</th>";
		}
		
		return data + "</tr>";
	}
	
	private void populateLoanData(){
		//populate aLoans,bLoans etc... here.
		Loan testLoan = new Loan(1234, 10000, 98.2);
		aLoans.add(testLoan);
		bLoans.add(testLoan);
		cLoans.add(testLoan);
		dLoans.add(testLoan);
	}
}