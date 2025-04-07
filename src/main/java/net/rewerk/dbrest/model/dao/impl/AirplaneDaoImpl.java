package net.rewerk.dbrest.model.dao.impl;

import net.rewerk.dbrest.model.dao.AirplaneDao;
import net.rewerk.dbrest.model.entity.Airplane;

public class AirplaneDaoImpl extends GenericDaoImpl<Airplane> implements AirplaneDao {
    public AirplaneDaoImpl() {
        super(Airplane.class);
    }
}
