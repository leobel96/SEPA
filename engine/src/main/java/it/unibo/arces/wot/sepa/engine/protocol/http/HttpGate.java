package it.unibo.arces.wot.sepa.engine.protocol.http;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.apache.http.ExceptionLogger;

import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.apache.http.impl.nio.bootstrap.ServerBootstrap;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.engine.bean.EngineBeans;
import it.unibo.arces.wot.sepa.engine.core.EngineProperties;

import it.unibo.arces.wot.sepa.engine.protocol.http.handler.QueryHandler;
import it.unibo.arces.wot.sepa.engine.protocol.http.handler.UpdateHandler;
import it.unibo.arces.wot.sepa.engine.protocol.http.handler.EchoHandler;
import it.unibo.arces.wot.sepa.engine.scheduling.Scheduler;

public class HttpGate {
	protected static final Logger logger = LogManager.getLogger("HttpGate");

	protected EngineProperties properties;
	protected Scheduler scheduler;

	protected String serverInfo = "SEPA Gate-HTTP/1.1";
	protected HttpServer server = null;
	
	protected IOReactorConfig config = IOReactorConfig.custom().setTcpNoDelay(true).setSoReuseAddress(true).build();
	
	public HttpGate(EngineProperties properties, Scheduler scheduler) throws SEPAProtocolException {
		this.properties = properties;
		this.scheduler = scheduler;
	
		server = ServerBootstrap.bootstrap().setListenerPort(properties.getHttpPort())
				.setServerInfo(serverInfo).setIOReactorConfig(config).setExceptionLogger(ExceptionLogger.STD_ERR)
				.registerHandler(properties.getQueryPath(), new QueryHandler(scheduler))
				.registerHandler(properties.getUpdatePath(), new UpdateHandler(scheduler))
				.registerHandler("/echo", new EchoHandler()).create();
		
		try {
			server.start();
		} catch (IOException e) {
			throw new SEPAProtocolException(e);
		}	
		
		if(server.getEndpoint().getException()!=null) {
			throw new SEPAProtocolException(server.getEndpoint().getException());	
		}
		
		String address = server.getEndpoint().getAddress().toString();
		
		try {
			address = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			throw new SEPAProtocolException(e1);
		}
		EngineBeans.setQueryURL("http://" + address + ":" + properties.getHttpPort()+properties.getQueryPath());
		EngineBeans.setUpdateURL("http://" + address + ":" + properties.getHttpPort()+properties.getUpdatePath());

		System.out.println("SPARQL 1.1 Query     | " + EngineBeans.getQueryURL());
		System.out.println("SPARQL 1.1 Update    | " + EngineBeans.getUpdateURL());
	}
	
	public void shutdown() {
		server.shutdown(5, TimeUnit.SECONDS);
		
		try {
			server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			logger.info(serverInfo+" interrupted: " + e.getMessage());
		}
	}
}
