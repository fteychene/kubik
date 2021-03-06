package com.cspinformatique.kubik.server.domain.kos.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cspinformatique.kubik.server.model.kos.KosNotification;
import com.cspinformatique.kubik.server.model.kos.KosNotification.Status;

public interface KosNotificationRepository extends JpaRepository<KosNotification, Integer> {
	List<KosNotification> findByStatus(Status status, Sort sort);
}
