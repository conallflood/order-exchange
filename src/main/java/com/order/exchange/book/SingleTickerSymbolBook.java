package com.order.exchange.book;

import com.order.exchange.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;

public class SingleTickerSymbolBook implements OrderBook {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleTickerSymbolBook.class);

    private ConcurrentMap<OrderPrice, List> allBidsAtPrice;
    private ConcurrentMap<OrderPrice, List> allOffersAtPrice;

    // TODO - For the moment use single ReentrantLock lock for executing the matching need to think about make this more fine grained
    private ReentrantLock lock = new ReentrantLock();

    /**
     * @param tickerSymbol Constucts a SingleTickerSymbolBook
     */
    SingleTickerSymbolBook(String tickerSymbol) {

        LOGGER.info("Initialising SingleTickerSymbolBook for " + tickerSymbol);

        allBidsAtPrice = new ConcurrentSkipListMap<>(new OrderPriceComparator(OrderSide.BID));
        allOffersAtPrice = new ConcurrentSkipListMap<>(new OrderPriceComparator(OrderSide.OFFER));
    }

    /**
     * @param newOrder submits an order to the book and kicks off matching if needed
     */
    void submitOrder(Order newOrder) {

        // TODO - Need to think about making this lock more fine grained to improve performance
        try {
            lock.lock();

            OrderSide side = newOrder.getSide();

            ConcurrentMap<OrderPrice, List> whichMap = side.equals(OrderSide.BID) ? allBidsAtPrice : allOffersAtPrice;

            whichMap.compute(newOrder.getPrice(), (orderPrice, orderLiquidityAtPrice) -> {
                if (orderLiquidityAtPrice == null) {
                    return new ArrayList<>(Arrays.asList(newOrder));
                } else {
                    orderLiquidityAtPrice.add(newOrder);
                }
                return orderLiquidityAtPrice;
            });

            LOGGER.info("Order Placed " + newOrder);


            List<Order> bidlist = checkForMatches(newOrder, OrderSide.BID);
            List<Order> offerList = checkForMatches(newOrder, OrderSide.OFFER);

            if (!bidlist.isEmpty() && !offerList.isEmpty()) {
                executeMatchesOnSide(newOrder.getSize(), bidlist, offerList);
            }

        } finally {
            lock.unlock();
        }
    }

    /**
     * @param newOrder    - the newly placed order
     * @param sidetoCheck the side BID or OFFER
     * @return a list of possible matches from one side of the book
     */
    private List<Order> checkForMatches(Order newOrder, OrderSide sidetoCheck) {

        ConcurrentMap<OrderPrice, List> map = OrderSide.BID.equals(sidetoCheck) ? allBidsAtPrice : allOffersAtPrice;

        List<Order> list = new ArrayList<>();
        for (OrderPrice price : map.keySet()) {

            if (OrderSide.BID.equals(sidetoCheck) ?
                    price.getPrice() >= newOrder.getPrice().getPrice() :
                    price.getPrice() <= newOrder.getPrice().getPrice()) {

                list.addAll(map.get(price));
            } else {
                break;
            }
        }
        return list;
    }

    private void executeMatchesOnSide(double amountToMatch, List<Order> bids, List<Order> offers) {

        executeMatchesOnSide(amountToMatch, bids);
        executeMatchesOnSide(amountToMatch, offers);
    }

    private void executeMatchesOnSide(double amountToMatch, List<Order> orders) {

        double alreadyMatched = 0;
        for (Order order : orders) {

            if (order.getSize() <= amountToMatch - alreadyMatched) {
                LOGGER.info("Order Matched " + order);
                removeOrderFromBook(order);
                alreadyMatched += order.getSize();
            } else {
                LOGGER.info("Order Partially Matched " + order);
                order.setSize(amountToMatch - alreadyMatched);
                alreadyMatched += order.getSize();
            }
            if (alreadyMatched == amountToMatch) {
                break;
            }
        }
    }

    private void removeOrderFromBook(Order order) {

        if (order.getSide().equals(OrderSide.BID)) {
            List queue = allBidsAtPrice.get(order.getPrice());
            if (queue != null) {
                if (queue.size() == 1) {
                    allBidsAtPrice.remove(order.getPrice());
                } else {
                    queue.remove(order);
                }
            }
        } else {
            List queue = allOffersAtPrice.get(order.getPrice());
            if (queue != null) {
                if (queue.size() == 1) {
                    allOffersAtPrice.remove(order.getPrice());
                } else {
                    queue.remove(order);
                }
            }
        }
    }

    @Override
    public ConcurrentMap<OrderPrice, List> getAllBids() {
        return allBidsAtPrice;
    }

    @Override
    public ConcurrentMap<OrderPrice, List> getAllOffers() {
        return allOffersAtPrice;
    }

    @Override
    public List[] getTopOfBook() {

        return new List[]{allBidsAtPrice.entrySet().iterator().next().getValue(),
                allOffersAtPrice.entrySet().iterator().next().getValue()};
    }
}