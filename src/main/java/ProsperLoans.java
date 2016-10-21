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

public class ProsperLoans{
	/*List<Loan> aLoans = new List<Loan>();
	List<Loan> bLoans = new List<Loan>();
	List<Loan> cLoans = new List<Loan>();
	List<Loan> dLoans = new List<Loan>();*/
	
	public static String getHTML(){
		return "Hello World";
	}
}