package net.rewerk.dbrest.model.dao.impl;

import net.rewerk.dbrest.model.dao.StaffRoleDao;
import net.rewerk.dbrest.model.entity.StaffRole;

public class StaffRoleDaoImpl extends GenericDaoImpl<StaffRole> implements StaffRoleDao {
    public StaffRoleDaoImpl() {
        super(StaffRole.class);
    }
}
