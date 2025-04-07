package net.rewerk.dbrest.model.dao.impl;

import net.rewerk.dbrest.model.dao.RouteDao;
import net.rewerk.dbrest.model.entity.Route;

public class RouteDaoImpl extends GenericDaoImpl<Route> implements RouteDao {
    public RouteDaoImpl() {
        super(Route.class);
    }
}
