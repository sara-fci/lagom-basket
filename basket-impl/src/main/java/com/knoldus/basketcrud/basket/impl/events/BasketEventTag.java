package com.knoldus.basketcrud.basket.impl.events;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

/**
 * Created by harmeet on 31/1/17.
 */
public class BasketEventTag {

    public static final AggregateEventTag<BasketEvent> INSTANCE = AggregateEventTag.of(BasketEvent.class);
}
