package com.example.demo.testControllers;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.Optional;

public class CartControllerTest {

    private CartController cartController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void testAddCartWithUserNotFound() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(getUser());
        when(cartRepository.save(any())).thenReturn(new Cart());
        Optional<Item> itemOptional = Optional.of(new Item());
        itemOptional.get().setPrice(new BigDecimal(10.0));
        when(itemRepository.findById(any())).thenReturn(itemOptional);
        ModifyCartRequest modifyCart = new ModifyCartRequest();
        modifyCart.setItemId(1);
        modifyCart.setQuantity(2);
        modifyCart.setUsername("user2");
        ResponseEntity<Cart> response = cartController.addTocart(modifyCart);
        Assertions.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testAddCartWithItemNotFound() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(getUser());
        when(itemRepository.findById(1L)).thenReturn(Optional.of(getItem()));
        ModifyCartRequest modifyCart = new ModifyCartRequest();
        modifyCart.setItemId(2);
        modifyCart.setQuantity(2);
        modifyCart.setUsername("userTest");

        ResponseEntity<Cart> response = cartController.addTocart(modifyCart);
        Assertions.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testAddCartSuccess() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(getUser());
        when(cartRepository.save(any())).thenReturn(new Cart());
        Optional<Item> itemOptional = Optional.of(new Item());
        itemOptional.get().setPrice(new BigDecimal(20.0));
        when(itemRepository.findById(any())).thenReturn(itemOptional);
        ModifyCartRequest modifyCart = new ModifyCartRequest();
        modifyCart.setItemId(1);
        modifyCart.setQuantity(1);
        modifyCart.setUsername(user.getUsername());
        ResponseEntity<Cart> response = cartController.addTocart(modifyCart);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        Assertions.assertNotNull(cart);
        Assertions.assertEquals(20, cart.getTotal().intValue());
    }

    @Test
    public void testRemoveCartWithItemNotFound() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(getUser());
        when(itemRepository.findById(1L)).thenReturn(Optional.of(getItem()));
        ModifyCartRequest modifyCart = new ModifyCartRequest();
        modifyCart.setItemId(2);
        modifyCart.setQuantity(2);
        modifyCart.setUsername("userTest");

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCart);
        Assertions.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testRemoveCartWithUserNotFound() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(getUser());
        when(cartRepository.save(any())).thenReturn(new Cart());
        Optional<Item> itemOptional = Optional.of(new Item());
        itemOptional.get().setPrice(new BigDecimal(8.0));
        when(itemRepository.findById(any())).thenReturn(itemOptional);
        ModifyCartRequest modifyCart = new ModifyCartRequest();
        modifyCart.setItemId(1);
        modifyCart.setQuantity(2);
        modifyCart.setUsername("user2");

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCart);
        Assertions.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testRemoveCartSuccess() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(getUser());
        when(cartRepository.save(any())).thenReturn(new Cart());
        Optional<Item> itemOptional = Optional.of(new Item());
        itemOptional.get().setPrice(new BigDecimal(26.0));
        when(itemRepository.findById(any())).thenReturn(itemOptional);
        ModifyCartRequest modifyCart = new ModifyCartRequest();
        modifyCart.setItemId(2);
        modifyCart.setQuantity(2);
        modifyCart.setUsername("userTest");

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCart);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    public static User getUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("userTest");
        user.setPassword("password");
        user.setCart(new Cart());
        return user;
    }

    public static Item getItem() {
        Item item = new Item();
        item.setId(1L);
        item.setPrice(new BigDecimal(26.0));
        item.setDescription("testItem");
        return item;
    }

}
