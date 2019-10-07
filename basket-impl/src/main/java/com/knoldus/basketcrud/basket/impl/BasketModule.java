package com.knoldus.basketcrud.basket.impl;

import com.google.inject.AbstractModule;
import com.knoldus.basketcrud.basket.api.BasketService;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 * Created by harmeet on 30/1/17.
 */
public class BasketModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {
        bindServices(serviceBinding(BasketService.class, BasketServiceImpl.class));
    }
}
