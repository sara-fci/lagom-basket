package com.knoldus.basketcrud.basket.impl.events;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.knoldus.basketcrud.basket.api.Basket;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Builder;
import lombok.Value;
import java.util.List;

/**
 * Created by harmeet on 30/1/17.
 */
public interface BasketEvent extends Jsonable, AggregateEvent<BasketEvent> {

    @Override
    default AggregateEventTagger<BasketEvent> aggregateTag() {
        return BasketEventTag.INSTANCE;
    }

    @Value
    @Builder
    @JsonDeserialize
    final class BasketCreated implements BasketEvent, CompressedJsonable {
        Basket basket;
        String entityUuid;
        // List<Role> roles;
    }

    @Value
    @Builder
    @JsonDeserialize
    final class BasketUpdated implements BasketEvent, CompressedJsonable {
        Basket basket;
        String entityUuid;
        // List<Role> roles;
    }

    @Value
    @Builder
    @JsonDeserialize
    final class BasketDeleted implements BasketEvent, CompressedJsonable {
        Basket basket;
        String entityUuid;
    }
}
