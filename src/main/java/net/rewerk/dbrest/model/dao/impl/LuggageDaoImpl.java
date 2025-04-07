package net.rewerk.dbrest.model.dao.impl;

import net.rewerk.dbrest.model.dao.LuggageDao;
import net.rewerk.dbrest.model.entity.Luggage;

public class LuggageDaoImpl extends GenericDaoImpl<Luggage> implements LuggageDao {
    public LuggageDaoImpl() {
        super(Luggage.class);
    }
}
