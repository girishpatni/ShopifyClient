package client;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import org.joda.time.DateTime;

/**
 * @author Girish
 * 
 * Bare Minimum representation of Shopify Order response
 *
 */
public class Order {
	private final DateTime createdAt;
	private final Long customerId;
	private List<LineItem> itemList;
	private BigDecimal totalPrice;

	public Order(Long customerId, DateTime createdAt, BigDecimal totalPrice,
			List<LineItem> itemList) throws ParseException {
		this.customerId = customerId;
		this.createdAt = createdAt;
		this.totalPrice = totalPrice;
		this.itemList = itemList;
	}
	
	public DateTime getCreatedAt() {
		return createdAt;
	}

	public List<LineItem> getItemList() {
		return itemList;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public Long getCustomerId() {
		return customerId;
	}

}
