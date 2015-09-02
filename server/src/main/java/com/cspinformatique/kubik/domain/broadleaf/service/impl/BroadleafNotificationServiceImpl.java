package com.cspinformatique.kubik.domain.broadleaf.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.cspinformatique.kubik.domain.broadleaf.processor.NotificationProcessors;
import com.cspinformatique.kubik.domain.broadleaf.repository.BroadleafNotificationRepository;
import com.cspinformatique.kubik.domain.broadleaf.service.BroadleafNotificationService;
import com.cspinformatique.kubik.model.broadleaf.BroadleafNotification;
import com.cspinformatique.kubik.model.broadleaf.BroadleafNotification.Status;

@Service
public class BroadleafNotificationServiceImpl implements BroadleafNotificationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(BroadleafNotificationServiceImpl.class);

	@Autowired
	private NotificationProcessors notificationProcessors;

	@Autowired
	private BroadleafNotificationRepository broadleafNotificationRepository;

	@Value("{broadleaf.url}")
	private String broadleafUrl;

	@Value("${broadleaf.activated}")
	private boolean activated;

	@Override
	public Page<BroadleafNotification> findAll(Pageable pageable) {
		return broadleafNotificationRepository.findAll(pageable);
	}

	@Override
	public BroadleafNotification findOne(int id) {
		return broadleafNotificationRepository.findOne(id);
	}

	@Override
	public List<BroadleafNotification> findByStatus(Status status) {
		return broadleafNotificationRepository.findByStatus(status);
	}

	@Override
	public List<BroadleafNotification> findByStatus(Status status, Sort sort) {
		return broadleafNotificationRepository.findByStatus(status, sort);
	}

	@Override
	@Transactional
	public void process(BroadleafNotification broadleafNotification) {
		try {
			broadleafNotification.setStatus(notificationProcessors.getProcessor(broadleafNotification.getType())
					.processNotification(broadleafNotification));
		} catch (Exception ex) {
			LOGGER.error("Error while processing broadleaf notification " + broadleafNotification.getId() + ".", ex);

			broadleafNotification.setError(ExceptionUtils.getStackTrace(ex));

			broadleafNotification.setStatus(Status.ERROR);
		} finally {
			save(broadleafNotification);
		}
	}

	@Override
	public BroadleafNotification save(BroadleafNotification broadleafNotification) {
		if (activated) {
			if (broadleafNotification.getCreationDate() == null) {
				broadleafNotification.setCreationDate(new Date());
			}

			if (broadleafNotification.getStatus().equals(Status.PROCESSED)
					&& broadleafNotification.getProcessedDate() == null) {
				broadleafNotification.setProcessedDate(new Date());
			}

			if (broadleafNotification.getStatus().equals(Status.ERROR)
					&& broadleafNotification.getErrorDate() == null) {
				broadleafNotification.setErrorDate(new Date());
			}

			return broadleafNotificationRepository.save(broadleafNotification);
		} else {
			return broadleafNotification;
		}
	}
}
