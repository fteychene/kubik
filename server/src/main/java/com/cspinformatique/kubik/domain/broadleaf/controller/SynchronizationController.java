package com.cspinformatique.kubik.domain.broadleaf.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.cspinformatique.kubik.domain.broadleaf.service.SynchronizationService;

@Controller
public class SynchronizationController {
	@Autowired
	private SynchronizationService synchronizationService;

	@PostConstruct
	public void init() {
		synchronizationService.executeInitialLoad();
	}
}
