package com.business.platform.kie.container.lifecycle.listener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.kie.server.api.model.Message;
import org.kie.server.api.model.Severity;
import org.kie.server.services.api.KieContainerInstance;
import org.kie.server.services.api.KieServer;
import org.kie.server.services.api.KieServerEventListener;
import org.kie.server.services.impl.KieServerImpl;
import org.mockserver.integration.ClientAndServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.business.platform.mockserver.ports.MockServerManager;

public class KieContainerLifecycleEventListener implements KieServerEventListener {
	
	private Logger logger = LoggerFactory.getLogger(KieContainerLifecycleEventListener.class);
	
	private MockServerManager mockServerManager = MockServerManager.getInstance();
	

	@Override
	public void afterContainerActivated(KieServer arg0, KieContainerInstance arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void afterContainerDeactivated(KieServer arg0, KieContainerInstance arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void afterContainerStarted(KieServer arg0, KieContainerInstance arg1) {
		
		InputStream expectationsFile = arg1.getKieContainer().getClassLoader().getResourceAsStream("expectations.json");
		
		if(expectationsFile != null) {
			ClientAndServer mockServer = mockServerManager.createMockServer(arg1.getContainerId(), expectationsFile);
			
			if(mockServer != null) {								
				List<Message> messageList = arg1.getResource().getMessages();
				messageList.add(
						new Message(
								Severity.INFO, 
								"Mockserver created in port " + mockServer.getPort()));
				
				arg1.getResource().setMessages(messageList);
			}			
		}
	}

	@Override
	public void afterContainerStopped(KieServer arg0, KieContainerInstance arg1) {
		
		boolean mockServerDeleted = mockServerManager.deleteMockServer(arg1.getContainerId());
		
		if(mockServerDeleted) {
			// TODO eliminar mensaje del kie-server relativo al mockserver
		}
	}

	@Override
	public void afterServerStarted(KieServer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterServerStopped(KieServer arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void beforeContainerActivated(KieServer arg0, KieContainerInstance arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeContainerDeactivated(KieServer arg0, KieContainerInstance arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void beforeContainerStarted(KieServer arg0, KieContainerInstance arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void beforeContainerStopped(KieServer arg0, KieContainerInstance arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void beforeServerStarted(KieServer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeServerStopped(KieServer arg0) {
		// TODO Auto-generated method stub
	}	
}