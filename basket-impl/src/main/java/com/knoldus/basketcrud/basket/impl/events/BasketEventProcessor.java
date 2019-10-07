package com.knoldus.basketcrud.basket.impl.events;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.knoldus.basketcrud.basket.impl.events.BasketEvent.BasketCreated;
import com.knoldus.basketcrud.basket.impl.events.BasketEvent.BasketDeleted;
import com.knoldus.basketcrud.basket.impl.events.BasketEvent.BasketUpdated;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Created by harmeet on 31/1/17.
 */
public class BasketEventProcessor extends ReadSideProcessor<BasketEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasketEventProcessor.class);

    private final CassandraSession session;
    private final CassandraReadSide readSide;

    private PreparedStatement writeBaskets;
    private PreparedStatement deleteBaskets;
    private PreparedStatement writeItem;
	
    @Inject
    public BasketEventProcessor(final CassandraSession session, final CassandraReadSide readSide) {
        this.session = session;
        this.readSide = readSide;
    }

    @Override
    public PSequence<AggregateEventTag<BasketEvent>> aggregateTags() {
        LOGGER.info(" aggregateTags method ... ");
        return TreePVector.singleton(BasketEventTag.INSTANCE);
    }

    @Override
    public ReadSideHandler<BasketEvent> buildHandler() {
        LOGGER.info(" buildHandler method ... ");
        return readSide.<BasketEvent>builder("baskets_offset")
                .setGlobalPrepare(this::createTable)
                .setPrepare(evtTag -> prepareWriteBasket()
                        .thenCombine(prepareDeleteBasket(), (d1, d2) -> Done.getInstance())
                )
                .setEventHandler(BasketCreated.class, this::processPostAdded)
                .setEventHandler(BasketUpdated.class, this::processPostUpdated)
                .setEventHandler(BasketDeleted.class, this::processPostDeleted)
                .build();
    }

    // Execute only once while application is start
    private CompletionStage<Done> createTable() {
        //session.executeCreateTable(
        //        "CREATE TYPE IF NOT EXISTS item ( " +
        //                "uuid TEXT, quantity INT,price INT,PRIMARY KEY(uuid))"
        //);
        return session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS baskets ( " +
                        "uuid TEXT, userUuid TEXT, subTotal INT,tax INT,total INT ,PRIMARY KEY(uuid))"
        ).thenCompose(a -> session.executeCreateTable( "CREATE TABLE IF NOT EXISTS item ( " +
                        "uuid TEXT, basketUuid TEXT ,quantity INT,price INT,PRIMARY KEY(uuid))"));
    }

    /*
    * START: Prepare statement for insert basket values into baskets table.
    * This is just creation of prepared statement, we will map this statement with our event
    */
    private CompletionStage<Done> prepareWriteBasket() {
        return session.prepare(
                "INSERT INTO baskets (uuid, userUuid, subTotal,tax,total) VALUES (?, ?, ?, ?, ?)"
        ).thenApply(ps -> {
            setWriteBaskets(ps);
            return Done.getInstance();
        });
    }

    private CompletionStage<Done> prepareWriteItem() {
        return session.prepare(
                "INSERT INTO item (uuid, basketUuid, quantity, price) VALUES (?, ?, ?, ?)"
        ).thenApply(ps -> {
            setWriteBaskets(ps);
            return Done.getInstance();
        });
    }
	
    private void setWriteBaskets(PreparedStatement statement) {
        this.writeBaskets = statement;
    }

    // Bind prepare statement while BasketCreate event is executed
    private CompletionStage<List<BoundStatement>> processPostAdded(BasketCreated event) {
        BoundStatement bindWriteBasket = writeBaskets.bind();
        bindWriteBasket.setString("uuid", event.getBasket().getUuid());
        bindWriteBasket.setString("userUuid", event.getBasket().getUserUuid());
        bindWriteBasket.setInt("subTotal", event.getBasket().getSubTotal());
		bindWriteBasket.setInt("tax", event.getBasket().getTax());
		bindWriteBasket.setInt("total", event.getBasket().getTotal());
        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteBasket));
    }
    /* ******************* END ****************************/

    /* START: Prepare statement for update the data in baskets table.
    * This is just creation of prepared statement, we will map this statement with our event
    */
    private CompletionStage<List<BoundStatement>> processPostUpdated(BasketUpdated event) {
        BoundStatement bindWriteBasket = writeBaskets.bind();
        bindWriteBasket.setString("uuid", event.getBasket().getUuid());
        bindWriteBasket.setString("userUuid", event.getBasket().getUserUuid());
        bindWriteBasket.setInt("subTotal", event.getBasket().getSubTotal());
		bindWriteBasket.setInt("tax", event.getBasket().getTax());
		bindWriteBasket.setInt("total", event.getBasket().getTotal());
        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteBasket));
    }
    /* ******************* END ****************************/

    /* START: Prepare statement for delete the the basket from table.
    * This is just creation of prepared statement, we will map this statement with our event
    */
    private CompletionStage<Done> prepareDeleteBasket() {
        return session.prepare(
                "DELETE FROM baskets WHERE uuid=?"
        ).thenApply(ps -> {
            setDeleteBaskets(ps);
            return Done.getInstance();
        });
    }

    private void setDeleteBaskets(PreparedStatement deleteBaskets) {
        this.deleteBaskets = deleteBaskets;
    }

    private CompletionStage<List<BoundStatement>> processPostDeleted(BasketDeleted event) {
        BoundStatement bindWriteBasket = deleteBaskets.bind();
        bindWriteBasket.setString("uuid", event.getBasket().getUuid());
        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteBasket));
    }
    /* ******************* END ****************************/
}
