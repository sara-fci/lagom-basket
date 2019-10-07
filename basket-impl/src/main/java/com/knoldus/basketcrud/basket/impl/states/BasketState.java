package com.knoldus.basketcrud.basket.impl.states;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.knoldus.basketcrud.basket.api.Basket;
import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

/**
 * Created by harmeet on 2/2/17.
 */
@Value
@Builder
@JsonDeserialize
public class BasketState implements CompressedJsonable {
    Optional<Basket> basket;
    String timestamp;
}
