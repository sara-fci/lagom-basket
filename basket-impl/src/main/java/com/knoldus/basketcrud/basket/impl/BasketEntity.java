package com.knoldus.basketcrud.basket.impl;

import akka.Done;
import com.knoldus.basketcrud.basket.impl.commands.BasketCommand;
import com.knoldus.basketcrud.basket.impl.commands.BasketCommand.CreateBasket;
import com.knoldus.basketcrud.basket.impl.commands.BasketCommand.DeleteBasket;
import com.knoldus.basketcrud.basket.impl.commands.BasketCommand.UpdateBasket;
import com.knoldus.basketcrud.basket.impl.commands.BasketCommand.BasketCurrentState;
import com.knoldus.basketcrud.basket.impl.events.BasketEvent;
import com.knoldus.basketcrud.basket.impl.events.BasketEvent.BasketCreated;
import com.knoldus.basketcrud.basket.impl.events.BasketEvent.BasketDeleted;
import com.knoldus.basketcrud.basket.impl.events.BasketEvent.BasketUpdated;
import com.knoldus.basketcrud.basket.impl.states.BasketState;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by harmeet on 30/1/17.
 */
public class BasketEntity extends PersistentEntity<BasketCommand, BasketEvent, BasketState> {

    @Override
    public Behavior initialBehavior(Optional<BasketState> snapshotState) {

        // initial behaviour of basket
        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(
                BasketState.builder().basket(Optional.empty())
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setCommandHandler(CreateBasket.class, (cmd, ctx) ->
                ctx.thenPersist(BasketCreated.builder().basket(cmd.getBasket())
                        .entityId(entityId()).build(), evt -> ctx.reply(Done.getInstance()))
        );

        behaviorBuilder.setEventHandler(BasketCreated.class, evt ->
                BasketState.builder().basket(Optional.of(evt.getBasket()))
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setCommandHandler(UpdateBasket.class, (cmd, ctx) ->
                ctx.thenPersist(BasketUpdated.builder().basket(cmd.getBasket()).entityId(entityId()).build()
                        , evt -> ctx.reply(Done.getInstance()))
        );

        behaviorBuilder.setEventHandler(BasketUpdated.class, evt ->
                BasketState.builder().basket(Optional.of(evt.getBasket()))
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setCommandHandler(DeleteBasket.class, (cmd, ctx) ->
                ctx.thenPersist(BasketDeleted.builder().basket(cmd.getBasket()).entityId(entityId()).build(),
                        evt -> ctx.reply(cmd.getBasket()))
        );

        behaviorBuilder.setEventHandler(BasketDeleted.class, evt ->
                BasketState.builder().basket(Optional.empty())
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setReadOnlyCommandHandler(BasketCurrentState.class, (cmd, ctx) ->
                ctx.reply(state().getBasket())
        );

        return behaviorBuilder.build();
    }
}
