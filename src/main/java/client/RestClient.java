package client;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.joda.time.DateTime;

/**
 * @author Girish
 * 
 * Rest Client to Consume data from Shopify API
 *
 */
public class RestClient {

	public static void main(String[] args) {
		try {
			List<Order> orderList = new APIConnector().buildRequest(IConstants.BASE_URL).processRequest().getOrderFromResponse();
			RestClient client = new RestClient();
			System.out.println("Number of Orders:  " + client.getNumberOfOrders(orderList));
			System.out.println("Number of customers:  " + client.getNumberOfCustomers(orderList));
			System.out.println("Median Order value " + client.getMedianOrderValue(orderList));
			client.printMostAndLeastOrderedItem(orderList);
			System.out.println(
					"Shortest Interval Difference In Millisecond :" + client.getShortestIntervalDiff(orderList));
		} catch (Exception e) {
			System.out.println("Unable To Proceed.. Quitting");
		}

	}

	private int getNumberOfOrders(List<Order> orderList) {
		return orderList.size();
	}

	private int getNumberOfCustomers(List<Order> orderList) {
		HashSet<Long> customersIdSet = new HashSet<Long>();
		for (Order order : orderList) {
			customersIdSet.add(order.getCustomerId());
		}
		return customersIdSet.size();
	}

	private BigDecimal getMedianOrderValue(List<Order> orderList) {
		int size = orderList.size();
		BigDecimal median = new BigDecimal(0);
		if (size < 1)
			return median;
		/* If number of Orders is Even*/
		if ((size & 1) == 0) {
			median = orderList.get((size - 1) / 2).getTotalPrice().add(orderList.get(size / 2).getTotalPrice());
		} else {
			median = orderList.get(size / 2).getTotalPrice();
		}
		BigDecimal divisor = new BigDecimal(2);
		return median.divide(divisor);
	}

	/**
	 * @param orderList
	 */
	private void printMostAndLeastOrderedItem(List<Order> orderList) {
		HashMap<Long, Integer> map = new HashMap<Long, Integer>();
		int maxCount = Integer.MIN_VALUE;
		int minCount = Integer.MAX_VALUE;
		Long maxItemId = 0l;
		Long minItemId = 0l;
		for (Order order : orderList) {
			List<LineItem> lineItemList = order.getItemList();
			for (LineItem item : lineItemList) {
				// map.put(item.productId, map.getOrDefault(item.productId,
				// 0)+1);
				Long productId = item.getProductId();
				if (map.containsKey(productId)) {
					int value = map.get(productId);
					map.put(productId, value + 1);
				} else {
					map.put(productId, 1);
				}
			}
		}
		for (Map.Entry<Long, Integer> entry : map.entrySet()) {
			if (entry.getValue() > maxCount) {
				maxCount = entry.getValue();
				maxItemId = entry.getKey();
			}
			if (entry.getValue() < minCount) {
				minCount = entry.getValue();
				minItemId = entry.getKey();
			}
		}
		System.out.println("Item Ordered Most: " + maxItemId);
		System.out.println("Item Ordered Least: " + minItemId);
	}

	private long getShortestIntervalDiff(List<Order> orderList) throws ParseException {
		Map<Long, PriorityQueue<DateTime>> map = new HashMap<Long, PriorityQueue<DateTime>>();
		for (Order order : orderList) {
			Long customerId = order.getCustomerId();
			DateTime createAt = order.getCreatedAt();
			if (map.containsKey(customerId)) {
				map.get(customerId).add(createAt);
			} else {
				PriorityQueue<DateTime> listOfOrderTime = new PriorityQueue<DateTime>();
				listOfOrderTime.add(createAt);
				map.put(customerId, listOfOrderTime);
			}
		}
		long shortestIntervalDiff = Long.MAX_VALUE;
		for (Map.Entry<Long, PriorityQueue<DateTime>> entry : map.entrySet()) {
			PriorityQueue<DateTime> listOfOrderDates = entry.getValue();
			int queueSize = listOfOrderDates.size();
			if (queueSize > 1) {
				DateTime last = listOfOrderDates.poll();
				for (int i = 1; i < queueSize; i++) {
					DateTime current = listOfOrderDates.poll();
					long diff = current.getMillis() - last.getMillis();
					if (diff < shortestIntervalDiff) {
						shortestIntervalDiff = diff;
					}
					last = current;
				}
			}
		}
		return shortestIntervalDiff == Long.MAX_VALUE ? -1 : shortestIntervalDiff;
	}
}