package com.cspinformatique.kubik.model.broadleaf;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Lob;

@Entity
@Inheritance
public abstract class BroadleafNotification {
	public enum Type {
		PRODUCT, CATEGORY
	}
	
	public enum Action{
		UPDATE, DELETE
	}

	public enum Status {
		TO_PROCESS, PROCESSED, ERROR, SKIPPED
	}

	private int id;
	private Status status;
	private Type type;
	private Action action;
	private Date creationDate;
	private Date processedDate;
	private Date errorDate;
	private String error;

	public BroadleafNotification() {

	}

	public BroadleafNotification(int id, Status status, Type type, Action action, Date creationDate, Date processedDate,
			Date errorDate, String error) {
		this.id = id;
		this.status = status;
		this.type = type;
		this.action = action;
		this.creationDate = creationDate;
		this.processedDate = processedDate;
		this.errorDate = errorDate;
		this.error = error;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Enumerated(EnumType.STRING)
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Enumerated(EnumType.STRING)
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Enumerated(EnumType.STRING)
	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getProcessedDate() {
		return processedDate;
	}

	public void setProcessedDate(Date processedDate) {
		this.processedDate = processedDate;
	}

	public Date getErrorDate() {
		return errorDate;
	}

	public void setErrorDate(Date errorDate) {
		this.errorDate = errorDate;
	}

	@Lob
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
