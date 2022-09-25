package com.daoutech.api;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartListhener implements ApplicationListener<ApplicationStartedEvent> {
	
	@Value("${app.statistics-dir-path}")
	private String statisticsDirPath;

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		File f = new File(statisticsDirPath);
		if(!f.isDirectory()) {
			f.mkdirs();
		}
	}
	
}
