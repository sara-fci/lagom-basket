package com.knoldus.basketcrud.basket.api;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

import java.util.Optional;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;
import static com.lightbend.lagom.javadsl.api.transport.Method.*;

/**
 * Basket service to create new basket and add new item to the basket
 */
public interface BasketService extends Service {

    ServiceCall<NotUsed, Optional<Basket>> basket(String uuid);

    ServiceCall<Basket, Done> newBasket();

    ServiceCall<Basket, Done> updateBasket();

    ServiceCall<NotUsed, Basket> delete(String uuid);

    ServiceCall<NotUsed, Optional<Basket>> currentState(String uuid);

    @Override
    default Descriptor descriptor() {

        return named("basket").withCalls(
                restCall(GET, "/api/basket/:uuid", this::basket),
                restCall(POST, "/api/basket", this::newBasket),
                restCall(PUT, "/api/basket", this::updateBasket),
                restCall(DELETE, "/api/basket/:uuid", this::delete),
                restCall(GET, "/api/basket/current-state/:uuid", this::currentState)
        ).withAutoAcl(true);
    }
}
