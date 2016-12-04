import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import java.net.URI;
import java.net.URISyntaxException;

import static spark.Spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;

import com.heroku.sdk.jdbc.DatabaseUrl;



public class Loan{
	
	public int id;
	public int loanAmount;
	public int fico;
	public int monthlyIncome;
	public int monthlyDebt;
	
	public Loan(int d, int loanAmount, int fico, int monthlyIncome, int monthlyDebt){
		this.id = d;
		this.loanAmount = loanAmount;
		this.fico = fico * 10;
		this.monthlyIncome = monthlyIncome;
		this.monthlyDebt = monthlyDebt;
	}
}