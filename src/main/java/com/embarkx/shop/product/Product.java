package com.embarkx.shop.product;

import java.util.List;
import java.util.Locale;

/**
 * A product shown on the product page. Prices are whole rupees.
 *
 * @param mrp      maximum retail price, shown struck through next to the price
 * @param imageUrl product photo shown on the page (files in static/images/)
 */
public record Product(
		String id,
		String name,
		String brand,
		List<String> categories,
		long price,
		long mrp,
		double rating,
		int reviewCount,
		String description,
		List<String> features,
		List<Spec> specs,
		String imageUrl) {

	public String formattedPrice() {
		return String.format(Locale.ENGLISH, "₹%,d", this.price);
	}

	public String formattedMrp() {
		return String.format(Locale.ENGLISH, "₹%,d", this.mrp);
	}

	public int discountPercent() {
		return (int) Math.round((this.mrp - this.price) * 100.0 / this.mrp);
	}

	/** One row of the specifications table. */
	public record Spec(String label, String value) {
	}

}
