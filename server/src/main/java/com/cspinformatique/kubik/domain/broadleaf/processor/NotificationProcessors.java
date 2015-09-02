package com.cspinformatique.kubik.domain.broadleaf.processor;

import java.util.HashMap;
import java.util.Map;

import com.cspinformatique.kubik.model.broadleaf.BroadleafNotification;
import com.cspinformatique.kubik.model.broadleaf.BroadleafNotification.Type;

public class NotificationProcessors {
	private Map<Type, NotificationProcessor<? extends BroadleafNotification>> processorsMap;

	public NotificationProcessors() {
		this(new HashMap<>());
	}

	public NotificationProcessors(Map<Type, NotificationProcessor<? extends BroadleafNotification>> processorsMap) {
		this.processorsMap = processorsMap;
	}

	public NotificationProcessors addProcessor(Type type,
			NotificationProcessor<? extends BroadleafNotification> processor) {
		processorsMap.put(type, processor);

		return this;
	}

	public NotificationProcessor<? extends BroadleafNotification> getProcessor(Type type) {
		NotificationProcessor<? extends BroadleafNotification> processor = processorsMap.get(type);

		if (processor == null) {
			throw new RuntimeException("No notification processor found for type " + type.toString() + ".");
		}

		return processor;

	}
}
