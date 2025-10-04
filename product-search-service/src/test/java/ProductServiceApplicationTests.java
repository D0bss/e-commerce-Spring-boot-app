//import com.example.dto.ProductDTO;
//import com.example.model.Product;
//import com.example.repository.ProductRepository;
//import com.example.service.ProductService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class ProductServiceApplicationTests {
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @InjectMocks
//    private ProductService productService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // Test 1: Product Search by Name
//    @Test
//    public void testSearchByName() {
//        // Arrange
//        String searchName = "laptop";
//        List<Product> expectedProducts = Arrays.asList(
//                new Product("1", "Laptop A", "Electronics", 1200.00, 4.5, true, "typeA"),
//                new Product("2", "Laptop B", "Electronics", 800.00, 4.0, true, "typeB")
//        );
//        when(productRepository.findByNameContainingIgnoreCase(searchName)).thenReturn(expectedProducts);
//
//        // Act
//        List<Product> actualProducts = productService.searchByName(searchName);
//
//        // Assert
//        assertEquals(expectedProducts, actualProducts);
//    }
//
//    // Test 2: Product Search by Category
//    @Test
//    public void testSearchByCategory() {
//        // Arrange
//        String searchCategory = "electronics";
//        List<Product> expectedProducts = List.of(
//                new Product("1", "Laptop A", "Electronics", 1200.00, 4.5, true, "typeA")
//        );
//        when(productRepository.findByCategoryContainingIgnoreCase(searchCategory)).thenReturn(expectedProducts);
//
//        // Act
//        List<Product> actualProducts = productService.searchByCategory(searchCategory);
//
//        // Assert
//        assertEquals(expectedProducts, actualProducts);
//    }
//
//    // Test 3: Filtering by Price Range
//    @Test
//    public void testFilterByPriceRange() {
//        // Arrange
//        double minPrice = 500.0;
//        double maxPrice = 1500.0;
//        List<Product> expectedProducts = Arrays.asList(
//                new Product("1", "Laptop A", "Electronics", 1200.00, 4.5, true, "typeA"),
//                new Product("2", "Phone B", "Electronics", 800.00, 4.0, true, "typeB")
//        );
//        when(productRepository.findByPriceBetween(minPrice, maxPrice)).thenReturn(expectedProducts);
//
//        // Act
//        List<Product> actualProducts = productService.filterByPriceRange(minPrice, maxPrice);
//
//        // Assert
//        assertEquals(expectedProducts, actualProducts);
//    }
//
//    // Test 4: Filtering by Rating
//    @Test
//    public void testFilterByRating() {
//        // Arrange
//        double minRating = 4.0;
//        List<Product> expectedProducts = Arrays.asList(
//                new Product("1", "Laptop A", "Electronics", 1200.00, 4.5, true, "typeA"),
//                new Product("2", "Phone B", "Electronics", 800.00, 4.2, true, "typeB")
//        );
//        when(productRepository.findByRatingGreaterThanEqual(minRating)).thenReturn(expectedProducts);
//
//        // Act
//        List<Product> actualProducts = productService.filterByRating(minRating);
//
//        // Assert
//        assertEquals(expectedProducts, actualProducts);
//    }
//
//    // Test 5: Filtering by Availability
//    @Test
//    public void testFilterByAvailability() {
//        // Arrange
//        boolean available = true;
//        List<Product> expectedProducts = Arrays.asList(
//                new Product("1", "Laptop A", "Electronics", 1200.00, 4.5, true, "typeA"),
//                new Product("2", "Phone B", "Electronics", 800.00, 4.2, true, "typeB")
//        );
//        when(productRepository.findByAvailable(available)).thenReturn(expectedProducts);
//
//        // Act
//        List<Product> actualProducts = productService.filterByAvailability(available);
//
//        // Assert
//        assertEquals(expectedProducts, actualProducts);
//    }
//
//    // Test 6: Create a Product
//    @Test
//    public void testCreateProduct() {
//        // Arrange
//        Product newProduct = new Product(null, "Tablet", "Electronics", 300.00, 4.0, true, "basic");
//        Product savedProduct = new Product("123", "Tablet", "Electronics", 300.00, 4.0, true, "basic");
//        when(productRepository.save(newProduct)).thenReturn(savedProduct);
//
//        // Act
//        ProductDTO productDTO = new ProductDTO();
//        productDTO.setName("Tablet");
//        productDTO.setCategory("Electronics");
//        productDTO.setPrice(300.00);
//        productDTO.setRating(4.0);
//        productDTO.setAvailable(true);
//        productDTO.setType("basic");
//        Product actualProduct = productService.createProduct(productDTO);
//
//        // Assert
//        assertNotNull(actualProduct.getId());
//        assertEquals(savedProduct, actualProduct);
//    }
//
//    // Test 7: Update a Product
//    @Test
//    public void testUpdateProduct() {
//        // Arrange
//        String productId = "111";
//        Product existingProduct = new Product(productId, "Phone", "Electronics", 800.00, 3.9, true, "typeB");
//        Product updatedProduct = new Product(productId, "Smartphone", "Electronics", 1000.00, 4.5, true, "typeB");
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
//        when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
//
//        // Act
//
//        // Act
//        ProductDTO productDTO = new ProductDTO();
//        productDTO.setName("Smartphone");
//        productDTO.setCategory("Electronics");
//        productDTO.setPrice(1000.00);
//        productDTO.setRating(4.5);
//        productDTO.setAvailable(true);
//        productDTO.setType("typeB");
//        Product actualProduct = productService.updateProduct(productId , productDTO);
//
//        // Assert
//        assertEquals(updatedProduct, actualProduct);
//    }
//
//    // Test 8: Delete a Product
//    @Test
//    public void testDeleteProductById() {
//        // Arrange
//        String productId = "123";
//        doNothing().when(productRepository).deleteById(productId);
//
//        // Act
//        productService.deleteProduct(productId);
//
//        // Assert
//        verify(productRepository, times(1)).deleteById(productId);
//    }
//
//    // Test 9: Reflection Usage for Product Creation
//    @Test
//    public void testReflectionProductCreation() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        // Arrange
//        Constructor<Product> constructor = Product.class.getDeclaredConstructor(String.class, String.class, String.class, double.class, double.class, boolean.class, String.class);
//        Product product;
//
//        // Act
//        product = constructor.newInstance("999", "Reflected Product", "Tools", 99.99, 4.2, true, "typeR");
//
//        // Assert
//        assertNotNull(product);
//        assertEquals("999", product.getId());
//        assertEquals("Reflected Product", product.getName());
//    }
//}