package net.rewerk.dbrest.model.dao.impl;

import net.rewerk.dbrest.model.dao.StaffDao;
import net.rewerk.dbrest.model.entity.Staff;

public class StaffDaoImpl extends GenericDaoImpl<Staff> implements StaffDao {
    public StaffDaoImpl() {
        super(Staff.class);
    }
}
