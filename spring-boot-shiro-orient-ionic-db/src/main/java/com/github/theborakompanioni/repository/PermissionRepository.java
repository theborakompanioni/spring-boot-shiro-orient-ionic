package com.github.theborakompanioni.repository;

import com.github.theborakompanioni.model.Permission;
import org.springframework.data.orient.object.repository.OrientObjectRepository;

/**
 * DAO for {@link Permission}.
 */
public interface PermissionRepository extends OrientObjectRepository<Permission> {

}
