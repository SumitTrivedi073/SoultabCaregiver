package com.soultabcaregiver.activity.shopping.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShoppingCategoryModel {
	
	@SerializedName ("status_code") @Expose private Integer statusCode;
	
	@SerializedName ("status") @Expose private String status;
	
	@SerializedName ("message") @Expose private String message;
	
	@SerializedName ("response") @Expose private Response response;
	
	public Integer getStatusCode() {
		return statusCode;
	}
	
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Response getResponse() {
		return response;
	}
	
	public void setResponse(Response response) {
		this.response = response;
	}
	
	public class Response {
		
		@SerializedName ("product_category_data") @Expose private List<ProductCategoryDatum>
				productCategoryData = null;
		
		public List<ProductCategoryDatum> getProductCategoryData() {
			return productCategoryData;
		}
		
		public void setProductCategoryData(List<ProductCategoryDatum> productCategoryData) {
			this.productCategoryData = productCategoryData;
		}
		
		public class ProductCategoryDatum {
			
			@SerializedName ("id") @Expose private String id;
			
			@SerializedName ("name") @Expose private String name;
			
			@SerializedName ("category_icon") @Expose private String categoryIcon;
			
			@SerializedName ("web_url") @Expose private String webUrl;
			
			public String getId() {
				return id;
			}
			
			public void setId(String id) {
				this.id = id;
			}
			
			public String getName() {
				return name;
			}
			
			public void setName(String name) {
				this.name = name;
			}
			
			public String getCategoryIcon() {
				return categoryIcon;
			}
			
			public void setCategoryIcon(String categoryIcon) {
				this.categoryIcon = categoryIcon;
			}
			
			public String getWebUrl() {
				return webUrl;
			}
			
			public void setWebUrl(String webUrl) {
				this.webUrl = webUrl;
			}
			
		}
	}
}
