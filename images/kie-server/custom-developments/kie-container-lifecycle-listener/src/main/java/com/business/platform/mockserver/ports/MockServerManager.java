package com.business.platform.mockserver.ports;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.mockserver.integration.ClientAndServer;
import org.mockserver.mock.Expectation;
import org.mockserver.serialization.ExpectationSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.business.platform.kie.container.lifecycle.listener.KieContainerLifecycleEventListener;

public class MockServerManager {
	
	private Logger logger = LoggerFactory.getLogger(KieContainerLifecycleEventListener.class);
	
	private static final Integer MIN_ALLOWED_PORT = 1081;
	private static MockServerManager mockServerManager;
	private TreeSet<Integer> usedPorts = new TreeSet<Integer>();
	private Map<String, ClientAndServer> mockServerRegistry = new HashMap<String, ClientAndServer>();
	
	private MockServerManager () {
		usedPorts = new TreeSet<Integer>();
	}
	
	public static MockServerManager getInstance() {
		if(mockServerManager == null) {
			mockServerManager = new MockServerManager();
			mockServerManager.mockServerRegistry = new HashMap<String, ClientAndServer>();
		}
		
		return mockServerManager;
	}
	
	public ClientAndServer createMockServer(String containerId, InputStream expectationsFile) {
		
		if(mockServerManager != null) {
			Integer nextFreePort = MIN_ALLOWED_PORT;		
			Integer lastPortInUse = MIN_ALLOWED_PORT;
				
			logger.debug("Buscando puerto candidato para kie container " + containerId);
			
			if(usedPorts.size() > 0) {
				
				for (Iterator<Integer> usedPortsIterator = usedPorts.iterator(); usedPortsIterator.hasNext();) {
					Integer currentPortIteration = (Integer) usedPortsIterator.next();
					
					logger.debug("Descartando puerto en uso " + currentPortIteration);
					
					if(currentPortIteration - lastPortInUse > 1) {
						
						nextFreePort = (MIN_ALLOWED_PORT + (currentPortIteration - lastPortInUse) - 1);
						break;
						
					} else {
						lastPortInUse = currentPortIteration;
					}
				}
				
				if(nextFreePort.equals(MIN_ALLOWED_PORT))
					nextFreePort = lastPortInUse + 1;
			}
			else {
				logger.debug("Lista de puertos actualmente vacía");
			}
						
			logger.info(" --------------------------------------------------------------------------------");
			logger.info("Creando mockserver...");
			
			usedPorts.add(nextFreePort);
			ClientAndServer mockServer = ClientAndServer.startClientAndServer(nextFreePort);
			
			String expectationsString = new BufferedReader(
					new InputStreamReader(expectationsFile, StandardCharsets.UTF_8))
					.lines()
					.collect(Collectors.joining("\n"));
			
			ExpectationSerializer expectationsSerializer = new ExpectationSerializer(null);
			Expectation[] expectationArray = expectationsSerializer.deserializeArray(expectationsString, true);
			mockServer.upsert(expectationArray);
			mockServerManager.mockServerRegistry.put(containerId, mockServer);
			
			logger.info("Mockserver creado correctamente");
			logger.info("Kie-container: " + containerId);
			logger.info("Puerto mockserver disponible: " + mockServer.getPort());
			logger.info(" --------------------------------------------------------------------------------");
			
			return mockServer; 
		}
		
		return null;
	}
	
	public boolean deleteMockServer(String containerId) {
		if(mockServerManager != null) {
			ClientAndServer mockServer = mockServerManager.mockServerRegistry.get(containerId);
			
			if(mockServer != null) {
				logger.info(" --------------------------------------------------------------------------------");
				logger.info("Eliminando mockserver en puerto " + mockServer.getPort());
				
				mockServer.stop();
				mockServerManager.usedPorts.remove(mockServer.getPort());
				mockServerManager.mockServerRegistry.remove(containerId);
				
				logger.info("Mockserver eliminado en puerto " + mockServer.getPort());
				logger.info(" --------------------------------------------------------------------------------");
				
				return true;
			}
		}
		
		return false;
	}
}
