package com.fujitsu.manager.viewManager.serviceImpl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.ChannelManagerMapper;
import com.fujitsu.manager.viewManager.service.ChannelManagerService;

public class ChannelManagerServiceImpl extends ChannelManagerService{
	
	@Resource
	private ChannelManagerMapper channelManagerMapper;
	@Override
	public List<Map<String, Object>> getEmsGroup() throws CommonException {
		List<Map<String, Object>> rv = channelManagerMapper.getAllEMSGroup();
		return rv;
	}
	@Override
	public List<Map<String, Object>> getEmsList(int emsGroupId) throws CommonException {
		List<Map<String, Object>> rv = channelManagerMapper.getEMSInGroup(emsGroupId);
		return rv;
	}
	@Override
	public List<Map<String, Object>> getSubnetList(int emsId)
			throws CommonException {
		List<Map<String, Object>> rv = channelManagerMapper.getSubnetInEMS(emsId);
		return rv;
	}

}
