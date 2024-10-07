package com.mycompany.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Gatherer;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    void exploreStreamGatherer() {
        Offer grandChildOffer = new Offer("GP1", List.of());
        Offer grandChildOffer2 = new Offer("GP1", List.of());
        Offer grandChildOffer3 = new Offer("GP2", List.of());
        Offer grandChildOffer4 = new Offer("GP3", List.of());
        Offer childOffer1 = new Offer("CP1", List.of(grandChildOffer, grandChildOffer4));
        Offer childOffer2 = new Offer("CP2", List.of(grandChildOffer2,grandChildOffer3));
        Offer offer = new Offer("P1", List.of(childOffer1, childOffer2));

        List<Offer> distinctGrandChildOffers = getAllDistinctGrandChildOffersByProductCode(offer);
        assertEquals(3, distinctGrandChildOffers.size());
    }

    static List<Offer> getAllDistinctGrandChildOffersByProductCode(Offer offer) {
        return offer.childOffers().stream()
                .flatMap(childOffer -> childOffer.childOffers().stream())
                .gather(distinctByProductCode())
                .toList();
    }

    public static Gatherer<Offer, List<Offer>, Offer> distinctByProductCode() {
        return Gatherer.ofSequential(
                // Initializer
                ArrayList::new,

                // Gatherer
                (state, element, downstream) -> {
                    if(hasProductWithSameProductCode(state, element)) {
                        return false;
                    }
                    state.add(element);
                    return true;
                },

                // Finisher
                (state, downstream) -> {
                    if (!state.isEmpty()) {
                        state.stream().forEach(downstream::push);
                    }
                });
    }

    private static boolean hasProductWithSameProductCode(List<Offer> state, Offer element) {
        return state.stream().anyMatch(offer -> offer.productCode().equals(element.productCode()));
    }
}

record Offer(String productCode, List<Offer> childOffers) {
}