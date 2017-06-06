import java.util.List;

import org.apache.http.NameValuePair;
import org.junit.Test;

import com.picoto.http.HttpGetClient;


public class SimpleTest {

	@Test
	public void test1() {
		HttpGetClient client = new HttpGetClient("http://localhost") {

			@Override
			protected List<NameValuePair> processRequest(
					List<NameValuePair> params) {
				// TODO Auto-generated method stub
				return params;
			}
			
		};
		
		client.setAuthenticated(false);
		client.process();
		System.out.println(client.getResponse());
	}

}
