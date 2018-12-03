package com.order.exchange;

import com.order.exchange.book.OrderBook;
import com.order.exchange.client.OrderClient;
import com.order.exchange.client.OrderClientInProcess;
import com.order.exchange.server.OrderServer;
import com.order.exchange.utils.AppProperties;
import com.order.exchange.model.Order;
import com.order.exchange.model.OrderPrice;
import com.order.exchange.model.OrderRequest;
import com.order.exchange.model.OrderSide;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OrderBookTest {

    private OrderServer server;
    private OrderClient clientFoo;
    private OrderClient clientBar;

    @Before
    public void setUp() {
        server = new OrderServer(new AppProperties());

        clientFoo = new OrderClientInProcess(server);
        clientBar = new OrderClientInProcess(server);

        clientBar.placeOrder(new OrderRequest("BAR-ORDER-1", "BAR", OrderSide.OFFER, "GOOGL", 1047.18, 1000000));
        clientBar.placeOrder(new OrderRequest("BAR-ORDER-2", "BAR", OrderSide.OFFER, "GOOGL", 1047.17, 1000000));
        clientBar.placeOrder(new OrderRequest("BAR-ORDER-3", "BAR", OrderSide.OFFER, "GOOGL", 1047.16, 1000000));
        clientBar.placeOrder(new OrderRequest("BAR-ORDER-4", "BAR", OrderSide.OFFER, "GOOGL", 1047.15, 1000000));
        clientBar.placeOrder(new OrderRequest("BAR-ORDER-5", "BAR", OrderSide.OFFER, "GOOGL", 1047.14, 1000000));

        clientFoo.placeOrder(new OrderRequest("FOO-ORDER-1", "FOO", OrderSide.BID, "GOOGL", 1047.12, 1000000));
        clientFoo.placeOrder(new OrderRequest("FOO-ORDER-2", "FOO", OrderSide.BID, "GOOGL", 1047.11, 1000000));
        clientFoo.placeOrder(new OrderRequest("FOO-ORDER-3", "FOO", OrderSide.BID, "GOOGL", 1047.10, 1000000));
        clientFoo.placeOrder(new OrderRequest("FOO-ORDER-4", "FOO", OrderSide.BID, "GOOGL", 1047.09, 1000000));
        clientFoo.placeOrder(new OrderRequest("FOO-ORDER-5", "FOO", OrderSide.BID, "GOOGL", 1047.08, 1000000));

    }

    @Test
    public void testMatchFullNewOrders() {

        clientBar.placeOrder(new OrderRequest("BAR-ORDER-6", "BAR", OrderSide.BID, "GOOGL", 1047.14, 1000000));
        clientBar.placeOrder(new OrderRequest("FOO-ORDER-6", "FOO", OrderSide.OFFER, "GOOGL", 1047.12, 1000000));

        OrderBook book = server.getBook("GOOGL");

        ConcurrentMap<OrderPrice, List> bids = book.getAllBids();
        ConcurrentMap<OrderPrice, List> offers = book.getAllOffers();

        assertTrue(bids.size() == 4);
        assertTrue(offers.size() == 4);

        List<Order>[] topOfBook = book.getTopOfBook();

        List<Order> bidTop = topOfBook[0];
        List<Order> offerTop = topOfBook[1];

        Order topBidOrder = bidTop.get(0);
        Order topOfferOrder = offerTop.get(0);

        // the bid side
        assertTrue(bidTop.size() == 1 && topBidOrder.getPrice().getPrice() == 1047.11 && topBidOrder.getSize() == 1000000 ) ;
        // the offer side
        assertTrue(offerTop.size() == 1 && topOfferOrder.getPrice().getPrice() == 1047.15 && topOfferOrder.getSize() == 1000000 ) ;

    }

    @Test
    public void testAddNewOrdersToExistingLevel() {

        clientBar.placeOrder(new OrderRequest("BAR-ORDER-6", "BAR", OrderSide.BID, "GOOGL", 1047.12, 500000));
        clientFoo.placeOrder(new OrderRequest("FOO-ORDER-6", "FOO", OrderSide.OFFER, "GOOGL", 1047.14, 500000));

        OrderBook book = server.getBook("GOOGL");

        ConcurrentMap<OrderPrice, List> bids = book.getAllBids();
        ConcurrentMap<OrderPrice, List> offers = book.getAllOffers();

        assertTrue(bids.size() == 5);
        assertTrue(offers.size() == 5);

        List<Order>[] topOfBook = book.getTopOfBook();

        List<Order> bidTop = topOfBook[0];
        List<Order> offerTop = topOfBook[1];

        Order topBidOrder = bidTop.get(0);
        Order topOfferOrder = offerTop.get(0);

        // the bid side
        assertTrue(bidTop.size() == 2 && topBidOrder.getPrice().getPrice() == 1047.12 &&  topOfferOrder.getSize() == 1000000) ;
        // the offer side
        assertTrue(offerTop.size() == 2 && topOfferOrder.getPrice().getPrice() == 1047.14 &&  topOfferOrder.getSize() == 1000000) ;
    }


    @Test
    public void testMatchNewOrderPartial() {

        clientBar.placeOrder(new OrderRequest("BAR-ORDER-6", "BAR", OrderSide.BID, "GOOGL", 1047.14, 500000));
        clientFoo.placeOrder(new OrderRequest("FOO-ORDER-6", "FOO", OrderSide.OFFER, "GOOGL", 1047.12, 500000));

        OrderBook book = server.getBook("GOOGL");

        ConcurrentMap<OrderPrice, List> bids = book.getAllBids();
        ConcurrentMap<OrderPrice, List> offers = book.getAllOffers();

        assertTrue(bids.size() == 5);
        assertTrue(offers.size() == 5);

        List<Order>[] topOfBook = book.getTopOfBook();

        List<Order> bidTop = topOfBook[0];
        List<Order> offerTop = topOfBook[1];

        Order topBidOrder = bidTop.get(0);
        Order topOfferOrder = offerTop.get(0);

        // the bid side
        assertTrue(bidTop.size() == 1 && topBidOrder.getPrice().getPrice() == 1047.12 &&  topBidOrder.getSize() == 500000);

        // the offer side
        assertTrue(offerTop.size() == 1 && topOfferOrder.getPrice().getPrice() == 1047.14 &&  topOfferOrder.getSize() == 500000);
    }


    @Test
    public void testMatchMultiplePartial() {

        clientBar.placeOrder(new OrderRequest("BAR-ORDER-6", "BAR", OrderSide.BID, "GOOGL", 1047.15, 500000));
        clientBar.placeOrder(new OrderRequest("BAR-ORDER-7", "BAR", OrderSide.BID, "GOOGL", 1047.15, 500000));
        clientBar.placeOrder(new OrderRequest("BAR-ORDER-8", "BAR", OrderSide.BID, "GOOGL", 1047.15, 500000));

        OrderBook book = server.getBook("GOOGL");

        ConcurrentMap<OrderPrice, List> bids = book.getAllBids();
        ConcurrentMap<OrderPrice, List> offers = book.getAllOffers();

        assertTrue(bids.size() == 5);
        assertTrue(offers.size() == 4);

        List<Order>[] topOfBook = book.getTopOfBook();

        List<Order> bidTop = topOfBook[0];
        List<Order> offerTop = topOfBook[1];

        Order topBidOrder = bidTop.get(0);
        Order topOfferOrder = offerTop.get(0);

        // the bid side
        assertTrue(bidTop.size() == 1 && topBidOrder.getPrice().getPrice() == 1047.12 &&  topBidOrder.getSize() == 1000000);
        // the offer side
        assertTrue(offerTop.size() == 1 && topOfferOrder.getPrice().getPrice() == 1047.15 &&  topOfferOrder.getSize() == 500000);
    }



    @Test
    public void testMatchingInMultipleThreads() {

        // unordered set so will execute in a different order each time
        HashSet<Thread> set = new HashSet<>();

        set.add(new Thread(() -> clientBar.placeOrder(new OrderRequest("BAR-ORDER-6", "BAR", OrderSide.BID, "GOOGL", 1047.15, 500000))));
        set.add(new Thread(() -> clientBar.placeOrder(new OrderRequest("BAR-ORDER-7", "BAR", OrderSide.BID, "GOOGL", 1047.15, 500000))));
        set.add(new Thread(() -> clientBar.placeOrder(new OrderRequest("BAR-ORDER-8", "BAR", OrderSide.BID, "GOOGL", 1047.15, 500000))));
        set.add(new Thread(() -> clientFoo.placeOrder(new OrderRequest("FOO-ORDER-9", "FOO", OrderSide.OFFER, "GOOGL", 1047.11, 500000))));
        set.add(new Thread(() -> clientFoo.placeOrder(new OrderRequest("FOO-ORDER-10", "FOO", OrderSide.OFFER, "GOOGL", 1047.11, 500000))));
        set.add(new Thread(() -> clientFoo.placeOrder(new OrderRequest("FOO-ORDER-11", "FOO", OrderSide.OFFER, "GOOGL", 1047.11, 500000))));

        for (Thread th : set) {
            th.start();
        }

        for (Thread th : set) {
            try {
                th.join();
            } catch (InterruptedException e) {
                fail();
            }
        }

        OrderBook book = server.getBook("GOOGL");

        ConcurrentMap<OrderPrice, List> bids = book.getAllBids();
        ConcurrentMap<OrderPrice, List> offers = book.getAllOffers();

        assertTrue(bids.size() == 4);
        assertTrue(offers.size() == 4);

        List<Order>[] topOfBook = book.getTopOfBook();

        List<Order> bidTop = topOfBook[0];
        List<Order> offerTop = topOfBook[1];

        Order topBidOrder = bidTop.get(0);
        Order topOfferOrder = offerTop.get(0);

        // the bid side
        assertTrue(bidTop.size() == 1 && topBidOrder.getPrice().getPrice() == 1047.11 ) ;
        // the offer side
        assertTrue(offerTop.size() == 1 && topOfferOrder.getPrice().getPrice() == 1047.15 ) ;
    }
}



