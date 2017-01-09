import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.xml.bind.DatatypeConverter;
 
class BTCChinaApiAuthentication{
 
	private static final String ACCESS_KEY = "YOUR_ACCESS_KEY";
	private static final String SECRET_KEY = "YOUR_SECRET_KEY";
	 
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	 
	public static String getSignature(String data,String key) throws Exception {
	 
		// get an hmac_sha1 key from the raw key bytes
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
		 
		// get an hmac_sha1 Mac instance and initialize with the signing key
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);
		 
		// compute the hmac on input data bytes
		byte[] rawHmac = mac.doFinal(data.getBytes());
		 
		return bytArrayToHex(rawHmac);
	}
 
 
	private static String bytArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder();
		for(byte b: a)
			sb.append(String.format("%02x", b&0xff));
		return sb.toString();
	}
 
	public static void main(String args[]) throws Exception{
	 
		String tonce = ""+(System.currentTimeMillis() * 1000);

		//查询账户信息
		String params = "tonce="+tonce.toString()+"&accesskey="+ACCESS_KEY+"&requestmethod=post&id=1&method=getAccountInfo&params=";

		//buyOrder2市价单//
//		String params = "tonce="+tonce.toString()+"&accesskey="+ACCESS_KEY+"&requestmethod=post&id=1&method=buyOrder2&params=,0.0010";
		
		//buyOrder2限价单
//		String params = "tonce="+tonce.toString()+"&accesskey="+ACCESS_KEY+"&requestmethod=post&id=1&method=buyOrder2&params=4,0.01";

        //cancelOrder
//		String params = "tonce="+tonce.toString()+"&accesskey="+ACCESS_KEY+"&requestmethod=post&id=1&method=cancelOrder&params=10000";

		String hash = getSignature(params, SECRET_KEY);
		 
		String url = "https://api.btcchina.com/api_trade_v1.php";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		String userpass = ACCESS_KEY + ":" + hash;
		String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());
		 
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
		con.setRequestProperty ("Authorization", basicAuth);
		 
		String postdata = "{\"method\": \"getAccountInfo\", \"params\": [], \"id\": 1}";

		//buyOrder2市价单//
//		String postdata = "{\"method\": \"buyOrder2\", \"params\": [null,\"0.0010\"], \"id\": 1}";
	
		//buyOrder2限价单
//		String postdata = "{\"method\": \"buyOrder2\", \"params\": [4,\"0.01\"], \"id\": 1}";
		
		//cancelOrder
//		String postdata = "{\"method\": \"cancelOrder\", \"params\": [10000], \"id\": 1}";
		 
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(postdata);
		wr.flush();
		wr.close();
		 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postdata);
		System.out.println("Response Code : " + responseCode);
		 
		BufferedReader in = new BufferedReader(
		       new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}

		in.close();
		 
		//print result
		System.out.println(response.toString());
	 
	}
}