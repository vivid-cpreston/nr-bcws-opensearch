package ca.bc.gov.nrs.wfdm.wfdm_opensearch_indexing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;

public class OpenSearchRESTClient {
	private static String serviceName = "es";
	private static String region = "ca-central-1";
	private static String domainEndpoint = "add URL here";
	private static String index = "my-index";
	
	private static String id =String.valueOf(System.currentTimeMillis());

	static final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

	public String addIndexToOpenSearch(String extractedContent, String fileName) {

		RestHighLevelClient searchClient = searchClient(serviceName, region);
		String type = "_doc";
		
		Map<String, Object> document = new HashMap<>();
       // document.put("entities", extractedContent);
		document.put("key", fileName);
        document.put("text", extractedContent);
        

		// Form the indexing request, send it, and print the response
		IndexRequest request = new IndexRequest(index, type, id).source(document);
		IndexResponse response = null;
		try {
			response = searchClient.index(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Response:"+response.toString());
		return "Success";
	}

	// Adds the interceptor to the OpenSearch REST client
	public static RestHighLevelClient searchClient(String serviceName, String region) {
		AWS4Signer signer = new AWS4Signer();
		signer.setServiceName(serviceName);
		signer.setRegionName(region);
		HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer,
				credentialsProvider);
		return new RestHighLevelClient(RestClient.builder(HttpHost.create(domainEndpoint))
				.setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));
	}


}
