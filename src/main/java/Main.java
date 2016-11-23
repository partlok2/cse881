import static javax.measure.unit.SI.KILOGRAM;
import javax.measure.quantity.Mass;
import org.jscience.physics.model.RelativisticModel;
import org.jscience.physics.amount.Amount;

import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import java.net.URI;
import java.net.URISyntaxException;

import static spark.Spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import weka.classifiers.Classifier;
import spark.ModelAndView;
import static spark.Spark.get;

import com.heroku.sdk.jdbc.DatabaseUrl;


public class Main {

  public static void main(String[] args) {

    port(Integer.valueOf(System.getenv("PORT")));
    staticFileLocation("/public");

    get("/hello", (req, res) -> {
      RelativisticModel.select();
      Amount<Mass> m = Amount.valueOf("12 GeV").to(KILOGRAM);
      return "E=mc^2: 12 GeV = " + m.toString();
    });

    get("/", (request, response) -> {
        ProsperLoans loans = new ProsperLoans();
        return loans.getHTML();
    });

    get("/db", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
      try {
        connection = DatabaseUrl.extract().getConnection();

        Statement stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
        stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
        ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

        ArrayList<String> output = new ArrayList<String>();
        while (rs.next()) {
          output.add( "Read from DB: " + rs.getTimestamp("tick"));
        }

        attributes.put("results", output);
        return new ModelAndView(attributes, "db.ftl");
      } catch (Exception e) {
        attributes.put("message", "There was an error: " + e);
        return new ModelAndView(attributes, "error.ftl");
      } finally {
        if (connection != null) try{connection.close();} catch(SQLException e){}
      }
    }, new FreeMarkerEngine());
	
	  /* This is basic Weka code. See the ProsperClassifier class
	  try {
		  ProsperClassifier pc = ProsperClassifier.getInstance();
		  pc.loadModel("prosper_training_data.arff");
		  
		  //pc.evaluate("prosper_training_data.arff");	
		  
		  // Call below function wih valid Instances object to predict instances
		  // Expected attributes, ordering, and type can be found in "prosper_training_data.arff"
		  // HashMap<Integer, Double> predictions = pc.predictInstances(new Instances());	  
	  } catch (Exception e) {
		  System.err.println("Error testing model");
	  }
	  */
	  
  }

}




