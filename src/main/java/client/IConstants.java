package client;

public interface IConstants {
	public static final String BASE_URL = "https://100pure-demo.myshopify.com/admin/";
	public static final String LIMIT_PARAM = "limit";
	public static final String FIELD_PARAM = "field";
	public static final String LIMIT_VALUE = "id,line_items,customer,total_price,created_at";
	public static final int FIELD_VALUE = 100;
	public static final int START_PAGE = 1;
	public static final String AUTH_HEADER_KEY = "X-Shopify-Access-Token";
	public static final String AUTH_HEADER_VALUE = "b1ade8379e97603f3b0d92846e238ad8";
	public static final String DATE_FORAMT = "yyyy-MM-dd'T'HH:mm:ssZ";

}
