package az.kon.academ.example;

import az.kon.academ.example.model.entity.Product;
import az.kon.academ.example.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            List<Product> products = Arrays.asList(
                    // Added manufacturer and status based on the complex filter JSON
                    new Product(null,1, "Laptop", "Electronics", 1200.0, 50, "AVAILABLE", "Sony"),
                    new Product(null,1, "Smartphone", "Electronics", 800.0, 120, "AVAILABLE", "Samsung"),
                    new Product(null,2, "Desk Chair", "Furniture", 250.0, 30, "OUT_OF_STOCK", "IKEA"),
                    new Product(null,3, "Coffee Maker", "Home Appliances", 150.0, 75, "AVAILABLE", "Philips"),
                    new Product(null,1, "Gaming Mouse", "Electronics", 75.0, 200, "AVAILABLE", "Logitech"),
                    new Product(null,3, "Bookshelf", "Furniture", 180.0, 40, "AVAILABLE", "IKEA"),
                    new Product(null,2, "Blender", "Home Appliances", 90.0, 60, "AVAILABLE", "Philips"),
                    new Product(null,4, "External SSD", "Electronics", 100.0, 90, "AVAILABLE", "Samsung"),
                    new Product(null,2, "Dining Table", "Furniture", 400.0, 25, "OUT_OF_STOCK", "IKEA"),
                    new Product(null, 3, "Toaster", "Home Appliances", 50.0, 110, "AVAILABLE", "Philips"),
                    new Product(null, 3, "Smartwatch", "Electronics", 300.0, 80, "AVAILABLE", "Sony")
            );

            productRepository.saveAll(products);
        }
    }
}
