package client;

import java.math.BigDecimal;
/**
 * @author Girish
 * 
 * Bare Minimum representation of Shopify LineItem object
 *
 */

public class LineItem {
	private final Long id;
	private BigDecimal price;
	/**
	 * 
	 */
	private final Long productId;

	public LineItem(Long id, BigDecimal price, Long productId) {
		this.id = id;
		this.price = price;
		this.productId = productId;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Long getProductId() {
		return productId;
	}

	public Long getId() {
		return id;
	}

}
