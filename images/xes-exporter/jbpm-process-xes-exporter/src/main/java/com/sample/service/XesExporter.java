package com.sample.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.jbpm.xes.XESExportServiceImpl;
import org.jbpm.xes.XESProcessFilter;
import org.jbpm.xes.dataset.DataSetService;
import org.jbpm.xes.dataset.DataSetServiceImpl;

public class XesExporter {

	public static String getXes (String processId, String processVersion, String[] processStatus, String logType, String nodeTypes) {
		String xml = "";

		try (BasicDataSource ds = new BasicDataSource()) {

			System.out.println("Testing database connection with driver " + System.getenv("database_driver") + " and url " + System.getenv("database_url"));
			
			ds.setDriverClassName(System.getenv("database_driver"));
			ds.setUrl(System.getenv("database_url"));
			ds.setUsername(System.getenv("database_user"));
			ds.setPassword(System.getenv("database_password"));
			ds.setDefaultReadOnly(true);
			ds.getConnection().close();

			System.out.println("Database connection successfully tested");
			
			DataSetService dataSetService = new DataSetServiceImpl(() -> ds);
			XESExportServiceImpl service = new XESExportServiceImpl();
			service.setDataSetService(dataSetService);
			final XESProcessFilter.Builder filter = XESProcessFilter.builder();
			filter.withProcessId(processId);
			if (processVersion != null) {
				filter.withProcessVersion(processVersion);
			}
			if (processStatus != null) {
				List<Integer> status = new ArrayList<>();
				for (String statusLine : processStatus) {
					try {
						final Integer pStatus = Integer.valueOf(statusLine);
						//only add valid status
						if (pStatus >= 0 && pStatus <= 4) {
							status.add(pStatus);
						}
					} catch (NumberFormatException ex) {
						System.err.println("Invalid process status number for input: " + statusLine + ", valid status are number between 0 and 4.");
					}
				}
				if (status.isEmpty() == false) {
					filter.withStatus(status);
				}
			}
			if (logType != null) {
				filter.withNodeInstanceLogType(Integer.valueOf(logType));
			}
			if (nodeTypes != null) {
				filter.withAllNodeTypes();
			}
			
			System.out.println("Exporting process " + processId + " info in xes format...");
			xml = service.export(filter.build());
			System.out.println("Export process successfully finished");
			
		} catch (ParseException exp) {
			System.err.println("Parsing options failed. Reason: " + exp.getMessage());
			exp.printStackTrace();
		} catch (Exception ex) {
			System.err.println("Failed to execute export due to: " + ex.getMessage());
			ex.printStackTrace();
		}
		
		return xml;
	}
}