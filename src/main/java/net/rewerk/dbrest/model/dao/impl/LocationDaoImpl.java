package net.rewerk.dbrest.model.dao.impl;

import net.rewerk.dbrest.model.dao.LocationDao;
import net.rewerk.dbrest.model.entity.Location;

public class LocationDaoImpl extends GenericDaoImpl<Location> implements LocationDao {
    public LocationDaoImpl() {
        super(Location.class);
    }
}
