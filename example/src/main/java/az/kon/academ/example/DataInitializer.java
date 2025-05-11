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
                    new Product(null, "Laptop", "Electronics", 1200.0, 50),
                    new Product(null, "Smartphone", "Electronics", 800.0, 120),
                    new Product(null, "Desk Chair", "Furniture", 250.0, 30),
                    new Product(null, "Coffee Maker", "Home Appliances", 150.0, 75),
                    new Product(null, "Gaming Mouse", "Electronics", 75.0, 200),
                    new Product(null, "Bookshelf", "Furniture", 180.0, 40),
                    new Product(null, "Blender", "Home Appliances", 90.0, 60),
                    new Product(null, "External SSD", "Electronics", 100.0, 90),
                    new Product(null, "Dining Table", "Furniture", 400.0, 25),
                    new Product(null, "Toaster", "Home Appliances", 50.0, 110),
                    new Product(null, "Smartwatch", "Electronics", 300.0, 80)
            );

            productRepository.saveAll(products);
        }
    }
}
