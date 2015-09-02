package com.cspinformatique.kubik.domain.broadleaf.processor;

import org.springframework.beans.factory.annotation.Autowired;

import com.cspinformatique.broadleaf.client.BroadleafTemplate;
import com.cspinformatique.kubik.domain.broadleaf.repository.BroadleafNotificationBaseRepository;
import com.cspinformatique.kubik.model.broadleaf.BroadleafNotification;
import com.cspinformatique.kubik.model.broadleaf.BroadleafNotification.Status;

public abstract class NotificationProcessor<T extends BroadleafNotification> {
	@Autowired protected BroadleafTemplate broadleafTemplate;
	
	public Status processNotification(BroadleafNotification broadleafNotification){
		return this.process(getRepository().findOne(broadleafNotification.getId()));
	}
	
	protected abstract Status process(T broadleafNotification);
	
	protected abstract BroadleafNotificationBaseRepository<T> getRepository();
}
