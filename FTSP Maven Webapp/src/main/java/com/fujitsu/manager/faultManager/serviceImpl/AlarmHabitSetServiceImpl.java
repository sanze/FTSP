package com.fujitsu.manager.faultManager.serviceImpl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.IColorMapper;
import com.fujitsu.manager.faultManager.service.AlarmHabitSetService;
@Service
@Transactional(rollbackFor = Exception.class)
public class AlarmHabitSetServiceImpl extends AlarmHabitSetService {
	@Resource
	private IColorMapper iColorMapper;
	@Override
	public boolean setAlarmColorConfig(List<Map> datas) throws CommonException {
		for(Map m:datas){
			String alarmLevel=(String)m.get("alarmLevel");//获取告警级别
			Map setInfo=iColorMapper.getAlarmSetInfoByLevel(alarmLevel);//获取指定级别的告警设置信息
			if(setInfo==null){//表示库中还没有此告警设置,则插入
				iColorMapper.insertAlarmSetInfo(m);
			}else{//表示库中已存在此告警设置,则更新
				iColorMapper.updateAlarmSetInfo(m);
			}
		}
		return true;
	}
	@Override
	public List<Map> getAlarmColorConfig(Map map) throws CommonException {
		return iColorMapper.getAlarmColorConfig(map);
	}
}
