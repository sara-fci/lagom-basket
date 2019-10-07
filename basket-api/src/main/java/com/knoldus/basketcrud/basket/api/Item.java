package com.knoldus.basketcrud.basket.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Builder;
import lombok.Value;

/**
 * Item class
 */
@Value
@Builder
@JsonDeserialize
public class Item implements Jsonable {
    String uuid;
    int quantity;
	int price;
	String basketUuid;
}
