package com.fujitsu.flex;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fujitsu.util.CommonUtil;

import flex.messaging.MessageClient;
import flex.messaging.client.FlexClientOutboundQueueProcessor;
import flex.messaging.client.FlushResult;
import flex.messaging.messages.Message;
import flex.messaging.services.messaging.ThrottleManager.ThrottleResult;
import flex.messaging.services.messaging.ThrottleManager.ThrottleResult.Result;

public class CustomPdvQueueProcessor extends FlexClientOutboundQueueProcessor {
	//原始代码
/*	public void add(List<Message> outboundQueue, Message message)
    {
        outboundQueue.add(message);
    }
	public FlushResult flush(List<Message> outboundQueue)
    {
        return flush(null  no client distinction , outboundQueue);
    }
	public FlushResult flush(MessageClient messageClient, List<Message> outboundQueue)
    {
        FlushResult flushResult = new FlushResult();
        List<Message> messagesToFlush = null;

        for (Iterator<Message> iter = outboundQueue.iterator(); iter.hasNext();)
        {
            Message message = iter.next();
            if (messageClient == null || (message.getClientId().equals(messageClient.getClientId())))
            {
                if (isMessageExpired(message)) // Don't flush expired messages.
                {
                    iter.remove();
                    continue;
                }

                messageClient = messageClient == null? getMessageClient(message) : messageClient;

                // First, apply the destination level outbound throttling.
                ThrottleResult throttleResult = throttleOutgoingDestinationLevel(messageClient, message, false);
                Result result = throttleResult.getResult();

                // No destination level throttling; check destination-client level throttling.
                if (Result.OK == result)
                {
                    throttleResult = throttleOutgoingClientLevel(messageClient, message, false);
                    result = throttleResult.getResult();
                    // If no throttling, simply add the message to the list.
                    if (Result.OK == result)
                    {
                        updateMessageFrequencyOutgoing(messageClient, message);
                        if (messagesToFlush == null)
                            messagesToFlush = new ArrayList<Message>();
                        messagesToFlush.add(message);
                    }
                    // In rest of the policies (which is NONE), simply don't
                    // add the message to the list.
                }
                iter.remove();
            }
        }

        flushResult.setMessages(messagesToFlush);
        return flushResult;
    }*/
	
	@Override
	public void add(List outboundQueue, Message message)
    {
		String flexClientId = getMessageClient(message).getFlexClient().getId();
		//检查客户端是否过期
		if(isFlexClientClosed(flexClientId)){
			//注销flexClient
			getMessageClient(message).getFlexClient().invalidate();
		}else{
		//使用SoftReference
		outboundQueue.add(new SoftReference<Message>(message));
    }
		//使用SoftReference
//		outboundQueue.add(new SoftReference<Message>(message));
//		System.out.println("队列长度："+outboundQueue.size());
//		System.out.println("flexClientId："+getMessageClient(message).getFlexClient().getId());
//		System.out.println("flexClient是否可用："+getMessageClient(message).getFlexClient().isValid());
//		System.out.println("客户端通道是否断开："+getMessageClient(message).isClientChannelDisconnected());
//		System.out.println("flexClientTimeoutPeriod："+getMessageClient(message).getFlexClient().getTimeoutPeriod());
//		getMessageClient(message).getFlexClient().invalidate();
    }
	
	@Override
	public FlushResult flush(List outboundQueue)
    {
        return flush(null /* no client distinction */, outboundQueue);
    }
	
	@Override
	public FlushResult flush(MessageClient messageClient, List outboundQueue)
    {
        FlushResult flushResult = new FlushResult();
        
        ArrayList<SoftReference<Message>> messages = (ArrayList<SoftReference<Message>>) outboundQueue;

        List<Message> messagesToFlush = null;

        for (Iterator<SoftReference<Message>> iter = messages.iterator(); iter.hasNext();)
        {
            Message message = iter.next().get();
            if (messageClient == null || (message.getClientId().equals(messageClient.getClientId())))
            {
                if (isMessageExpired(message)) // Don't flush expired messages.
                {
                    iter.remove();
                    continue;
                }

                messageClient = messageClient == null? getMessageClient(message) : messageClient;

                //更新flexClientReceiveTime
                String flexClientId = messageClient.getFlexClient().getId();
                flexClientReceiveTime.put(flexClientId, new Date());
                
                // First, apply the destination level outbound throttling.
                ThrottleResult throttleResult = throttleOutgoingDestinationLevel(messageClient, message, false);
                Result result = throttleResult.getResult();

                // No destination level throttling; check destination-client level throttling.
                if (Result.OK == result)
                {
                    throttleResult = throttleOutgoingClientLevel(messageClient, message, false);
                    result = throttleResult.getResult();
                    // If no throttling, simply add the message to the list.
                    if (Result.OK == result)
                    {
                        updateMessageFrequencyOutgoing(messageClient, message);
                        if (messagesToFlush == null)
                            messagesToFlush = new ArrayList<Message>();
                        messagesToFlush.add(message);
                    }
                    // In rest of the policies (which is NONE), simply don't
                    // add the message to the list.
                }
                iter.remove();
            }
        }

        flushResult.setMessages(messagesToFlush);
        
        outboundQueue.clear(); 
        
        return flushResult;
    }

	public static Map<String,Date> flexClientReceiveTime = new HashMap<String,Date>();
	
	//超时时间 单位：分钟 默认5分钟
	public static int CHECK_TIME_OUT = CommonUtil
			.getSystemConfigProperty("flexClientTimeOut") == null ? 5 : Integer
			.valueOf(CommonUtil.getSystemConfigProperty("flexClientTimeOut"));
	
	//检测flexClient是否过期
	private boolean isFlexClientClosed(String flexClientId){
		boolean result = false;
		Date date = flexClientReceiveTime.get(flexClientId);
		//计算加入超时后时间
		date = CommonUtil.getSpecifiedDay(date, 0, CHECK_TIME_OUT);
		// 已超时后处理流程
		if (date.before(new Date())) {
			result = true;
		}else{
			
		}
		return result;
	}

}
