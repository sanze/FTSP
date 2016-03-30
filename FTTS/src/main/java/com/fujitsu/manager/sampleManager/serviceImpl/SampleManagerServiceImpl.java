package com.fujitsu.manager.sampleManager.serviceImpl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.SampleMapper;
import com.fujitsu.manager.sampleManager.service.SampleManagerService;

@Service
@Transactional(rollbackFor = Exception.class)
public class SampleManagerServiceImpl extends SampleManagerService {
	@Resource
	private SampleMapper sampleMapper;

	@Override
	public List<Map> getSampleData(String tableName) throws CommonException {
		return sampleMapper.getSampleData(tableName);
	}


}
