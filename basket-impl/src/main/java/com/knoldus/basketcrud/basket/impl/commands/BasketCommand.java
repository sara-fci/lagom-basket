package com.knoldus.basketcrud.basket.impl.commands;

import akka.Done;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.knoldus.basketcrud.basket.api.Basket;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Builder;
import lombok.Value;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

/**
 *
 */
public interface BasketCommand extends Jsonable {

    @Value
    @Builder
    @JsonDeserialize
    final class CreateBasket implements BasketCommand, PersistentEntity.ReplyType<Done> {
        Basket basket;
    }

    @Value
    @Builder
    @JsonDeserialize
    final class UpdateBasket implements BasketCommand, PersistentEntity.ReplyType<Done> {
        Basket basket;
    }

    @Value
    @Builder
    @JsonDeserialize
    final class DeleteBasket implements BasketCommand, PersistentEntity.ReplyType<Basket> {
        Basket basket;
    }

    @Immutable
    @JsonDeserialize
    final class BasketCurrentState implements BasketCommand, PersistentEntity.ReplyType<Optional<Basket>> {}
}
