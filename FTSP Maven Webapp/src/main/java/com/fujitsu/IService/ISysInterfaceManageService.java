package com.fujitsu.IService;

import java.util.Map;

import com.fujitsu.common.CommonException;

public interface ISysInterfaceManageService {

	Map<String, Object> getSysInterfaceManageData(int start, int limit) throws CommonException;

	void addInterface(Map<String, Object> paramMap) throws CommonException;

	void modifyInterface(Map<String, Object> paramMap) throws CommonException;

	void deleteInterface(Map<String, Object> paramMap) throws CommonException;

	Map<String, Object> getDetailById(Map<String, Object> paramMap) throws CommonException;

	int checkConnection(Map<String, Object> map);

/*	void AlarmSend(Map<String,Object> alrm);*/
	
	String getCurrentAlarms(Map<String,Object> map);

	Map<String, Object> checkInterface(Map<String, Object> paramMap) throws CommonException;
}
