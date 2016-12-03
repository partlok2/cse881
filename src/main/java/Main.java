import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;


public class Main {

	private final static String[] attributeNames = new String[] {
	  "loan_origination_date",
      "listing_amount",
	  "estimated_return",
	  "estimated_loss_rate",
	  "lender_yield",
	  "listing_term",
	  "listing_monthly_payment",
	  "fico_score",
	  "prosper_score",
	  "stated_monthly_income",
	  "dti_wprosper_loan",
	  "months_employed", //Don't have
	  "prior_prosper_loans_active",
	  "prior_prosper_loans" ,
	  "monthly_debt",
	  "current_delinquencies",
	  "delinquencies_last7_years",
	  "first_recorded_credit_line",
	  "credit_lines_last7_years",
	  "inquiries_last6_months",
	  "amount_delinquent",
	  "current_credit_lines",
	  "bankcard_utilization",
	  "total_open_revolving_accounts",
	  "installment_balance",  //Don't have
	  "real_estate_balance", //Don't have
	  "revolving_balance", 
	  "real_estate_payment", //Don't have
	  "total_inquiries",
	  "total_trade_items",
	  "satisfactory_accounts",
	  "delinquencies_over30_days",
	  "delinquencies_over60_days",
	  "delinquencies_over90_days",
	  "is_homeowner",
	  "prosper_rating",
	  "label"};
	
	public static void main(String[] args) {
		ArrayList<Attribute> lAttributes = initializeAttributes();
		
		ProsperWrapper pw = new ProsperWrapper("friede80@gmail.com", "5t*ZCiolp*!&G28");
		String token = pw.getOAuthToken();
		List<JSONObject> currentListings = pw.getListings( token );
		  
		Instances lProsperData = buildInstances(lAttributes, currentListings);		  
		  
	      // This is basic Weka code. See the ProsperClassifier class
		  try {
			  ProsperClassifier pc = ProsperClassifier.getInstance();
			  pc.loadModel("prosper_training_data.arff");		  
			  pc.evaluate("prosper_training_data.arff");	
			  
			  // Call below function wih valid Instances object to predict instances
			  // Expected attributes, ordering, and type can be found in "prosper_training_data.arff"
			  
			  HashMap<Integer, Double> predictions = pc.predictInstances(lProsperData);	 
			  System.out.println( predictions.get(2).toString() );
		  } catch (Exception e) {
			  System.err.println("Error testing model");
		  }	  
	}

	private static ArrayList<Attribute> initializeAttributes()
	{
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for( int i = 0; i < attributeNames.length; i++ )
		{
			Attribute att;
			ArrayList<String> nominalVal = new ArrayList<String>();
			  switch( i )
			  {
			  	case 5: // Listing term		  		
			  		nominalVal.add("36");
			  		nominalVal.add("60");
			  		att = new Attribute( attributeNames[i], nominalVal );
			  		break;
			  	case 8: //Prosper Score
			  		nominalVal.add("1");
			  		nominalVal.add("2");
			  		nominalVal.add("3");
			  		nominalVal.add("4");
			  		nominalVal.add("5");
			  		nominalVal.add("6");
			  		nominalVal.add("7");
			  		nominalVal.add("8");
			  		nominalVal.add("9");
			  		nominalVal.add("10");
			  		nominalVal.add("11");
			  		nominalVal.add("12");
			  		att = new Attribute( attributeNames[i], nominalVal );
			  		break;
			  	case 34:
			  		nominalVal.add("False");
			  		nominalVal.add("True");
			  		att = new Attribute( attributeNames[i], nominalVal );
			  		break;		  		
			  	case 35:
			  		nominalVal.add("AA");
			  		nominalVal.add("A");
			  		nominalVal.add("B");
			  		nominalVal.add("C");
			  		nominalVal.add("D");
			  		nominalVal.add("E");
			  		nominalVal.add("HR");
			  		att = new Attribute( attributeNames[i], nominalVal );
			  		break;
			  	case 36:
			  		nominalVal.add("0");
			  		nominalVal.add("1");
			  		att = new Attribute( attributeNames[i], nominalVal );
			  		break;
			  	default:
			  		att = new Attribute( attributeNames[i] );		  	
			  }
			  attributes.add( att );
		}	
		return attributes;
	}
	
	private static Instances buildInstances(ArrayList<Attribute> aAttributes, List<JSONObject> aCurrentListings) 
	{
		Instances lProsperData = new Instances("all_data", aAttributes,0);
		
		for( JSONObject listing : aCurrentListings )
		{
			DenseInstance inst = new DenseInstance(37);
			  
			for( int i = 0; i < attributeNames.length; i++ )
			{
				double value;
				switch( i )
				{
					case 0: //Loan Origination Date -- Use creation date instead
						value = dateToDouble( listing.getString("listing_creation_date") );
				  		break;				  
				  	case 7: //FICO Score
				  		value = Double.parseDouble( listing.getString( attributeNames[i] ).substring(0, 2) );
				  		break;
				  	case 8: //Prosper Score
				  		value = listing.getInt( attributeNames[i] )- 1;
				  		break;
				  	case 17: //First Recorded Credit Line
				  		value = dateToDouble( listing.getString(attributeNames[i]));
				  		break;			  	
				  	case 34: //Is home owner
				  		value = listing.getBoolean( attributeNames[i] ) ? 1 : 0;
				  		break;
				  	case 35: //Prosper Rating
				  		switch(listing.getString( attributeNames[i] ))
				  		{
					  		case "A":
					  			value = 0;
					  			break;
					  		case "AA":
					  			value = 1;
					  			break;
					  		case "B":
					  			value = 2;
					  			break;
					  		case "C":
					  			value = 3;
					  			break;
					  		case "D":
					  			value = 4;
					  			break;
					  		case "E":
					  			value = 5;
					  			break;
					  		case "HR":
					  			value = 6;
					  			break;
					  		default:
					  			value = -1;
				  		}
				  		break;
				  	case 36: //Label
				  		value = 0; //?
				  		break;
				  	//Don't have the following attributes - Use 0
				  	case 11: //Months Employed
				  	case 24: //Installment Balance
				  	case 25: //Real Estate Balance
				  	case 27: //Real Estate Payment
				  		value = 0;
				  		break;			  		
				  	default:
				  		value = listing.getDouble( attributeNames[i] );
				  }
				  inst.setValue( aAttributes.get(i), value );
			  }
			  lProsperData.add( inst );		  
		  }
		return lProsperData;
	}	

	private static double dateToDouble( String aDate )
	{
		String[] lDate = aDate.split(" ")[0].split("-");
		int lYear = Integer.parseInt(lDate[0]);
		int lMonth = Integer.parseInt(lDate[1]);
		int lDay = Integer.parseInt(lDate[2]);
		int lDaysIntoYear = (lMonth-1)*30 + lDay; //Approximation
		return (lYear-1900)*365 + lDaysIntoYear;	
	}
}




