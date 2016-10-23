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
	public double loanAmount;
	public double defaultOdds;
	
	public Loan(int id, double loanAmount, double defaultOdds){
		this.id = id;
		this.loanAmount = loanAmount;
		this.defaultOdds = defaultOdds;
	}
}