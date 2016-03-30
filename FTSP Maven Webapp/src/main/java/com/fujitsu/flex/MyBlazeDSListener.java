package com.fujitsu.flex;

import java.util.Date;

import flex.messaging.FlexSession;
import flex.messaging.FlexSessionListener;
import flex.messaging.MessageClient;
import flex.messaging.MessageClientListener;
import flex.messaging.client.FlexClient;
import flex.messaging.client.FlexClientListener;
import flex.messaging.config.ConfigMap;
import flex.messaging.services.AbstractBootstrapService;

public class MyBlazeDSListener extends AbstractBootstrapService {

	@Override
	public void initialize(String id, ConfigMap properties) {
		System.out.println("MyBlazeDSListener is initializing..."); 

		// Add the FlexSession created listener. 
		MyFlexSessionListener sessionListener = new MyFlexSessionListener(); 
		FlexSession.addSessionCreatedListener(sessionListener); 

		// Add the FlexClient created listener. 
		MyFlexClientListener flexClientListener = new MyFlexClientListener(); 
		FlexClient.addClientCreatedListener(flexClientListener); 

		// Add the MessageClient created listener. 
		MyMessageClientListener messageClientListener = new MyMessageClientListener(); 
		MessageClient.addMessageClientCreatedListener(messageClientListener); 
	}

	@Override
	public void start() {
		System.out.println("MyBlazeDSListener is start..."); 
		
	}

	@Override
	public void stop() {
		System.out.println("MyBlazeDSListener is stop..."); 
	}
	
	//Flex Session Listener
	class MyFlexSessionListener implements FlexSessionListener {
		public void sessionCreated(FlexSession session) {
			System.out.println("FlexSession created: " + session.getId());
			// Add the FlexSession destroyed listener. 
			session.addSessionDestroyedListener(this);
		}

		public void sessionDestroyed(FlexSession session) {
			System.out.println("FlexSession destroyed: " + session.getId());
		}
	}
	//Flex Client Listener
	class MyFlexClientListener implements FlexClientListener {

		public void clientCreated(FlexClient client) {
			
			System.out.println("FlexClient created: " + client.getId());
			//加入维护
			CustomPdvQueueProcessor.flexClientReceiveTime.put(client.getId(), new Date());
			// Add the FlexClient destroyed listener. 
			client.addClientDestroyedListener(this); 
		}

		public void clientDestroyed(FlexClient client) {
			System.out.println("FlexClient destroyed: " + client.getId());
			//移除
			CustomPdvQueueProcessor.flexClientReceiveTime.remove(client.getId());
		}

	}
	//Message Client Listener
	class MyMessageClientListener implements MessageClientListener {

		public void messageClientCreated(MessageClient messageClient) {
			System.out.println("MessageClient created: "
					+ messageClient.getClientId());
			// Add the MessageClient destroyed listener. 
			messageClient.addMessageClientDestroyedListener(this); 
		}

		public void messageClientDestroyed(MessageClient messageClient) {
			System.out.println("MessageClient destroyed: "
					+ messageClient.getClientId());
		}
	}
	

}
