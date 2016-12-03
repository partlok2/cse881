import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject; 


public class ProsperWrapper {
	private static final String CLIENTID = "70fe7b90663646a59c5f31e5673c288a"; 
	private static final String CLIENTSECRET = "480898530d054d518c283cded8315c32";
	private static final int PAGINATION_LIMIT = 50;
	
	private String mUserID;
	private String mPass;
	  
	public ProsperWrapper( String aUserID, String aPassword )
	{
		mUserID = aUserID;
		mPass = aPassword;		
		return;
	}
	
	public List<JSONObject> getListings( String aOAuthToken )
	{
		List<JSONObject> lResults = new ArrayList<JSONObject>();
		int lResultCount = 0;
		int lOffset = 0;
		
		try 
		{
			do
			{
				URI lURI = new URIBuilder().setScheme("https")
										  .setHost("api.prosper.com/v1/")
										  .setPath("search/listings/")										  
										  .setParameter("offset", Integer.toString( lOffset ))
										  .setParameter("limit", Integer.toString( PAGINATION_LIMIT ) )	
										  .build();			
				HttpGet lHTTPGet = new HttpGet( lURI );
				lHTTPGet.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + aOAuthToken);
				lHTTPGet.addHeader(HttpHeaders.ACCEPT, "application/json" );
				
				CloseableHttpClient lHTTPClient = HttpClients.createDefault();
				JSONObject lResponse = lHTTPClient.execute( lHTTPGet, responseToJSON );
				
				JSONArray lJSONResults = lResponse.getJSONArray("result");
				for(int i = 0; i < lJSONResults.length(); i++ )
				{
					lResults.add( lJSONResults.getJSONObject(i) );
				}
				
				lResultCount = lResponse.getInt("result_count");
				lOffset += 50;	
			} while( lResultCount == PAGINATION_LIMIT );	
			
		} catch (Exception e) {
			// Too bad
			e.printStackTrace();
		}
		
		return lResults;		
	}
	
	public String getOAuthToken()
	{
		String oAuthToken = "";
		
		try {
			URI uri = new URIBuilder().setScheme("https")
									  .setHost("api.prosper.com/v1")
									  .setPath("/security/oauth/token")
									  .build();
				  
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost( uri );
			
				  
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("grant_type", "password"));
			params.add(new BasicNameValuePair("client_id", CLIENTID));
			params.add(new BasicNameValuePair("client_secret", CLIENTSECRET));
			params.add(new BasicNameValuePair("username", mUserID));
			params.add(new BasicNameValuePair("password", mPass));
			httppost.setEntity( new UrlEncodedFormEntity( params, "UTF-8" ) );	
			
			JSONObject response = httpclient.execute( httppost, responseToJSON );
			oAuthToken = response.getString("access_token");
		} catch (Exception e) {
			// I hate exceptions
			e.printStackTrace();
		}	
		
		return oAuthToken;
	}

	private ResponseHandler<JSONObject> responseToJSON = new ResponseHandler<JSONObject>() {
	
	    @Override
	    public JSONObject handleResponse( final HttpResponse response ) throws IOException {
	        StatusLine statusLine = response.getStatusLine();
	        HttpEntity entity = response.getEntity();
	        if (statusLine.getStatusCode() >= 300) {
	            throw new HttpResponseException(
	                    statusLine.getStatusCode(),
	                    statusLine.getReasonPhrase());
	        }
	        if (entity == null) {
	            throw new ClientProtocolException("Response contains no content");
	        }
	        String tmp = EntityUtils.toString(entity);
	        return new JSONObject( tmp );
	    }
	};
}
