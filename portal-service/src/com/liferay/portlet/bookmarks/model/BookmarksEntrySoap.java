/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.bookmarks.model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used by SOAP remote services, specifically {@link com.liferay.portlet.bookmarks.service.http.BookmarksEntryServiceSoap}.
 *
 * @author    Brian Wing Shun Chan
 * @see       com.liferay.portlet.bookmarks.service.http.BookmarksEntryServiceSoap
 * @generated
 */
public class BookmarksEntrySoap implements Serializable {
	public static BookmarksEntrySoap toSoapModel(BookmarksEntry model) {
		BookmarksEntrySoap soapModel = new BookmarksEntrySoap();

		soapModel.setUuid(model.getUuid());
		soapModel.setEntryId(model.getEntryId());
		soapModel.setGroupId(model.getGroupId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setResourceBlockId(model.getResourceBlockId());
		soapModel.setFolderId(model.getFolderId());
		soapModel.setName(model.getName());
		soapModel.setUrl(model.getUrl());
		soapModel.setDescription(model.getDescription());
		soapModel.setVisits(model.getVisits());
		soapModel.setPriority(model.getPriority());

		return soapModel;
	}

	public static BookmarksEntrySoap[] toSoapModels(BookmarksEntry[] models) {
		BookmarksEntrySoap[] soapModels = new BookmarksEntrySoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static BookmarksEntrySoap[][] toSoapModels(BookmarksEntry[][] models) {
		BookmarksEntrySoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new BookmarksEntrySoap[models.length][models[0].length];
		}
		else {
			soapModels = new BookmarksEntrySoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static BookmarksEntrySoap[] toSoapModels(List<BookmarksEntry> models) {
		List<BookmarksEntrySoap> soapModels = new ArrayList<BookmarksEntrySoap>(models.size());

		for (BookmarksEntry model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new BookmarksEntrySoap[soapModels.size()]);
	}

	public BookmarksEntrySoap() {
	}

	public long getPrimaryKey() {
		return _entryId;
	}

	public void setPrimaryKey(long pk) {
		setEntryId(pk);
	}

	public String getUuid() {
		return _uuid;
	}

	public void setUuid(String uuid) {
		_uuid = uuid;
	}

	public long getEntryId() {
		return _entryId;
	}

	public void setEntryId(long entryId) {
		_entryId = entryId;
	}

	public long getGroupId() {
		return _groupId;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public long getUserId() {
		return _userId;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public String getUserName() {
		return _userName;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;
	}

	public long getResourceBlockId() {
		return _resourceBlockId;
	}

	public void setResourceBlockId(long resourceBlockId) {
		_resourceBlockId = resourceBlockId;
	}

	public long getFolderId() {
		return _folderId;
	}

	public void setFolderId(long folderId) {
		_folderId = folderId;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getUrl() {
		return _url;
	}

	public void setUrl(String url) {
		_url = url;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public int getVisits() {
		return _visits;
	}

	public void setVisits(int visits) {
		_visits = visits;
	}

	public int getPriority() {
		return _priority;
	}

	public void setPriority(int priority) {
		_priority = priority;
	}

	private String _uuid;
	private long _entryId;
	private long _groupId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private long _resourceBlockId;
	private long _folderId;
	private String _name;
	private String _url;
	private String _description;
	private int _visits;
	private int _priority;
}