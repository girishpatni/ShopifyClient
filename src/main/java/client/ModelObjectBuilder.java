package client;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Girish
 * 
 * This is a builder class
 * It creates List<Order> objects 
 *
 */
public class ModelObjectBuilder {
	
	public static List<Order> createOrderList(String response) throws ParseException {
		List<Order> orderList = new ArrayList<Order>();
		JsonObject ordeJsons = new JsonParser().parse(response.toString()).getAsJsonObject();
		JsonArray jarray = ordeJsons.getAsJsonArray("orders");
		for (int i = 0; i < jarray.size(); i++) {
			JsonObject order = jarray.get(i).getAsJsonObject();
			JsonObject customer = order.getAsJsonObject("customer");
			JsonArray lineItems = order.getAsJsonArray("line_items");
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
			orderList.add(createOrder(customerId, createdAt, totalPrice, itemList));
		}
		return orderList;
	}	
	

	public static LineItem createLineItem(String itemPrice, String itemId, String productId) {
		BigDecimal price = new BigDecimal(itemPrice);
		Long lineItemId = Long.parseLong(itemId);
		Long productIdNew = Long.parseLong(productId);
		LineItem item = new LineItem(lineItemId, price, productIdNew);
		return item;
	}

	public static Order createOrder(String customerId, String createdAt, String totalPrice,
			List<LineItem> itemList) throws ParseException {
		DateTimeFormatter formatter = DateTimeFormat.forPattern(IConstants.DATE_FORMAT);
		DateTime created = formatter.parseDateTime(createdAt);
		BigDecimal total = new BigDecimal(totalPrice);
		Long customerNum = Long.parseLong(customerId);
		Order order = new Order(customerNum, created, total, itemList);
		return order;
	}

}
