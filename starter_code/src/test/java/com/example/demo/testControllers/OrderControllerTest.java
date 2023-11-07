package com.example.demo.testControllers;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.example.demo.testControllers.CartControllerTest.getUser;
import static com.example.demo.testControllers.CartControllerTest.getItem;


public class OrderControllerTest {

    private OrderController orderController;

    private OrderRepository orderRepository = mock(OrderRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setUp () {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void testNotFoundUserNameWhenSubmit() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(getUser());
        Optional<UserOrder> userOrder = Optional.of(new UserOrder());
        when(orderRepository.findById(any())).thenReturn(userOrder);
        ResponseEntity<?> responseOder = orderController.submit("user2");

        Assertions.assertNotNull(responseOder);
        Assertions.assertEquals(404, responseOder.getStatusCodeValue());
    }

    @Test
    public void testSubmitOrderSuccess() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(getUser());
        Optional<UserOrder> userOrder = Optional.of(new UserOrder());
        when(orderRepository.findById(any())).thenReturn(userOrder);

        Item item = getItem();
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setTotal(new BigDecimal(10.0));
        cart.setItems((itemList));
        user.setCart(cart);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        ResponseEntity<?> responseOder = orderController.submit("userTest");

        Assertions.assertNotNull(responseOder);
        Assertions.assertEquals(200, responseOder.getStatusCodeValue());
    }

    @Test
    public void testNotFoundUserNameWhenGetOrderForUser() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(getUser());
        Optional<UserOrder> userOrder = Optional.of(new UserOrder());
        when(orderRepository.findById(any())).thenReturn(userOrder);
        ResponseEntity<List<UserOrder>> responseOder = orderController.getOrdersForUser("user2");

        Assertions.assertNotNull(responseOder);
        Assertions.assertEquals(404, responseOder.getStatusCodeValue());
    }

    @Test
    public void testGetOrderForUserSuccess() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(getUser());
        Optional<UserOrder> userOrder = Optional.of(new UserOrder());
        when(orderRepository.findById(any())).thenReturn(userOrder);
        ResponseEntity<List<UserOrder>> responseOder = orderController.getOrdersForUser("userTest");

        Assertions.assertNotNull(responseOder);
        Assertions.assertEquals(200, responseOder.getStatusCodeValue());
    }
}
