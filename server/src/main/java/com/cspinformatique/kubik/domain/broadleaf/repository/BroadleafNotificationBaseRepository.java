package com.cspinformatique.kubik.domain.broadleaf.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.cspinformatique.kubik.model.broadleaf.BroadleafNotification;
import com.cspinformatique.kubik.model.broadleaf.BroadleafNotification.Status;

@NoRepositoryBean
public interface BroadleafNotificationBaseRepository<T extends BroadleafNotification>
		extends JpaRepository<T, Integer> {
	List<T> findByStatus(Status status);
	
	List<T> findByStatus(Status status, Sort sort);
}
