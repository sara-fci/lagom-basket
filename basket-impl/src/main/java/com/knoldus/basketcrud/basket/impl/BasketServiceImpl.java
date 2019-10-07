package com.knoldus.basketcrud.basket.impl;

import akka.Done;
import akka.NotUsed;
import com.knoldus.basketcrud.basket.api.Basket;
import com.knoldus.basketcrud.basket.api.BasketService;
import com.knoldus.basketcrud.basket.impl.commands.BasketCommand;
import com.knoldus.basketcrud.basket.impl.commands.BasketCommand.CreateBasket;
import com.knoldus.basketcrud.basket.impl.commands.BasketCommand.DeleteBasket;
import com.knoldus.basketcrud.basket.impl.commands.BasketCommand.UpdateBasket;
import com.knoldus.basketcrud.basket.impl.commands.BasketCommand.BasketCurrentState;
import com.knoldus.basketcrud.basket.impl.events.BasketEventProcessor;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Created by harmeet on 30/1/17.
 */
public class BasketServiceImpl implements BasketService {

    private final PersistentEntityRegistry persistentEntityRegistry;
    private final CassandraSession session;

    @Inject
    public BasketServiceImpl(final PersistentEntityRegistry registry, ReadSide readSide, CassandraSession session) {
        this.persistentEntityRegistry = registry;
        this.session = session;

        persistentEntityRegistry.register(BasketEntity.class);
        readSide.register(BasketEventProcessor.class);
    }

    @Override
    public ServiceCall<NotUsed, Optional<Basket>> basket(String uuid) {
        return request -> {
            CompletionStage<Optional<Basket>> basketFuture =
                    session.selectAll("SELECT * FROM baskets WHERE uuid = ?", uuid)
                            .thenApply(rows ->
                                    rows.stream()
                                            .map(row -> Basket.builder().uuid(row.getString("uuid"))
                                                    .userUuid(row.getString("userUuid")).subTotal(row.getInt("subTotal")).tax(row.getInt("tax")).total(row.getInt("total"))
                                                    .build()
                                            )
                                            .findFirst()
                            );
            return basketFuture;
        };
    }

    @Override
    public ServiceCall<Basket, Done> newBasket() {
        return basket -> {
            PersistentEntityRef<BasketCommand> ref = basketEntityRef(basket);
            return ref.ask(CreateBasket.builder().basket(basket).build());
        };
    }

    @Override
    public ServiceCall<Basket, Done> updateBasket() {
        return basket -> {
            PersistentEntityRef<BasketCommand> ref = basketEntityRef(basket);
            return ref.ask(UpdateBasket.builder().basket(basket).build());
        };
    }

    @Override
    public ServiceCall<NotUsed, Basket> delete(String uuid) {
        return request -> {
            Basket basket = Basket.builder().uuid(uuid).build();
            PersistentEntityRef<BasketCommand> ref = basketEntityRef(basket);
            return ref.ask(DeleteBasket.builder().basket(basket).build());
        };
    }

    @Override
    public ServiceCall<NotUsed, Optional<Basket>> currentState(String uuid) {
        return request -> {
            Basket basket = Basket.builder().uuid(uuid).build();
            PersistentEntityRef<BasketCommand> ref = basketEntityRef(basket);
            return ref.ask(new BasketCurrentState());
        };
    }

    private PersistentEntityRef<BasketCommand> basketEntityRef(Basket basket) {
        return persistentEntityRegistry.refFor(BasketEntity.class, basket.getUuid());
    }
}
