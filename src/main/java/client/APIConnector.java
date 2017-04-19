package client;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Girish 
 * This class implements functionality of Connecting to the
 *         Shopify API and return List<Order> for client consumption
 *
 */
public class APIConnector {
	private List<Order> orderList;
	private WebTarget target;
	private Client client;

	public APIConnector() {
		orderList = new ArrayList<Order>();
	}

	public APIConnector buildRequest(String url) {
		this.client = ClientBuilder.newClient();
		WebTarget baseTarget = client.target(url);
		WebTarget orderTarget = baseTarget.path("orders.json").queryParam("status", "any").queryParam("limit", 100)
				.queryParam(IConstants.FIELD_PARAM, IConstants.FIELD_VALUE);
		this.target = orderTarget;
		return this;
	}

	public APIConnector processRequest() throws ParseException {
		int pageNo = IConstants.START_PAGE;
		int status = 200;
		while (status == 200) {
			WebTarget paginatedTarget = this.target.queryParam("page", pageNo);
			pageNo++;
			Invocation.Builder invocationBuilder = paginatedTarget.request(MediaType.APPLICATION_JSON);
			invocationBuilder.header(IConstants.AUTH_HEADER_KEY, IConstants.AUTH_HEADER_VALUE);
			Response response = invocationBuilder.get();
			String responseStr = response.readEntity(String.class);
			status = response.getStatus();
			if (isEOF(responseStr) || status != 200) {
				break;
			} else {
				List<Order> orders = ModelObjectBuilder.createOrderList(responseStr);
				orderList.addAll(orders);
				pageNo++;
			}
		}
		client.close();
		return this;
	}

	private boolean isEOF(String response) {
		JsonObject jObject = new JsonParser().parse(response.toString()).getAsJsonObject();
		JsonArray jarray = jObject.getAsJsonArray("orders");
		if (jarray.size() == 0)
			return true;
		return false;
	}

	public List<Order> getOrderFromResponse() {
		return orderList;
	}
}
