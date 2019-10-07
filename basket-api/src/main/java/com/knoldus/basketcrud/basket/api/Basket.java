package com.knoldus.basketcrud.basket.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Builder;
import lombok.Value;
import java.util.List;

/**
 *
 */
@Value
@Builder
@JsonDeserialize
public class Basket implements Jsonable {
    String uuid;
    String userUuid;
    int subTotal;
	int tax;
	int total;
    List<Item> items;
}
