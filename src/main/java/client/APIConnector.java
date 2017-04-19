package client;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Ghost
 *
 */
public class APIConnector {
	private List<Order> orderList;

	public APIConnector() {
		orderList = new ArrayList<Order>();
	}

	public List<Order> getResponseFromServer() throws ParseException {
		Client client = ClientBuilder.newClient();
		WebTarget baseTarget = client.target(IConstants.BASE_URL);
		WebTarget orderTarget = baseTarget.path("orders.json").queryParam("status", "any").queryParam("limit", 100)
				.queryParam(IConstants.FIELD_PARAM, IConstants.FIELD_VALUE);
		int pageNo = IConstants.START_PAGE;
		int status = 200;
		while (status == 200) {
			WebTarget paginatedTarget = orderTarget.queryParam("page", pageNo);
			pageNo++;
			Invocation.Builder invocationBuilder = paginatedTarget.request(MediaType.APPLICATION_JSON);
			invocationBuilder.header(IConstants.AUTH_HEADER_KEY, IConstants.AUTH_HEADER_VALUE);
			Response response = invocationBuilder.get();
			String responseStr = response.readEntity(String.class);
			status = response.getStatus();
			if (isEOF(responseStr) || status != 200) {
				break;
			} else {
				List<Order> orders = parseJsonData(responseStr);
				orderList.addAll(orders);
				pageNo++;
			}
		}
		client.close();
		return orderList;
	}

	private boolean isEOF(String response) {
		JsonObject jObject = new JsonParser().parse(response.toString()).getAsJsonObject();
		JsonArray jarray = jObject.getAsJsonArray("orders");
		if (jarray.size() == 0)
			return true;
		return false;
	}

	private List<Order> parseJsonData(String response) throws ParseException {
		List<Order> orderList = new ArrayList<Order>();
		JsonObject ordeJsons = new JsonParser().parse(response.toString()).getAsJsonObject();
		JsonArray jarray = ordeJsons.getAsJsonArray("orders");
		for (int i = 0; i < jarray.size(); i++) {
			JsonObject order = jarray.get(i).getAsJsonObject();
			JsonObject customer = order.getAsJsonObject("customer");
			JsonArray lineItems = order.getAsJsonArray("line_items");
			String orderId = order.get("id").getAsString();
			String customerId = customer.get("id").getAsString();
			String createdAt = order.get("created_at").getAsString();
			String totalPrice = order.get("total_price").getAsString();
			List<LineItem> itemList = new ArrayList<LineItem>(lineItems.size());
			for (int j = 0; j < lineItems.size(); j++) {
				JsonObject lineItemObject = lineItems.get(j).getAsJsonObject();
				String itemPrice = lineItemObject.get("price").getAsString();
				String itemId = lineItemObject.get("id").getAsString();
				String productId = lineItemObject.get("product_id").getAsString();
				itemList.add(createLineItem(itemPrice, itemId, productId));
			}
			orderList.add(createOrder(orderId, customerId, createdAt, totalPrice, itemList));
		}
		return orderList;
	}

	private LineItem createLineItem(String itemPrice, String itemId, String productId) {
		BigDecimal price = new BigDecimal(itemPrice);
		Long lineItemId = Long.parseLong(itemId);
		Long productIdNew = Long.parseLong(productId);
		LineItem item = new LineItem(lineItemId, price, productIdNew);
		return item;
	}

	private Order createOrder(String orderId, String customerId, String createdAt, String totalPrice,
			List<LineItem> itemList) throws ParseException {
		DateTimeFormatter formatter = DateTimeFormat.forPattern(IConstants.DATE_FORAMT);
		DateTime created = formatter.parseDateTime(createdAt);
		BigDecimal total = new BigDecimal(totalPrice);
		Long orderNum = Long.parseLong(orderId);
		Long customerNum = Long.parseLong(customerId);
		Order order = new Order(orderNum, customerNum, created, total, itemList);
		return order;
	}
}
