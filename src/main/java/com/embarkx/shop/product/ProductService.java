package com.embarkx.shop.product;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.embarkx.shop.product.Product.Spec;

import org.springframework.stereotype.Service;

/**
 * In-memory product catalogue. To add a product, put its photo in static/images/ and add an
 * entry here.
 */
@Service
public class ProductService {

	private final Map<String, Product> products = Map.of(
			"1", new Product(
					"1",
					"Vale Structured Tote Handbag",
					"Maison Vale",
					List.of("Women", "Handbags"),
					3499,
					5999,
					4.3,
					1286,
					"A structured everyday tote in smooth vegan leather with a roomy main compartment, "
							+ "sturdy top handles and gold-tone hardware that goes from desk to dinner.",
					List.of(
							"Smooth vegan-leather exterior that wipes clean and holds its shape",
							"Roomy main compartment fits a 13-inch laptop, a water bottle and the day's essentials",
							"Zip pocket and two slip pockets inside keep keys and cards easy to find",
							"Reinforced top handles with a comfortable 20 cm drop",
							"Gold-tone metal feet protect the base when you set it down"),
					List.of(
							new Spec("Material", "Vegan leather (PU)"),
							new Spec("Lining", "Polyester twill"),
							new Spec("Closure", "Magnetic snap"),
							new Spec("Dimensions", "34 x 26 x 14 cm (W x H x D)"),
							new Spec("Handle drop", "20 cm"),
							new Spec("Weight", "780 g"),
							new Spec("Care", "Wipe with a soft, damp cloth"),
							new Spec("Country of origin", "India")),
					"/images/handbag.jpg"));

	public Optional<Product> findById(String id) {
		return Optional.ofNullable(this.products.get(id));
	}

}
